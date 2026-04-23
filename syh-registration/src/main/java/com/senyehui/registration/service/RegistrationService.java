package com.senyehui.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.event.entity.Event;
import com.senyehui.event.entity.EventGroup;
import com.senyehui.event.enums.EventStatusEnum;
import com.senyehui.event.mapper.EventMapper;
import com.senyehui.event.mapper.EventGroupMapper;
import com.senyehui.registration.dto.RegisterRequest;
import com.senyehui.registration.entity.Registration;
import com.senyehui.registration.mapper.RegistrationMapper;
import com.senyehui.registration.quota.QuotaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 报名管理服务
 * 核心：使用 Redisson 分布式锁 + Redis 名额原子扣减，防止超卖
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final EventMapper eventMapper;
    private final EventGroupMapper eventGroupMapper;
    private final QuotaService quotaService;
    private final RedissonClient redissonClient;

    private static final String LOCK_KEY_PREFIX = "syh:reg:lock:event:";
    private static final AtomicLong REG_NO_SEQ = new AtomicLong(0);

    /**
     * 报名（核心方法 — 分布式锁 + 名额控制）
     */
    @Transactional(rollbackFor = Exception.class)
    public Registration register(RegisterRequest req, Long userId) {
        Long eventId = req.getEventId();
        Long groupId = req.getGroupId();

        // 构建锁 key（按赛事+组别粒度上锁）
        String lockKey = LOCK_KEY_PREFIX + eventId;
        if (groupId != null) {
            lockKey += ":group:" + groupId;
        }

        RLock lock = redissonClient.getLock(lockKey);
        try {
            // 尝试获取分布式锁（等待 3 秒，锁自动释放 5 秒）
            if (!lock.tryLock(3, 5, TimeUnit.SECONDS)) {
                throw new BusinessException("系统繁忙，请稍后重试");
            }

            // 1. 校验赛事状态和报名时间窗口
            Event event = validateEventForRegistration(eventId);

            // 2. 校验是否重复报名
            checkDuplicateRegistration(eventId, userId);

            // 3. 扣减名额（Redis 原子操作）
            quotaService.deductEventQuota(eventId, event.getMaxParticipants());

            // 4. 如果有组别，扣减组别名额
            EventGroup group = null;
            if (groupId != null) {
                group = eventGroupMapper.selectById(groupId);
                if (group == null) {
                    quotaService.rollbackEventQuota(eventId);
                    throw new BusinessException("组别不存在");
                }
                try {
                    quotaService.deductGroupQuota(groupId, group.getMaxParticipants());
                } catch (BusinessException e) {
                    // 组别名额满，回滚赛事名额
                    quotaService.rollbackEventQuota(eventId);
                    throw e;
                }
            }

            // 5. 创建报名记录
            Registration registration = new Registration();
            registration.setEventId(eventId);
            registration.setGroupId(groupId);
            registration.setUserId(userId);
            registration.setRegNo(generateRegNo(eventId));
            registration.setName(req.getName());
            registration.setPhone(req.getPhone());
            registration.setIdCard(req.getIdCard());
            registration.setAmount(group != null ? group.getPrice() : event.getPrice());
            if (registration.getAmount() == null) {
                registration.setAmount(BigDecimal.ZERO);
            }
            // 免费赛事自动通过
            if (registration.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                registration.setPayStatus(1); // 无需支付
                registration.setStatus(1);     // 自动通过
            } else {
                registration.setPayStatus(0);  // 待支付
                registration.setStatus(0);     // 待审核
            }
            registration.setCheckInStatus(0);
            registrationMapper.insert(registration);

            // 6. 更新赛事报名人数 (DB)
            event.setCurrentParticipants(event.getCurrentParticipants() + 1);
            eventMapper.updateById(event);

            if (group != null) {
                group.setCurrentParticipants(group.getCurrentParticipants() + 1);
                eventGroupMapper.updateById(group);
            }

            log.info("报名成功: regNo={}, eventId={}, userId={}", registration.getRegNo(), eventId, userId);
            return registration;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("报名操作被中断");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 取消报名
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelRegistration(Long registrationId) {
        Registration reg = registrationMapper.selectById(registrationId);
        if (reg == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (reg.getStatus() == 3) {
            throw new BusinessException("报名已取消，无需重复操作");
        }

        reg.setStatus(3); // 已取消
        registrationMapper.updateById(reg);

        // 回滚名额
        quotaService.rollbackEventQuota(reg.getEventId());
        if (reg.getGroupId() != null) {
            quotaService.rollbackGroupQuota(reg.getGroupId());
        }

        // 更新赛事/组别报名人数
        Event event = eventMapper.selectById(reg.getEventId());
        if (event != null && event.getCurrentParticipants() > 0) {
            event.setCurrentParticipants(event.getCurrentParticipants() - 1);
            eventMapper.updateById(event);
        }
        if (reg.getGroupId() != null) {
            EventGroup group = eventGroupMapper.selectById(reg.getGroupId());
            if (group != null && group.getCurrentParticipants() > 0) {
                group.setCurrentParticipants(group.getCurrentParticipants() - 1);
                eventGroupMapper.updateById(group);
            }
        }

        log.info("取消报名: id={}, regNo={}", registrationId, reg.getRegNo());
    }

    /**
     * 审核报名
     */
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long registrationId, int status) {
        if (status != 1 && status != 2) {
            throw new BusinessException("审核状态只能是 1(通过) 或 2(拒绝)");
        }
        Registration reg = registrationMapper.selectById(registrationId);
        if (reg == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (reg.getStatus() != 0) {
            throw new BusinessException("只有待审核状态的报名才能审核");
        }
        reg.setStatus(status);
        registrationMapper.updateById(reg);

        // 如果拒绝，回滚名额
        if (status == 2) {
            quotaService.rollbackEventQuota(reg.getEventId());
            if (reg.getGroupId() != null) {
                quotaService.rollbackGroupQuota(reg.getGroupId());
            }
        }

        log.info("审核报名: id={}, status={}", registrationId, status == 1 ? "通过" : "拒绝");
    }

    /**
     * 签到
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkIn(Long registrationId) {
        Registration reg = registrationMapper.selectById(registrationId);
        if (reg == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (reg.getStatus() != 1) {
            throw new BusinessException("只有审核通过的报名才能签到");
        }
        if (reg.getCheckInStatus() == 1) {
            throw new BusinessException("已签到，无需重复操作");
        }
        reg.setCheckInStatus(1);
        reg.setCheckInTime(LocalDateTime.now());
        registrationMapper.updateById(reg);
        log.info("签到成功: id={}, regNo={}", registrationId, reg.getRegNo());
    }

    /**
     * 分页查询报名列表
     */
    public PageResult<Registration> page(PageQuery pageQuery, Long eventId, Long groupId, Integer status) {
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        if (eventId != null) {
            wrapper.eq(Registration::getEventId, eventId);
        }
        if (groupId != null) {
            wrapper.eq(Registration::getGroupId, groupId);
        }
        if (status != null) {
            wrapper.eq(Registration::getStatus, status);
        }
        wrapper.orderByDesc(Registration::getCreateTime);

        IPage<Registration> page = registrationMapper.selectPage(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * C端查询用户的报名记录
     */
    public PageResult<Registration> pageByUser(PageQuery pageQuery, Long userId) {
        LambdaQueryWrapper<Registration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Registration::getUserId, userId);
        wrapper.orderByDesc(Registration::getCreateTime);

        IPage<Registration> page = registrationMapper.selectPage(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * 获取报名详情
     */
    public Registration getById(Long id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            throw new BusinessException("报名记录不存在");
        }
        return reg;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验赛事是否允许报名
     */
    private Event validateEventForRegistration(Long eventId) {
        Event event = eventMapper.selectById(eventId);
        if (event == null) {
            throw new BusinessException("赛事不存在");
        }
        // 校验赛事状态
        if (event.getStatus() != EventStatusEnum.REGISTRATION_OPEN.getCode()
                && event.getStatus() != EventStatusEnum.PUBLISHED.getCode()) {
            throw new BusinessException("该赛事当前不接受报名");
        }
        // 校验报名时间窗口
        LocalDateTime now = LocalDateTime.now();
        if (event.getRegStartTime() != null && now.isBefore(event.getRegStartTime())) {
            throw new BusinessException("报名尚未开始");
        }
        if (event.getRegEndTime() != null && now.isAfter(event.getRegEndTime())) {
            throw new BusinessException("报名已截止");
        }
        return event;
    }

    /**
     * 检查重复报名
     */
    private void checkDuplicateRegistration(Long eventId, Long userId) {
        if (userId == null) {
            return;
        }
        Long count = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getEventId, eventId)
                        .eq(Registration::getUserId, userId)
                        .ne(Registration::getStatus, 3) // 排除已取消的
        );
        if (count > 0) {
            throw new BusinessException("您已报名该赛事，请勿重复报名");
        }
    }

    /**
     * 生成报名编号: REG + 日期 + 赛事ID后4位 + 序号
     */
    private String generateRegNo(Long eventId) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String eventSuffix = String.format("%04d", eventId % 10000);
        long seq = REG_NO_SEQ.incrementAndGet() % 10000;
        return "REG" + date + eventSuffix + String.format("%04d", seq);
    }
}

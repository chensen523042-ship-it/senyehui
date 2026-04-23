package com.senyehui.event.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.event.entity.Event;
import com.senyehui.event.enums.EventStatusEnum;
import com.senyehui.event.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 赛事活动服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventMapper eventMapper;

    /**
     * 分页查询赛事列表
     */
    public PageResult<Event> page(PageQuery pageQuery, String keyword, Integer status, Integer eventType) {
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Event::getTitle, keyword)
                    .or().like(Event::getLocation, keyword);
        }
        if (status != null) {
            wrapper.eq(Event::getStatus, status);
        }
        if (eventType != null) {
            wrapper.eq(Event::getEventType, eventType);
        }
        wrapper.orderByDesc(Event::getSortOrder, Event::getCreateTime);

        IPage<Event> page = eventMapper.selectPage(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * C端公开赛事分页（只返回已发布/报名中的赛事）
     */
    public PageResult<Event> publicPage(PageQuery pageQuery, String keyword, Integer eventType) {
        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        // 只展示 已发布(2) 和 报名中(3)
        wrapper.in(Event::getStatus,
                EventStatusEnum.PUBLISHED.getCode(),
                EventStatusEnum.REGISTRATION_OPEN.getCode());
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Event::getTitle, keyword)
                    .or().like(Event::getLocation, keyword));
        }
        if (eventType != null) {
            wrapper.eq(Event::getEventType, eventType);
        }
        wrapper.orderByDesc(Event::getSortOrder, Event::getCreateTime);

        IPage<Event> page = eventMapper.selectPage(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * 获取赛事详情
     */
    public Event getById(Long id) {
        Event event = eventMapper.selectById(id);
        if (event == null) {
            throw new BusinessException("赛事不存在");
        }
        return event;
    }

    /**
     * 创建赛事
     */
    @Transactional(rollbackFor = Exception.class)
    public Event create(Event event) {
        event.setStatus(EventStatusEnum.DRAFT.getCode());
        event.setCurrentParticipants(0);
        event.setViewCount(0);
        if (event.getMaxParticipants() == null) {
            event.setMaxParticipants(0);
        }
        if (event.getSortOrder() == null) {
            event.setSortOrder(0);
        }
        eventMapper.insert(event);
        log.info("创建赛事: id={}, title={}", event.getId(), event.getTitle());
        return event;
    }

    /**
     * 更新赛事信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Event event) {
        Event existing = getById(event.getId());
        // 已发布/进行中的赛事不能修改关键信息
        if (existing.getStatus() >= EventStatusEnum.REGISTRATION_OPEN.getCode()
                && existing.getStatus() < EventStatusEnum.ENDED.getCode()) {
            // 只允许修改部分字段
            existing.setDescription(event.getDescription());
            existing.setCoverImage(event.getCoverImage());
            existing.setSortOrder(event.getSortOrder());
            eventMapper.updateById(existing);
        } else {
            eventMapper.updateById(event);
        }
        log.info("更新赛事: id={}", event.getId());
    }

    /**
     * 发布赛事 (草稿/待审核 → 已发布)
     */
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        Event event = getById(id);
        int currentStatus = event.getStatus();
        if (currentStatus != EventStatusEnum.DRAFT.getCode()
                && currentStatus != EventStatusEnum.PENDING_REVIEW.getCode()) {
            throw new BusinessException("当前状态不允许发布，当前状态: "
                    + EventStatusEnum.fromCode(currentStatus).getDesc());
        }
        // 校验必填信息
        validateForPublish(event);
        event.setStatus(EventStatusEnum.PUBLISHED.getCode());
        eventMapper.updateById(event);
        log.info("赛事已发布: id={}", id);
    }

    /**
     * 开启报名 (已发布 → 报名中)
     */
    @Transactional(rollbackFor = Exception.class)
    public void openRegistration(Long id) {
        Event event = getById(id);
        if (event.getStatus() != EventStatusEnum.PUBLISHED.getCode()) {
            throw new BusinessException("只有已发布状态的赛事才能开启报名");
        }
        event.setStatus(EventStatusEnum.REGISTRATION_OPEN.getCode());
        eventMapper.updateById(event);
        log.info("赛事报名已开启: id={}", id);
    }

    /**
     * 下架赛事 (已发布/报名中 → 草稿)
     */
    @Transactional(rollbackFor = Exception.class)
    public void unpublish(Long id) {
        Event event = getById(id);
        int currentStatus = event.getStatus();
        if (currentStatus != EventStatusEnum.PUBLISHED.getCode()
                && currentStatus != EventStatusEnum.REGISTRATION_OPEN.getCode()) {
            throw new BusinessException("当前状态不允许下架");
        }
        event.setStatus(EventStatusEnum.DRAFT.getCode());
        eventMapper.updateById(event);
        log.info("赛事已下架: id={}", id);
    }

    /**
     * 取消赛事
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        Event event = getById(id);
        if (event.getStatus() == EventStatusEnum.ENDED.getCode()
                || event.getStatus() == EventStatusEnum.CANCELLED.getCode()) {
            throw new BusinessException("赛事已结束或已取消，不能再次取消");
        }
        event.setStatus(EventStatusEnum.CANCELLED.getCode());
        eventMapper.updateById(event);
        log.info("赛事已取消: id={}", id);
    }

    /**
     * 结束赛事
     */
    @Transactional(rollbackFor = Exception.class)
    public void end(Long id) {
        Event event = getById(id);
        if (event.getStatus() == EventStatusEnum.ENDED.getCode()
                || event.getStatus() == EventStatusEnum.CANCELLED.getCode()) {
            throw new BusinessException("赛事已结束或已取消");
        }
        event.setStatus(EventStatusEnum.ENDED.getCode());
        eventMapper.updateById(event);
        log.info("赛事已结束: id={}", id);
    }

    /**
     * 删除赛事（逻辑删除，仅草稿/已取消状态可删）
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Event event = getById(id);
        if (event.getStatus() != EventStatusEnum.DRAFT.getCode()
                && event.getStatus() != EventStatusEnum.CANCELLED.getCode()) {
            throw new BusinessException("只有草稿或已取消状态的赛事才能删除");
        }
        eventMapper.deleteById(id);
        log.info("删除赛事: id={}", id);
    }

    /**
     * 增加浏览量
     */
    public void incrementViewCount(Long id) {
        Event event = eventMapper.selectById(id);
        if (event != null) {
            event.setViewCount(event.getViewCount() + 1);
            eventMapper.updateById(event);
        }
    }

    /**
     * 发布前校验必填字段
     */
    private void validateForPublish(Event event) {
        if (!StringUtils.hasText(event.getTitle())) {
            throw new BusinessException("赛事标题不能为空");
        }
        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new BusinessException("赛事开始/结束时间不能为空");
        }
        if (event.getStartTime().isAfter(event.getEndTime())) {
            throw new BusinessException("赛事开始时间不能晚于结束时间");
        }
        if (event.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("赛事开始时间不能早于当前时间");
        }
    }
}

package com.senyehui.tenant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.tenant.entity.Tenant;
import com.senyehui.tenant.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 租户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantMapper tenantMapper;

    /**
     * 分页查询租户列表
     */
    public PageResult<Tenant> page(PageQuery pageQuery, String keyword) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Tenant::getTenantName, keyword)
                    .or().like(Tenant::getTenantCode, keyword);
        }
        wrapper.orderByDesc(Tenant::getCreateTime);

        IPage<Tenant> page = tenantMapper.selectPage(
                new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize()),
                wrapper
        );
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getRecords());
    }

    /**
     * 根据 ID 查询租户
     */
    public Tenant getById(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        return tenant;
    }

    /**
     * 根据租户编码查询
     */
    public Tenant getByCode(String tenantCode) {
        return tenantMapper.selectOne(
                new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantCode, tenantCode)
        );
    }

    /**
     * 创建租户
     */
    @Transactional(rollbackFor = Exception.class)
    public Tenant create(Tenant tenant) {
        // 校验编码唯一
        Tenant existing = getByCode(tenant.getTenantCode());
        if (existing != null) {
            throw new BusinessException("租户编码已存在: " + tenant.getTenantCode());
        }
        tenant.setStatus(1);
        tenantMapper.insert(tenant);
        log.info("创建租户成功: id={}, code={}", tenant.getId(), tenant.getTenantCode());
        return tenant;
    }

    /**
     * 更新租户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Tenant tenant) {
        Tenant existing = getById(tenant.getId());
        // 编码不允许修改
        tenant.setTenantCode(existing.getTenantCode());
        tenantMapper.updateById(tenant);
        log.info("更新租户: id={}", tenant.getId());
    }

    /**
     * 停用/启用租户
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Tenant tenant = getById(id);
        tenant.setStatus(status);
        tenantMapper.updateById(tenant);
        log.info("租户状态变更: id={}, status={}", id, status);
    }

    /**
     * 删除租户（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getById(id); // 确认存在
        tenantMapper.deleteById(id);
        log.info("删除租户: id={}", id);
    }

    /**
     * 校验租户是否有效
     */
    public boolean isValid(Long tenantId) {
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null || tenant.getStatus() != 1) {
            return false;
        }
        // 检查是否过期
        if (tenant.getExpireTime() != null &&
                tenant.getExpireTime().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }
        return true;
    }
}

package com.senyehui.tenant.controller;

import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.common.model.R;
import com.senyehui.tenant.entity.Tenant;
import com.senyehui.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 租户管理 API
 */
@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * 分页查询租户列表
     */
    @GetMapping("/page")
    public R<PageResult<Tenant>> page(PageQuery pageQuery,
                                      @RequestParam(required = false) String keyword) {
        return R.ok(tenantService.page(pageQuery, keyword));
    }

    /**
     * 获取租户详情
     */
    @GetMapping("/{id}")
    public R<Tenant> getById(@PathVariable Long id) {
        return R.ok(tenantService.getById(id));
    }

    /**
     * 创建租户
     */
    @PostMapping
    public R<Tenant> create(@RequestBody Tenant tenant) {
        return R.ok(tenantService.create(tenant));
    }

    /**
     * 更新租户
     */
    @PutMapping
    public R<Void> update(@RequestBody Tenant tenant) {
        tenantService.update(tenant);
        return R.ok();
    }

    /**
     * 更新租户状态
     */
    @PutMapping("/{id}/status/{status}")
    public R<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        tenantService.updateStatus(id, status);
        return R.ok();
    }

    /**
     * 删除租户
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return R.ok();
    }
}

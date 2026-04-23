package com.senyehui.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senyehui.admin.entity.TenantPackage;
import com.senyehui.admin.mapper.TenantPackageMapper;
import com.senyehui.common.model.R;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tenant/package")
public class TenantPackageController {

    private final TenantPackageMapper packageMapper;

    public TenantPackageController(TenantPackageMapper packageMapper) {
        this.packageMapper = packageMapper;
    }

    @GetMapping("/list")
    public R<List<TenantPackage>> list() {
        return R.ok(packageMapper.selectList(
                new LambdaQueryWrapper<TenantPackage>().orderByAsc(TenantPackage::getId)));
    }

    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R<TenantPackage> create(@RequestBody TenantPackage pkg) {
        if (pkg.getStatus() == null) pkg.setStatus(1);
        pkg.setCreateTime(LocalDateTime.now());
        pkg.setUpdateTime(LocalDateTime.now());
        packageMapper.insert(pkg);
        return R.ok(pkg);
    }

    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public R<Void> update(@RequestBody TenantPackage pkg) {
        pkg.setUpdateTime(LocalDateTime.now());
        packageMapper.updateById(pkg);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        packageMapper.deleteById(id);
        return R.ok();
    }
}

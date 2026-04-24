package com.senyehui.membership.controller;

import com.senyehui.common.model.R;
import com.senyehui.membership.entity.MembershipConfig;
import com.senyehui.membership.service.MembershipConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership/config")
@RequiredArgsConstructor
public class MembershipConfigController {

    private final MembershipConfigService configService;

    @GetMapping("/list")
    public R<List<MembershipConfig>> listConfigs(@RequestParam(required = false) String membershipType) {
        List<MembershipConfig> configs = membershipType != null
                ? configService.getConfigsByType(membershipType)
                : configService.list();
        return R.ok(configs);
    }

    @GetMapping("/{id}")
    public R<MembershipConfig> getConfig(@PathVariable Long id) {
        MembershipConfig config = configService.getById(id);
        return R.ok(config);
    }

    @PostMapping
    public R<MembershipConfig> createConfig(@RequestBody MembershipConfig config) {
        configService.save(config);
        return R.ok(config);
    }

    @PutMapping("/{id}")
    public R<MembershipConfig> updateConfig(@PathVariable Long id, @RequestBody MembershipConfig config) {
        config.setId(id);
        configService.updateById(config);
        return R.ok(config);
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteConfig(@PathVariable Long id) {
        configService.removeById(id);
        return R.ok();
    }
}

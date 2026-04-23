package com.senyehui.event.controller;

import com.senyehui.common.model.R;
import com.senyehui.event.entity.Event;
import com.senyehui.event.mapper.EventMapper;
import com.senyehui.tenant.context.TenantContext;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Dashboard 统计接口
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final EventMapper eventMapper;

    /**
     * 总览统计
     */
    @GetMapping("/stats")
    public R<Map<String, Object>> getStats() {
        Long tenantId = TenantContext.getTenantId();

        LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Event::getTenantId, tenantId);
        long totalEvents = eventMapper.selectCount(wrapper);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvents", totalEvents);
        stats.put("totalRegistrations", 0);
        stats.put("monthlyRevenue", 0);
        stats.put("checkInRate", 0);

        return R.ok(stats);
    }

    /**
     * 赛事状态分布
     */
    @GetMapping("/event-status")
    public R<List<Map<String, Object>>> getEventStatusDistribution() {
        Long tenantId = TenantContext.getTenantId();
        List<Map<String, Object>> list = new ArrayList<>();

        String[] names = {"草稿", "待审核", "已发布", "报名中", "进行中", "已结束", "已取消"};
        for (int i = 0; i < names.length; i++) {
            LambdaQueryWrapper<Event> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Event::getTenantId, tenantId).eq(Event::getStatus, i);
            long count = eventMapper.selectCount(wrapper);
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", names[i]);
                item.put("value", count);
                list.add(item);
            }
        }

        return R.ok(list);
    }
}

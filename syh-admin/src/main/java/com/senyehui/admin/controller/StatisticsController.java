package com.senyehui.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senyehui.common.model.R;
import com.senyehui.event.entity.Event;
import com.senyehui.event.mapper.EventMapper;
import com.senyehui.registration.entity.Registration;
import com.senyehui.registration.mapper.RegistrationMapper;
import com.senyehui.tenant.context.TenantContext;
import com.senyehui.wechat.entity.PayOrder;
import com.senyehui.wechat.mapper.PayOrderMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final EventMapper eventMapper;
    private final RegistrationMapper registrationMapper;
    private final PayOrderMapper payOrderMapper;

    public StatisticsController(EventMapper eventMapper,
                                RegistrationMapper registrationMapper,
                                PayOrderMapper payOrderMapper) {
        this.eventMapper = eventMapper;
        this.registrationMapper = registrationMapper;
        this.payOrderMapper = payOrderMapper;
    }

    /** 总览数据 */
    @GetMapping("/overview")
    public R<Map<String, Object>> overview() {
        Long tenantId = TenantContext.getTenantId();

        long totalEvents = eventMapper.selectCount(
                new LambdaQueryWrapper<Event>().eq(Event::getTenantId, tenantId));

        long totalRegistrations = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>().eq(Registration::getTenantId, tenantId));

        long checkedIn = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getTenantId, tenantId)
                        .eq(Registration::getCheckInStatus, 1));

        // 本月收入（status=2 已支付）
        LocalDateTime monthStart = YearMonth.now().atDay(1).atStartOfDay();
        List<PayOrder> paidOrders = payOrderMapper.selectList(
                new LambdaQueryWrapper<PayOrder>()
                        .eq(PayOrder::getTenantId, tenantId)
                        .eq(PayOrder::getStatus, 2)
                        .ge(PayOrder::getPayTime, monthStart));
        BigDecimal monthlyRevenue = paidOrders.stream()
                .map(PayOrder::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 总收入
        List<PayOrder> allPaid = payOrderMapper.selectList(
                new LambdaQueryWrapper<PayOrder>()
                        .eq(PayOrder::getTenantId, tenantId)
                        .eq(PayOrder::getStatus, 2));
        BigDecimal totalRevenue = allPaid.stream()
                .map(PayOrder::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double checkInRate = totalRegistrations > 0
                ? Math.round(checkedIn * 1000.0 / totalRegistrations) / 10.0
                : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvents", totalEvents);
        stats.put("totalRegistrations", totalRegistrations);
        stats.put("monthlyRevenue", monthlyRevenue);
        stats.put("totalRevenue", totalRevenue);
        stats.put("checkInRate", checkInRate);
        stats.put("checkedIn", checkedIn);
        return R.ok(stats);
    }

    /** 赛事状态分布（饼图） */
    @GetMapping("/event-status")
    public R<List<Map<String, Object>>> eventStatus() {
        Long tenantId = TenantContext.getTenantId();
        String[] names = {"草稿", "待审核", "已发布", "报名中", "进行中", "已结束", "已取消"};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            long count = eventMapper.selectCount(
                    new LambdaQueryWrapper<Event>()
                            .eq(Event::getTenantId, tenantId)
                            .eq(Event::getStatus, i));
            if (count > 0) {
                list.add(Map.of("name", names[i], "value", count));
            }
        }
        return R.ok(list);
    }

    /** 近6个月报名趋势（折线图） */
    @GetMapping("/registration-trend")
    public R<Map<String, Object>> registrationTrend() {
        Long tenantId = TenantContext.getTenantId();
        List<String> months = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);
            long count = registrationMapper.selectCount(
                    new LambdaQueryWrapper<Registration>()
                            .eq(Registration::getTenantId, tenantId)
                            .between(Registration::getCreateTime, start, end));
            months.add(ym.getYear() + "/" + String.format("%02d", ym.getMonthValue()));
            counts.add(count);
        }
        return R.ok(Map.of("months", months, "counts", counts));
    }
}

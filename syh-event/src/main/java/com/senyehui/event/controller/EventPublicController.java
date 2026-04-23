package com.senyehui.event.controller;

import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.common.model.R;
import com.senyehui.event.entity.Event;
import com.senyehui.event.entity.EventGroup;
import com.senyehui.event.enums.EventStatusEnum;
import com.senyehui.event.service.EventGroupService;
import com.senyehui.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C 端赛事查询 API（公开接口，无需登录）
 */
@RestController
@RequestMapping("/api/public/event")
@RequiredArgsConstructor
public class EventPublicController {

    private final EventService eventService;
    private final EventGroupService groupService;

    /**
     * C 端分页查询已发布/报名中/进行中的赛事
     */
    @GetMapping("/page")
    public R<PageResult<Event>> page(PageQuery pageQuery,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer eventType) {
        // C 端只查询已发布及以后的赛事（排除草稿、待审核、已取消）
        return R.ok(eventService.page(pageQuery, keyword, EventStatusEnum.PUBLISHED.getCode(), eventType));
    }

    /**
     * C 端获取赛事详情（并增加浏览量）
     */
    @GetMapping("/{id}")
    public R<Event> getById(@PathVariable Long id) {
        Event event = eventService.getById(id);
        // 增加浏览量
        eventService.incrementViewCount(id);
        return R.ok(event);
    }

    /**
     * C 端查询赛事组别
     */
    @GetMapping("/{eventId}/group/list")
    public R<List<EventGroup>> listGroups(@PathVariable Long eventId) {
        return R.ok(groupService.listByEventId(eventId));
    }
}

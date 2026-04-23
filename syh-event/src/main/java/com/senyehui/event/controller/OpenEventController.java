package com.senyehui.event.controller;

import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.common.model.R;
import com.senyehui.event.entity.Event;
import com.senyehui.event.entity.EventGroup;
import com.senyehui.event.service.EventGroupService;
import com.senyehui.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端公开赛事 API（无需登录）
 */
@RestController
@RequestMapping("/open/event")
@RequiredArgsConstructor
public class OpenEventController {

    private final EventService eventService;
    private final EventGroupService groupService;

    /**
     * 赛事列表（只返回已发布 + 报名中的赛事）
     */
    @GetMapping("/list")
    public R<PageResult<Event>> list(PageQuery pageQuery,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer eventType) {
        // C端只展示已发布(2) 和 报名中(3) 的赛事
        PageResult<Event> result = eventService.publicPage(pageQuery, keyword, eventType);
        return R.ok(result);
    }

    /**
     * 赛事详情
     */
    @GetMapping("/{id}")
    public R<Event> detail(@PathVariable Long id) {
        Event event = eventService.getById(id);
        // 增加浏览量
        eventService.incrementViewCount(id);
        return R.ok(event);
    }

    /**
     * 赛事组别列表
     */
    @GetMapping("/{eventId}/groups")
    public R<List<EventGroup>> groups(@PathVariable Long eventId) {
        return R.ok(groupService.listByEventId(eventId));
    }
}

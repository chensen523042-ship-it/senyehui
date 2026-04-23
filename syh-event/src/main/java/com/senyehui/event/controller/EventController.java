package com.senyehui.event.controller;

import com.senyehui.common.model.PageQuery;
import com.senyehui.common.model.PageResult;
import com.senyehui.common.model.R;
import com.senyehui.event.entity.Event;
import com.senyehui.event.entity.EventCategory;
import com.senyehui.event.entity.EventGroup;
import com.senyehui.event.service.EventCategoryService;
import com.senyehui.event.service.EventGroupService;
import com.senyehui.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 赛事管理 API（管理后台）
 */
@RestController
@RequestMapping("/api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventCategoryService categoryService;
    private final EventGroupService groupService;

    // ==================== 赛事 CRUD ====================

    /**
     * 分页查询赛事列表
     */
    @GetMapping("/page")
    public R<PageResult<Event>> page(PageQuery pageQuery,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) Integer eventType) {
        return R.ok(eventService.page(pageQuery, keyword, status, eventType));
    }

    /**
     * 获取赛事详情
     */
    @GetMapping("/{id}")
    public R<Event> getById(@PathVariable Long id) {
        return R.ok(eventService.getById(id));
    }

    /**
     * 创建赛事
     */
    @PostMapping
    public R<Event> create(@RequestBody Event event) {
        return R.ok(eventService.create(event));
    }

    /**
     * 更新赛事
     */
    @PutMapping
    public R<Void> update(@RequestBody Event event) {
        eventService.update(event);
        return R.ok();
    }

    /**
     * 删除赛事
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return R.ok();
    }

    // ==================== 赛事状态操作 ====================

    /**
     * 发布赛事
     */
    @PutMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id) {
        eventService.publish(id);
        return R.ok();
    }

    /**
     * 开启报名
     */
    @PutMapping("/{id}/open-registration")
    public R<Void> openRegistration(@PathVariable Long id) {
        eventService.openRegistration(id);
        return R.ok();
    }

    /**
     * 下架赛事
     */
    @PutMapping("/{id}/unpublish")
    public R<Void> unpublish(@PathVariable Long id) {
        eventService.unpublish(id);
        return R.ok();
    }

    /**
     * 取消赛事
     */
    @PutMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id) {
        eventService.cancel(id);
        return R.ok();
    }

    /**
     * 结束赛事
     */
    @PutMapping("/{id}/end")
    public R<Void> end(@PathVariable Long id) {
        eventService.end(id);
        return R.ok();
    }

    // ==================== 赛事分类 ====================

    /**
     * 查询所有分类
     */
    @GetMapping("/category/list")
    public R<List<EventCategory>> listCategories() {
        return R.ok(categoryService.listAll());
    }

    /**
     * 创建分类
     */
    @PostMapping("/category")
    public R<EventCategory> createCategory(@RequestBody EventCategory category) {
        return R.ok(categoryService.create(category));
    }

    /**
     * 更新分类
     */
    @PutMapping("/category")
    public R<Void> updateCategory(@RequestBody EventCategory category) {
        categoryService.update(category);
        return R.ok();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/category/{id}")
    public R<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return R.ok();
    }

    // ==================== 赛事组别 ====================

    /**
     * 查询赛事下所有组别
     */
    @GetMapping("/{eventId}/group/list")
    public R<List<EventGroup>> listGroups(@PathVariable Long eventId) {
        return R.ok(groupService.listByEventId(eventId));
    }

    /**
     * 创建组别
     */
    @PostMapping("/group")
    public R<EventGroup> createGroup(@RequestBody EventGroup group) {
        return R.ok(groupService.create(group));
    }

    /**
     * 更新组别
     */
    @PutMapping("/group")
    public R<Void> updateGroup(@RequestBody EventGroup group) {
        groupService.update(group);
        return R.ok();
    }

    /**
     * 删除组别
     */
    @DeleteMapping("/group/{id}")
    public R<Void> deleteGroup(@PathVariable Long id) {
        groupService.delete(id);
        return R.ok();
    }
}

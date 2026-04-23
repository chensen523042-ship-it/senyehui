package com.senyehui.event.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.event.entity.EventCategory;
import com.senyehui.event.mapper.EventCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 赛事分类服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventCategoryService {

    private final EventCategoryMapper categoryMapper;

    /**
     * 查询所有分类
     */
    public List<EventCategory> listAll() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<EventCategory>()
                        .eq(EventCategory::getStatus, 1)
                        .orderByAsc(EventCategory::getSortOrder)
        );
    }

    /**
     * 获取分类详情
     */
    public EventCategory getById(Long id) {
        EventCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    /**
     * 创建分类
     */
    @Transactional(rollbackFor = Exception.class)
    public EventCategory create(EventCategory category) {
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
        log.info("创建赛事分类: id={}, name={}", category.getId(), category.getName());
        return category;
    }

    /**
     * 更新分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(EventCategory category) {
        getById(category.getId());
        categoryMapper.updateById(category);
        log.info("更新赛事分类: id={}", category.getId());
    }

    /**
     * 删除分类
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getById(id);
        categoryMapper.deleteById(id);
        log.info("删除赛事分类: id={}", id);
    }
}

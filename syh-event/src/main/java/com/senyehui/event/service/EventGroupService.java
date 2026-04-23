package com.senyehui.event.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senyehui.common.exception.BusinessException;
import com.senyehui.event.entity.EventGroup;
import com.senyehui.event.mapper.EventGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 赛事组别服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventGroupService {

    private final EventGroupMapper groupMapper;

    /**
     * 查询赛事下的所有组别
     */
    public List<EventGroup> listByEventId(Long eventId) {
        return groupMapper.selectList(
                new LambdaQueryWrapper<EventGroup>()
                        .eq(EventGroup::getEventId, eventId)
                        .orderByAsc(EventGroup::getSortOrder)
        );
    }

    /**
     * 获取组别详情
     */
    public EventGroup getById(Long id) {
        EventGroup group = groupMapper.selectById(id);
        if (group == null) {
            throw new BusinessException("组别不存在");
        }
        return group;
    }

    /**
     * 创建组别
     */
    @Transactional(rollbackFor = Exception.class)
    public EventGroup create(EventGroup group) {
        group.setCurrentParticipants(0);
        if (group.getMaxParticipants() == null) {
            group.setMaxParticipants(0);
        }
        if (group.getSortOrder() == null) {
            group.setSortOrder(0);
        }
        if (group.getStatus() == null) {
            group.setStatus(1);
        }
        groupMapper.insert(group);
        log.info("创建赛事组别: id={}, eventId={}, name={}", group.getId(), group.getEventId(), group.getName());
        return group;
    }

    /**
     * 更新组别
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(EventGroup group) {
        getById(group.getId());
        groupMapper.updateById(group);
        log.info("更新赛事组别: id={}", group.getId());
    }

    /**
     * 删除组别
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getById(id);
        groupMapper.deleteById(id);
        log.info("删除赛事组别: id={}", id);
    }
}

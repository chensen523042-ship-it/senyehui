package com.senyehui.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.event.entity.EventGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * 赛事组别数据访问
 */
@Mapper
public interface EventGroupMapper extends BaseMapper<EventGroup> {
}

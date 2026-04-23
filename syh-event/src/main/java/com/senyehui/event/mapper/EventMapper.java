package com.senyehui.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.event.entity.Event;
import org.apache.ibatis.annotations.Mapper;

/**
 * 赛事活动数据访问
 */
@Mapper
public interface EventMapper extends BaseMapper<Event> {
}

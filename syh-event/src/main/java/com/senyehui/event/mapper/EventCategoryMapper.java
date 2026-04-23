package com.senyehui.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.event.entity.EventCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 赛事分类数据访问
 */
@Mapper
public interface EventCategoryMapper extends BaseMapper<EventCategory> {
}

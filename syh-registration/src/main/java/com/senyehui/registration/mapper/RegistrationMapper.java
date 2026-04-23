package com.senyehui.registration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.registration.entity.Registration;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报名记录数据访问
 */
@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
}

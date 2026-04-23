package com.senyehui.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.security.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色数据访问
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {
}

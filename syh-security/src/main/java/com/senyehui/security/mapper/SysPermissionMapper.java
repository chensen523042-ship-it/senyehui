package com.senyehui.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.security.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限数据访问
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
}

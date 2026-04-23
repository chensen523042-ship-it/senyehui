package com.senyehui.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.security.entity.SysRolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联数据访问
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(Long roleId);

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);
}

package com.senyehui.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.tenant.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户数据访问
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}

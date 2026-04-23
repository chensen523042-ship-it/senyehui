package com.senyehui.wechat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.wechat.entity.PayOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {

    @Select("SELECT COALESCE(SUM(amount), 0) FROM pay_order WHERE status = 1 AND deleted = 0 AND tenant_id = #{tenantId}")
    BigDecimal sumPaidAmount(Long tenantId);

    @Select("SELECT COUNT(*) FROM pay_order WHERE status = 1 AND deleted = 0 AND tenant_id = #{tenantId}")
    int countPaid(Long tenantId);
}

package com.senyehui.membership.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senyehui.membership.entity.MembershipConfig;
import com.senyehui.membership.mapper.MembershipConfigMapper;
import com.senyehui.membership.service.MembershipConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipConfigServiceImpl extends ServiceImpl<MembershipConfigMapper, MembershipConfig> implements MembershipConfigService {

    @Override
    public List<MembershipConfig> getConfigsByType(String membershipType) {
        return lambdaQuery()
                .eq(MembershipConfig::getMembershipType, membershipType)
                .eq(MembershipConfig::getEnabled, true)
                .orderByAsc(MembershipConfig::getSortOrder)
                .list();
    }

    @Override
    public MembershipConfig getConfigByTypeAndLevel(String membershipType, String level) {
        return lambdaQuery()
                .eq(MembershipConfig::getMembershipType, membershipType)
                .eq(MembershipConfig::getLevel, level)
                .eq(MembershipConfig::getEnabled, true)
                .one();
    }
}

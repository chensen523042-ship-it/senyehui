package com.senyehui.membership.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.senyehui.membership.entity.MembershipConfig;

import java.util.List;

public interface MembershipConfigService extends IService<MembershipConfig> {

    List<MembershipConfig> getConfigsByType(String membershipType);

    MembershipConfig getConfigByTypeAndLevel(String membershipType, String level);
}

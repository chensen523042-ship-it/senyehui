package com.senyehui.membership.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.senyehui.membership.entity.MembershipBenefit;

import java.util.List;

public interface MembershipBenefitService extends IService<MembershipBenefit> {

    List<MembershipBenefit> getMembershipBenefits(Long membershipId);

    MembershipBenefit allocateBenefit(Long membershipId, String benefitCode, Integer quota);

    boolean consumeBenefit(Long benefitId, Integer quantity);

    boolean checkBenefitAvailable(Long membershipId, String benefitCode);
}

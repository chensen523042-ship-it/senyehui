package com.senyehui.membership.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.senyehui.membership.entity.MembershipBenefit;
import com.senyehui.membership.mapper.MembershipBenefitMapper;
import com.senyehui.membership.service.MembershipBenefitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MembershipBenefitServiceImpl extends ServiceImpl<MembershipBenefitMapper, MembershipBenefit> implements MembershipBenefitService {

    @Override
    public List<MembershipBenefit> getMembershipBenefits(Long membershipId) {
        return lambdaQuery()
                .eq(MembershipBenefit::getMembershipId, membershipId)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MembershipBenefit allocateBenefit(Long membershipId, String benefitCode, Integer quota) {
        MembershipBenefit benefit = new MembershipBenefit();
        benefit.setMembershipId(membershipId);
        benefit.setBenefitCode(benefitCode);
        benefit.setBenefitName(benefitCode);
        benefit.setBenefitType("QUOTA");
        benefit.setTotalQuota(quota);
        benefit.setUsedQuota(0);
        benefit.setValidFrom(LocalDateTime.now());
        benefit.setValidTo(LocalDateTime.now().plusYears(1));

        save(benefit);
        return benefit;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean consumeBenefit(Long benefitId, Integer quantity) {
        MembershipBenefit benefit = getById(benefitId);
        if (benefit == null) {
            throw new RuntimeException("权益不存在");
        }

        if (benefit.getValidTo().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("权益已过期");
        }

        int remainingQuota = benefit.getTotalQuota() - benefit.getUsedQuota();
        if (remainingQuota < quantity) {
            throw new RuntimeException("权益配额不足");
        }

        benefit.setUsedQuota(benefit.getUsedQuota() + quantity);
        return updateById(benefit);
    }

    @Override
    public boolean checkBenefitAvailable(Long membershipId, String benefitCode) {
        MembershipBenefit benefit = lambdaQuery()
                .eq(MembershipBenefit::getMembershipId, membershipId)
                .eq(MembershipBenefit::getBenefitCode, benefitCode)
                .one();

        if (benefit == null) {
            return false;
        }

        if (benefit.getValidTo().isBefore(LocalDateTime.now())) {
            return false;
        }

        return benefit.getUsedQuota() < benefit.getTotalQuota();
    }
}

package com.senyehui.membership.integration;

import com.senyehui.membership.entity.Membership;
import com.senyehui.membership.entity.MembershipBenefit;
import com.senyehui.membership.service.MembershipBenefitService;
import com.senyehui.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipIntegration {

    private final MembershipService membershipService;
    private final MembershipBenefitService benefitService;

    public BigDecimal applyMembershipDiscount(Long userId, BigDecimal originalAmount) {
        if (!membershipService.checkMembershipValid(userId)) {
            return originalAmount;
        }

        Membership membership = membershipService.getUserMembership(userId);
        if (membership == null) {
            return originalAmount;
        }

        boolean hasDiscount = benefitService.checkBenefitAvailable(membership.getId(), "REGISTRATION_DISCOUNT");
        if (!hasDiscount) {
            return originalAmount;
        }

        BigDecimal discountRate = getDiscountRateByLevel(membership.getLevel());
        BigDecimal discountedAmount = originalAmount.multiply(discountRate);

        log.info("应用会员折扣: userId={}, level={}, 原价={}, 折后价={}",
                userId, membership.getLevel(), originalAmount, discountedAmount);

        return discountedAmount;
    }

    public boolean checkFreeRegistrationBenefit(Long userId) {
        if (!membershipService.checkMembershipValid(userId)) {
            return false;
        }

        Membership membership = membershipService.getUserMembership(userId);
        if (membership == null) {
            return false;
        }

        return benefitService.checkBenefitAvailable(membership.getId(), "FREE_REGISTRATION");
    }

    public void consumeFreeRegistrationBenefit(Long userId) {
        Membership membership = membershipService.getUserMembership(userId);
        if (membership == null) {
            return;
        }

        MembershipBenefit benefit = benefitService.lambdaQuery()
                .eq(MembershipBenefit::getMembershipId, membership.getId())
                .eq(MembershipBenefit::getBenefitCode, "FREE_REGISTRATION")
                .one();

        if (benefit != null) {
            benefitService.consumeBenefit(benefit.getId(), 1);
            log.info("消费免费报名权益: userId={}, membershipId={}", userId, membership.getId());
        }
    }

    private BigDecimal getDiscountRateByLevel(String level) {
        return switch (level) {
            case "SILVER" -> new BigDecimal("0.95");
            case "GOLD" -> new BigDecimal("0.90");
            case "DIAMOND" -> new BigDecimal("0.85");
            default -> BigDecimal.ONE;
        };
    }
}

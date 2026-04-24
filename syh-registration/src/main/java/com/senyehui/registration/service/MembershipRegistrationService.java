package com.senyehui.registration.service;

import com.senyehui.common.exception.BusinessException;
import com.senyehui.membership.integration.MembershipIntegration;
import com.senyehui.registration.dto.RegisterRequest;
import com.senyehui.registration.entity.Registration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipRegistrationService {

    private final RegistrationService registrationService;
    private final MembershipIntegration membershipIntegration;

    @Transactional(rollbackFor = Exception.class)
    public Registration registerWithMembershipBenefit(RegisterRequest req, Long userId) {
        if (userId == null) {
            return registrationService.register(req, userId);
        }

        boolean hasFreeRegistration = membershipIntegration.checkFreeRegistrationBenefit(userId);

        if (hasFreeRegistration) {
            log.info("用户拥有免费报名权益: userId={}", userId);
            Registration registration = registrationService.register(req, userId);

            registration.setAmount(BigDecimal.ZERO);
            registration.setPayStatus(1);
            registration.setStatus(1);

            membershipIntegration.consumeFreeRegistrationBenefit(userId);

            return registration;
        }

        Registration registration = registrationService.register(req, userId);

        if (registration.getAmount() != null && registration.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountedAmount = membershipIntegration.applyMembershipDiscount(
                    userId, registration.getAmount());

            if (discountedAmount.compareTo(registration.getAmount()) < 0) {
                log.info("应用会员折扣: userId={}, 原价={}, 折后价={}",
                        userId, registration.getAmount(), discountedAmount);
                registration.setAmount(discountedAmount);
            }
        }

        return registration;
    }
}

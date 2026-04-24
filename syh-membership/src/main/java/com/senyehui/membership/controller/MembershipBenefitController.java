package com.senyehui.membership.controller;

import com.senyehui.common.model.R;
import com.senyehui.membership.entity.MembershipBenefit;
import com.senyehui.membership.service.MembershipBenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership/benefit")
@RequiredArgsConstructor
public class MembershipBenefitController {

    private final MembershipBenefitService benefitService;

    @GetMapping("/list/{membershipId}")
    public R<List<MembershipBenefit>> getMembershipBenefits(@PathVariable Long membershipId) {
        List<MembershipBenefit> benefits = benefitService.getMembershipBenefits(membershipId);
        return R.ok(benefits);
    }

    @PostMapping("/allocate")
    public R<MembershipBenefit> allocateBenefit(@RequestParam Long membershipId,
                                                 @RequestParam String benefitCode,
                                                 @RequestParam Integer quota) {
        MembershipBenefit benefit = benefitService.allocateBenefit(membershipId, benefitCode, quota);
        return R.ok(benefit);
    }

    @PostMapping("/consume/{benefitId}")
    public R<Boolean> consumeBenefit(@PathVariable Long benefitId, @RequestParam Integer quantity) {
        boolean success = benefitService.consumeBenefit(benefitId, quantity);
        return R.ok(success);
    }

    @GetMapping("/check")
    public R<Boolean> checkBenefitAvailable(@RequestParam Long membershipId, @RequestParam String benefitCode) {
        boolean available = benefitService.checkBenefitAvailable(membershipId, benefitCode);
        return R.ok(available);
    }
}

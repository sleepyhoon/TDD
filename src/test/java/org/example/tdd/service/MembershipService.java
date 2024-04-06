package org.example.tdd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tdd.domain.MembershipAddResponse;
import org.example.tdd.MembershipType;
import org.example.tdd.domain.MembershipDetailResponse;
import org.example.tdd.repository.MembershipRepository;
import org.example.tdd.domain.Membership;
import org.example.tdd.error.MembershipErrorResult;
import org.example.tdd.error.MembershipException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {
    private final MembershipRepository membershipRepository;
    private final PointService ratePointService;
    @Transactional
    public MembershipAddResponse addMembership(final String userId, final MembershipType membershipType, final Integer point){
        final Membership result = membershipRepository.findByUserIdAndMembershipType(userId,membershipType);
        if(result!=null){
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }
        final Membership membership = Membership.builder()
                .userId(userId)
                .membershipType(membershipType)
                .point(point)
                .build();
        final Membership savedMembership = membershipRepository.save(membership);
        return MembershipAddResponse.builder()
                .id(savedMembership.getId())
                .membershipType(savedMembership.getMembershipType())
                .build();
    }

    public List<MembershipDetailResponse> getMembershipList(String userId) {
        final List<Membership> list = membershipRepository.findByUserId(userId);
        // entity를 dto로 바꿔주는 과정이다.
        return list.stream()
                .map(v->MembershipDetailResponse.builder()
                        .id(v.getId())
                        .point(v.getPoint())
                        .membershipType(v.getMembershipType())
                        .createdAt(v.getCreatedAt())
                        .build()
                ).collect(Collectors.toList());
    }

    public MembershipDetailResponse getMembership(final Long membershipId,final String userId){
        final Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(()->new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if(!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }
        return MembershipDetailResponse.builder()
                .id(membership.getId())
                .membershipType(membership.getMembershipType())
                .point(membership.getPoint())
                .createdAt(membership.getCreatedAt())
                .build();
    }
    @Transactional
    public void removeMembership(Long membershipId,String userId) {
        final Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(()-> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if(!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }
        membershipRepository.deleteById(membershipId);
    }
    @Transactional
    public void accumulateMembershipPoint(final Long membershipId, final String userId, final int amount) {
        final Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(()->new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if(!membership.getUserId().equals(userId))
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        final int additionalAmount = ratePointService.calculateAmount(amount);
        membership.setPoint(additionalAmount + membership.getPoint());
    }
}


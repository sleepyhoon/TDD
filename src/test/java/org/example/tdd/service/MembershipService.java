package org.example.tdd.service;

import lombok.extern.slf4j.Slf4j;
import org.example.tdd.domain.MembershipAddResponse;
import org.example.tdd.MembershipType;
import org.example.tdd.domain.MembershipDetailResponse;
import org.example.tdd.repository.MembershipRepository;
import org.example.tdd.domain.Membership;
import org.example.tdd.MembershipErrorResult;
import org.example.tdd.MembershipException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MembershipService {
    private MembershipRepository membershipRepository;
    public MembershipAddResponse addMembership(final String userId, final MembershipType membershipType, final Integer point){
        log.info("Adding membership for userId: {}, membershipType: {}, point: {}", userId, membershipType, point);
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
}


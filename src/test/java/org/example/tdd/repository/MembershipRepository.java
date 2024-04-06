package org.example.tdd.repository;

import org.example.tdd.MembershipType;
import org.example.tdd.domain.Membership;
import org.example.tdd.domain.MembershipDetailResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership,Long> {

    List<Membership> findByUserId(String userId);
    Membership findByUserIdAndMembershipType(String userId, MembershipType membershipType);
}

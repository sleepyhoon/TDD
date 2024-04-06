package org.example.tdd.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tdd.ValidationGroups;
import org.example.tdd.domain.MembershipDetailResponse;
import org.example.tdd.domain.MembershipRequest;
import org.example.tdd.domain.MembershipAddResponse;
import org.example.tdd.service.MembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.tdd.MembershipContents.USER_ID_HEADER;
import static org.example.tdd.ValidationGroups.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequiredArgsConstructor
public class MembershipController {
    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipAddResponse> addMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Validated(MembershipAddMarker.class) final MembershipRequest membershipRequest
    ) {
        final MembershipAddResponse membershipAddResponse = membershipService.addMembership(userId, membershipRequest.getMembershipType(),
                membershipRequest.getPoint());
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipAddResponse);
    }

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponse>> getMembershipList(
            @RequestHeader(USER_ID_HEADER) final String userId) {
        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    @GetMapping("/api/v1/memberships/{id}")
    public ResponseEntity<MembershipDetailResponse> getMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id) {
        return ResponseEntity.ok(membershipService.getMembership(id,userId));
    }

    @DeleteMapping("/api/v1/memberships/{id}")
    public ResponseEntity<?> removeMembership (
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id) {
        membershipService.removeMembership(id,userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/memberships/{id}/accumulate")
    public ResponseEntity<?> accumulateMembershipPoint(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable Long id,
            @RequestBody @Validated(MembershipAccumulateMarker.class) final MembershipRequest membershipRequest) {
        membershipService.accumulateMembershipPoint(id,userId, membershipRequest.getPoint());
        return ResponseEntity.noContent().build();
    }
}


package org.example.tdd.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tdd.domain.MembershipDetailResponse;
import org.example.tdd.domain.MembershipRequest;
import org.example.tdd.domain.MembershipAddResponse;
import org.example.tdd.service.MembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.tdd.MembershipContents.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController {
    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipAddResponse> addMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Valid final MembershipRequest membershipRequest
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
}


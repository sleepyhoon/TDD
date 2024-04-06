package org.example.tdd.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.tdd.MembershipType;

@Getter
@Builder
@RequiredArgsConstructor
public class MembershipAddResponse {
    private final Long id;
    private final MembershipType membershipType;
}

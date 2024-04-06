package org.example.tdd.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.tdd.MembershipType;

@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class MembershipRequest {
    @NotNull
    @Min(0)
    private final Integer point;

    @NotNull
    private final MembershipType membershipType;
}

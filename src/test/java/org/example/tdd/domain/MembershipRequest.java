package org.example.tdd.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.tdd.MembershipType;
import org.example.tdd.ValidationGroups;
import org.example.tdd.ValidationGroups.MembershipAddMarker;

@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class MembershipRequest {
    @NotNull(groups = {MembershipAddMarker.class, ValidationGroups.MembershipAccumulateMarker.class})
    @Min(value=0, groups = {MembershipAddMarker.class, ValidationGroups.MembershipAccumulateMarker.class})
    private final Integer point;

    @NotNull(groups = {MembershipAddMarker.class})
    private final MembershipType membershipType;
}

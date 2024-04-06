package org.example.tdd.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.tdd.MembershipType;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MembershipDetailResponse {
    @NotNull
    private Long id;

    @NotNull
    @Min(0)
    private Integer point;

    @NotNull
    private MembershipType membershipType;

    @NotNull
    private LocalDateTime createdAt;
}

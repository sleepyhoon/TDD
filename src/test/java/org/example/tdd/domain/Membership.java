package org.example.tdd.domain;

import jakarta.persistence.*;
import lombok.*;
import net.bytebuddy.agent.VirtualMachine;
import net.bytebuddy.asm.Advice;
import org.example.tdd.MembershipType;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 20)
    private String userId;

    @Column(nullable = false)
    private Integer point;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    private LocalDateTime createdAt;
}

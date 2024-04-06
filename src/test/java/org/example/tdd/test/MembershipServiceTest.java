package org.example.tdd.test;

import org.example.tdd.MembershipErrorResult;
import org.example.tdd.MembershipException;
import org.example.tdd.MembershipType;
import org.example.tdd.domain.MembershipAddResponse;
import org.example.tdd.domain.MembershipDetailResponse;
import org.example.tdd.repository.MembershipRepository;
import org.example.tdd.domain.Membership;
import org.example.tdd.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    public class MembershipServiceTest {
        private final String userId = "userId";
        private final MembershipType membershipType = MembershipType.NAVER;
        private final Integer point = 10000;

    @InjectMocks
    private MembershipService membershipService;
    @Mock
    private MembershipRepository membershipRepository;

    private Membership membership(){
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .membershipType(MembershipType.NAVER)
                .point(point)
                .build();
    }
    @Test
    public void 멤버쉽등록실패_이미존재함(){
        //given
        doReturn(Membership.builder().build()).when(membershipRepository).findByUserIdAndMembershipType(userId,membershipType);
        //when
        final MembershipException result = assertThrows(MembershipException.class,
                ()->membershipService.addMembership(userId,membershipType,point));
        //then
        assertThat(result.getErrorResult()).isEqualTo
                (MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);

    }

    @Test
    public void 멤버쉽등록성공(){
        //given
        doReturn(null).when(membershipRepository).findByUserIdAndMembershipType(userId,membershipType);
        doReturn(membership()).when(membershipRepository).save(any(Membership.class));
        //when
        final MembershipAddResponse result = membershipService.addMembership(userId,membershipType,point);
        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
        //verify
        verify(membershipRepository,times(1)).findByUserIdAndMembershipType(userId,membershipType);
        verify(membershipRepository,times(1)).save(any(Membership.class));
    }

    @Test
    void 멤버쉽목록조회() {
        //given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findByUserId(userId);
        //when
        final List<MembershipDetailResponse> result = membershipService.getMembershipList(userId);
        //then
        assertThat(result.size()).isEqualTo(3);
    }
    @Test
    void 멤버쉽상세조회실패_존재하지않음() {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);
        //when
        final MembershipException result = assertThat(MembershipException.class,()->membershipService.getMembership(membershipId,userId));
        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    void 멤버쉽상세조회실패_본인이아님() {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);
        //when
        final MembershipException result = assertThat(MembershipException.class,()->membershipService.getMembership(membershipId,"notOwner"));
        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    void 멤버쉽상세조회성공() {
        //given
        doReturn(Optional.of(membership())).when(membershipRepository).findById(membershipId);
        //when
        final MembershipException result = membershipService.getMembership(membershipId,userId);
        //then
        assertThat(result.getMembership()).isEqualto(MembershipType.NAVER);
        assertThat(result.getPoint()).isEqualto(10000);
    }
}

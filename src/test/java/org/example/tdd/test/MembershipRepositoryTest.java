package org.example.tdd.test;

import org.example.tdd.MembershipType;
import org.example.tdd.domain.MembershipDetailResponse;
import org.example.tdd.repository.MembershipRepository;
import org.example.tdd.domain.Membership;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;
import java.util.List;
@DataJpaTest
class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    public void 멤버쉽등록(){
        //given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();
        //when
        final Membership result = membershipRepository.save(membership);
        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
    }

    @Test
    public void 멤버쉽이존재하는지테스트(){
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        final Membership result = membershipRepository.save(membership);
        final Membership findResult = membershipRepository.findByUserIdAndMembershipType("userId",MembershipType.NAVER);

        assertThat(findResult.getId()).isNotNull();
        assertThat(findResult.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(findResult.getUserId()).isEqualTo("userId");
        assertThat(findResult.getPoint()).isEqualTo(10000);
    }

    @Test
    void 멤버쉽조회_사이즈가0() {
        //given
        //when
        List<Membership> result = membershipRepository.findByUserId("userId");
        //then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void 멤버쉽조회_사이즈가2(){
        Membership membership1 = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();
        Membership membership2 = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.LINE)
                .point(10000)
                .build();
        membershipRepository.save(membership1);
        membershipRepository.save(membership2);

        List<Membership> list = membershipRepository.findByUserId("userId");
        assertThat(list.size()).isEqualTo(2);
    }
}

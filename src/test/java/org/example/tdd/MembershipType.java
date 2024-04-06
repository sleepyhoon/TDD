package org.example.tdd;

import lombok.Getter;

@Getter
public enum MembershipType {
    NAVER("네이버"),
    LINE("라인"),
    KAKAO("카카오"),
    ;
    private String companyName;
    MembershipType(String companyName) {
        this.companyName = companyName;
    }
}

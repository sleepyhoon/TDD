package org.example.tdd.test;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.example.tdd.*;
import org.example.tdd.controller.MembershipController;
import org.example.tdd.domain.MembershipAddResponse;
import org.example.tdd.domain.MembershipRequest;
import org.example.tdd.handler.GlobalExceptionHandler;
import org.example.tdd.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.example.tdd.MembershipContents.USER_ID_HEADER;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class MembershipControllerTest1 {
    @InjectMocks
    private MembershipController membershipController;
    @Mock
    private MembershipService membershipService;
    private MockMvc mockMvc;
    private Gson gson;

    @BeforeEach
    void init(){
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(membershipController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void 멤버쉽등록실패_사용자식별값이헤더에없음() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버쉽등록실패_MemberService에서Throws() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .addMembership("12345", MembershipType.NAVER, 10000);
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(10000,MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버쉽등록성공() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        final MembershipAddResponse membershipAddResponse = MembershipAddResponse.builder()
                .id(5L)
                .membershipType(MembershipType.NAVER)
                .build();

        doReturn(membershipAddResponse).when(membershipService).addMembership("12345",MembershipType.NAVER,10000);
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(10000,MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isCreated());
        // 결과 확인을 위한 로깅
        MvcResult mvcResult = resultActions.andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        log.info("Response body: {}", responseBody);

        final MembershipAddResponse response = gson.fromJson(resultActions.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), MembershipAddResponse.class);

        Assertions.assertThat(response.getMembershipType()).isEqualTo(MembershipType.NAVER);
        Assertions.assertThat(response.getId()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalidMembershipAddParameter")
    public void 멤버쉽등록실패_잘못된파라미터(final Integer point, final MembershipType membershipType) throws Exception {
        //given
        final String url = "/api/v1/memberships";
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(point,membershipType)))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        //then
        resultActions.andExpect(status().isBadRequest());
    }

    public MembershipRequest membershipRequest(final Integer point, final MembershipType membershipType){
        return MembershipRequest.builder()
                .point(point)
                .membershipType(membershipType)
                .build();
    }

    private static Stream<Arguments> invalidMembershipAddParameter() {
        return Stream.of(
                Arguments.of(null,MembershipType.NAVER),
                Arguments.of(-1,MembershipType.NAVER),
                Arguments.of(10000,null)
        );
    }
}

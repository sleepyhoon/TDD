package org.example.tdd.test;

import com.google.gson.Gson;
import org.example.tdd.MembershipType;
import org.example.tdd.controller.MembershipController;
import org.example.tdd.domain.Membership;
import org.example.tdd.domain.MembershipDetailResponse;
import org.example.tdd.domain.MembershipRequest;
import org.example.tdd.error.MembershipErrorResult;
import org.example.tdd.error.MembershipException;
import org.example.tdd.handler.GlobalExceptionHandler;
import org.example.tdd.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.metadata.HsqlTableMetaDataProvider;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.example.tdd.MembershipContents.USER_ID_HEADER;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTest2 {
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
    void 멤버쉽조회실패_사용자식별값이헤더에없음() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
        );
        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버쉽조회성공() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        doReturn(Arrays.asList(
                MembershipDetailResponse.builder().build(),
                MembershipDetailResponse.builder().build(),
                MembershipDetailResponse.builder().build()
        )).when(membershipService).getMembershipList("12345");
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER,"12345")
        );
        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 멤버쉽상세조회실패_사용자식별값이헤더에없음() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );
        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버쉽상세조회실패_멤버쉽이존재하지않음() throws Exception {
        //given
        final String url = "/api/v1/memberships/-1";
        doThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND))
                .when(membershipService)
                .getMembership(-1L,"12345");
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER,"12345")
        );
        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void 멤버쉽상세조회성공() throws Exception {
        //given
        final String url = "/api/v1/memberships/-1";
        doReturn(MembershipDetailResponse.builder().build()).when(membershipService).getMembership(-1L,"12345");
        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER,"12345")
                        .param("membershipType", MembershipType.NAVER.name())
        );
        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 멤버쉽삭제실패_사용자식별값이없음() throws Exception {
        final String url = "/api/v1/memberships/-1";

        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
        );
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버쉽삭제성공() throws Exception {
        final String url = "/api/v1/memberships/-1";

        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header(USER_ID_HEADER,"12345")
        );

        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void 멤버쉽적립실패_사용자식별값이없음() throws Exception {
        final String url = "/api/v1/memberships/-1/accumulate";

        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipRequest(10000)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버쉽적립실패_포인트가음수() throws Exception {
        final String url = "/api/v1/memberships/-1/accumulate";

        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(-1)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void 멤버쉽적립성공() throws Exception {
        final String url = "/api/v1/memberships/-1/accumulate";

        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(10000)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isNoContent());
    }

    private MembershipRequest membershipRequest(final Integer point) {
        return MembershipRequest.builder()
                .point(point)
                .build();
    }
}

package zerobase.group2.cookingRecipe.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
import zerobase.group2.cookingRecipe.member.service.MemberService;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successRegister() throws Exception {
        //given
        given(memberService.register(any()))
            .willReturn(MemberDto.builder()
                .email("group2@gmail.com")
                .name("그룹2")
                .build()
            );
        //when
        //then
        mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new MemberRegister.Request("email", "pw", "name")
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.body.email").value("group2@gmail.com"))
            .andExpect(jsonPath("$.body.name").value("그룹2"))
            .andDo(print());
    }

    @Test
    void successEmailAuth() throws Exception {
        //given
        String uuid = UUID.randomUUID().toString();
//        given(memberService.emailAuth(anyString()))
//            .wil

        //when
        //then
        mockMvc.perform(get("/member/email-auth?key=" + uuid))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.body").value(true));
    }
}
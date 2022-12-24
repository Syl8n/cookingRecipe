package zerobase.group2.cookingRecipe.member.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import zerobase.group2.cookingRecipe.Security.authProvider.JwtProvider;
import zerobase.group2.cookingRecipe.Security.config.SecurityConfig;
import zerobase.group2.cookingRecipe.Security.filter.JwtAuthFilter;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
import zerobase.group2.cookingRecipe.member.service.MemberService;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtProvider jwtProvider;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    SecurityConfig securityConfig;

    @MockBean
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation))
            .build();
    }

    @Test
    public void successRegister() throws Exception {
        //given
        given(memberService.register(anyString(), anyString(), anyString()))
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
            .andDo(print())
            .andDo(document("member",
                requestFields(
                    fieldWithPath("email").description("사용자 이메일"),
                    fieldWithPath("password").description("비밀번호"),
                    fieldWithPath("name").description("닉네임")
                ),
                responseFields(
                    fieldWithPath("status").description("응답코드"),
                    fieldWithPath("body.email").description("등록된 이메일"),
                    fieldWithPath("body.name").description("등록된 닉네임")
                )));
    }

    @Test
    public void successEmailAuth() throws Exception {
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
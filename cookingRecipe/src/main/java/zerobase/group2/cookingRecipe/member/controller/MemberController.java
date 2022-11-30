package zerobase.group2.cookingRecipe.member.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
import zerobase.group2.cookingRecipe.member.service.MemberService;
import zerobase.group2.cookingRecipe.model.ResponseResult;
import zerobase.group2.cookingRecipe.type.StatusCode;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/register")
    public ResponseResult memberRegister(@RequestBody @Valid MemberRegister.Request request) {
        MemberDto memberDto = memberService.register(request);
        return ResponseResult.builder()
            .status(StatusCode.OK.getCode())
            .body(memberDto)
            .build();
    }

    @GetMapping("/member/email-auth")
    public ResponseResult emailAuth(@RequestParam("key") String key) {
        boolean result = memberService.emailAuth(key);
        return ResponseResult.builder()
            .status(StatusCode.OK.getCode())
            .body(result)
            .build();

    }
}

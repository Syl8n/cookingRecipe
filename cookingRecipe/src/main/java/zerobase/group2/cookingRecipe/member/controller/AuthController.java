package zerobase.group2.cookingRecipe.member.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
import zerobase.group2.cookingRecipe.member.dto.ResetPassword;
import zerobase.group2.cookingRecipe.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseResult register(@RequestBody @Valid MemberRegister.Request request) {
        MemberDto memberDto = memberService.register(
            request.getEmail(),
            request.getPassword(),
            request.getName()
        );
        return ResponseResult.ok(MemberRegister.Response.from(memberDto));
    }

    @GetMapping("/email-auth")
    public ResponseResult emailAuth(@RequestParam("key") String key) {
        memberService.emailAuth(key);
        return ResponseResult.ok(true);
    }

    @PostMapping("/reset-password")
    public ResponseResult resetPasswordRequest(@RequestBody String email) {
        return ResponseResult.ok(memberService.sendEmailToResetPassword(email));
    }

    @GetMapping("/reset-password")
    public ResponseResult resetPasswordProceeding(@RequestParam("key") String key) {
        return ResponseResult.ok(memberService.authPasswordResetKey(key));
    }

    @PutMapping("/reset-password")
    public ResponseResult resetPasswordProceeding(
        @RequestBody @Valid ResetPassword.Request request) {
        memberService.processResetPassword(request.getEmail(), request.getPassword());
        return ResponseResult.ok(true);
    }
}

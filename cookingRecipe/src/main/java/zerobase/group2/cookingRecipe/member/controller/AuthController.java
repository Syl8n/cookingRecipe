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
import zerobase.group2.cookingRecipe.Security.authProvider.JwtProvider;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.member.dto.JwtIssue;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
import zerobase.group2.cookingRecipe.member.dto.ResetPassword;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public ResponseResult register(@RequestBody @Valid MemberRegister.Request request) {
        MemberDto memberDto = memberService.register(
            request.getEmail(),
            request.getPassword(),
            request.getName()
        );
        return ResponseResult.ok(MemberRegister.Response.from(memberDto));
    }

    @PostMapping("/reissue")
    public ResponseResult reissue(@RequestBody @Valid JwtIssue request) {
        if(!jwtProvider.validateToken(request.getRefreshToken())){
            throw new CustomException(ErrorCode.TOKEN_NOT_VALID);
        }
        Member member = memberService.attemptJwtReissue(jwtProvider.getAuthentication(
            request.getAccessToken()).getName(), request.getRefreshToken());

        JwtIssue jwtIssue = jwtProvider.generateTokens(member.getEmail(), member.getRoles());

        memberService.putRefreshToken(member.getEmail(), jwtIssue.getRefreshToken());

        return ResponseResult.ok(jwtIssue);
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

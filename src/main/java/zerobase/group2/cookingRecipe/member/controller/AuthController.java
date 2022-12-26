package zerobase.group2.cookingRecipe.member.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import zerobase.group2.cookingRecipe.Security.authProvider.JwtProvider;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.member.dto.*;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.service.MemberService;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = {"인증용 api (JWT를 필요로하지 않습니다)"})
public class AuthController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    @ApiOperation(value = "회원가입을 해 DB에 정보를 입력합니다")
    public ResponseResult register(@RequestBody @Valid
                                   RegisterRequest request) {
        MemberDto memberDto = memberService.register(
                request.getEmail(),
                request.getPassword(),
                request.getName()
        );
        return ResponseResult.ok(RegisterResponse.from(memberDto));
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인을 해 JWT를 발행합니다")
    public ResponseResult login(@RequestBody @Valid
                                LoginRequest request) {
        Member member = memberService.authenticate(request.getUsername(), request.getPassword());
        JwtIssue jwtIssue = jwtProvider.generateTokens(member.getUsername(), member.getRoles());
        memberService.putRefreshToken(member.getUsername(), jwtIssue.getRefreshToken());
        return ResponseResult.ok(jwtIssue);
    }

    @PostMapping("/reissue")
    @ApiOperation(value = "토큰 만료 시 재발행용 API입니다")
    public ResponseResult reissue(@RequestBody @Valid JwtIssue request) {
        if (!jwtProvider.validateRefreshToken(request.getRefreshToken())) {
            throw new CustomException(ErrorCode.TOKEN_NOT_VALID);
        }

        Authentication authentication = jwtProvider.getAuthentication(request.getAccessToken());
        String tokenInCache = memberService.getRefreshToken(authentication.getName());

        memberService.validateJwtReissue(tokenInCache, request.getRefreshToken());

        JwtIssue jwtIssue = jwtProvider.generateTokens(authentication.getName(),
                authentication.getAuthorities().stream().map(Object::toString).collect(
                        Collectors.toList()));

        memberService.putRefreshToken(authentication.getName(), jwtIssue.getRefreshToken());

        return ResponseResult.ok(jwtIssue);
    }

    @GetMapping("/email-auth")
    public ResponseResult emailAuth(@RequestParam("key") String key) {
        memberService.emailAuth(key);
        return ResponseResult.ok(true);
    }

    @PostMapping("/reset-password")
    @ApiOperation(value = "비밀번호 초기화용 메일을 보냅니다")
    public ResponseResult resetPasswordRequest(@RequestBody @ApiParam("회원가입한 이메일") String email) {
        return ResponseResult.ok(memberService.sendEmailToResetPassword(email));
    }

    @GetMapping("/reset-password")
    public ResponseResult resetPasswordProceeding(@RequestParam("key") String key) {
        return ResponseResult.ok(memberService.authPasswordResetKey(key));
    }

    @PutMapping("/reset-password")
    @ApiOperation(value = "비밀번호를 초기화합니다",
            notes = "초기화용 key가 일치하지 않으면 예외 발생하므로 메일 송신부터 해야 확인 가능합니다")
    public ResponseResult resetPasswordProceeding(
            @RequestBody @Valid ResetPasswordRequest request) {
        memberService.processResetPassword(request.getEmail(), request.getNewPassword(), request.getKey());
        return ResponseResult.ok(true);
    }
}

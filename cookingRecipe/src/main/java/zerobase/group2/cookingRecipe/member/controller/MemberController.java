package zerobase.group2.cookingRecipe.member.controller;

import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.member.dto.EditMemberInfo;
import zerobase.group2.cookingRecipe.member.dto.EditPassword;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.dto.MemberRegister;
import zerobase.group2.cookingRecipe.member.dto.ResetPassword;
import zerobase.group2.cookingRecipe.member.service.MemberService;
import zerobase.group2.cookingRecipe.model.ResponseResult;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/register")
    public ResponseResult memberRegister(@RequestBody @Valid MemberRegister.Request request) {
        MemberDto memberDto = memberService.register(request);
        return ResponseResult.ok(MemberRegister.Response.from(memberDto));
    }

    @GetMapping("/member/email-auth")
    public ResponseResult emailAuth(@RequestParam("key") String key) {
        memberService.emailAuth(key);
        return ResponseResult.ok(true);

    }

    @GetMapping("/member/info")
    public ResponseResult memberInfo(Principal principal){
        MemberDto memberDto = memberService.getInfoById(principal.getName());
        return ResponseResult.ok(memberDto);
    }

    @PutMapping("/member/info")
    public ResponseResult editMemberInfo(Principal principal,
        @RequestBody @Valid EditMemberInfo.Request request){
        MemberDto memberDto = memberService.editMemberInfo(principal.getName(), request);
        return ResponseResult.ok(memberDto);
    }

    @PutMapping("/member/edit-password")
    public ResponseResult editPassword(Principal principal,
        @RequestBody @Valid EditPassword.Request request){
        memberService.editPassword(principal.getName(), request);
        return ResponseResult.ok(true);
    }

    @DeleteMapping("/member/withdraw")
    public ResponseResult withdraw(Principal principal,
        @RequestBody String password){
        memberService.withdraw(principal.getName(), password);
        return ResponseResult.ok(true);
    }

    @PostMapping("/member/reset-password")
    public ResponseResult resetPasswordRequest(@RequestBody String email){
        return ResponseResult.ok(memberService.sendEmailToResetPassword(email));
    }

    @GetMapping("/member/reset-password")
    public ResponseResult resetPasswordProceeding(@RequestParam("key") String key) {
        return ResponseResult.ok(memberService.authPasswordResetKey(key));
    }

    @PutMapping("/member/reset-password")
    public ResponseResult resetPasswordProceeding(
        @RequestBody @Valid ResetPassword.Request request) {
        memberService.processResetPassword(request.getEmail(), request.getPassword());
        return ResponseResult.ok(true);
    }

//    @GetMapping("/member/info/likes")
//    public ResponseResult memberLikes(Principal principal){
//        MemberDto memberDto = memberService.getInfoById(principal.getName());
//        return ResponseResult.builder()
//            .status(StatusCode.OK.getCode())
//            .body(memberDto)
//            .build();
//    }
}

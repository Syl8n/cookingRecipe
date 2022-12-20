package zerobase.group2.cookingRecipe.member.controller;

import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.member.dto.EditMemberInfo;
import zerobase.group2.cookingRecipe.member.dto.EditPassword;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseResult memberInfo(Principal principal){
        MemberDto memberDto = memberService.getInfoById(principal.getName());
        return ResponseResult.ok(memberDto);
    }

    @PutMapping("/info")
    public ResponseResult editMemberInfo(Principal principal,
        @RequestBody @Valid EditMemberInfo.Request request){
        MemberDto memberDto = memberService.editMemberInfo(principal.getName(), request.getName());
        return ResponseResult.ok(memberDto);
    }

    @PutMapping("/edit-password")
    public ResponseResult editPassword(Principal principal,
        @RequestBody @Valid EditPassword.Request request){
        memberService.editPassword(principal.getName(),
            request.getOldPassword(), request.getNewPassword());
        return ResponseResult.ok(true);
    }

    @DeleteMapping("/withdraw")
    public ResponseResult withdraw(Principal principal,
        @RequestBody String password){
        memberService.withdraw(principal.getName(), password);
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

package zerobase.group2.cookingRecipe.member.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.member.dto.EditMemberInfoRequest;
import zerobase.group2.cookingRecipe.member.dto.EditPasswordRequest;
import zerobase.group2.cookingRecipe.member.dto.MemberDto;
import zerobase.group2.cookingRecipe.member.service.MemberService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Api("회원용 api")
public class MemberController {

    private final MemberService memberService;

    // 로그아웃은 로그인이 되어 있는 상태라 JwtFilter를 통과해야 하기 때문에 필터의 영향을 받는 MemberController에 위치.
    @GetMapping("/logout")
    @ApiOperation("로그아웃을 하고 재발행용 토큰을 삭제합니다")
    public ResponseResult logout(Principal principal) {
        return ResponseResult.ok("삭제완료: " + memberService.deleteRefreshToken(principal.getName()));
    }

    @GetMapping("/info")
    @ApiOperation("회원 정보를 받아옵니다")
    public ResponseResult memberInfo(Principal principal) {
        MemberDto memberDto = memberService.getInfoById(principal.getName());
        return ResponseResult.ok(memberDto);
    }

    @PutMapping("/info")
    @ApiOperation("회원 정보를 수정합니다. 현재는 닉네임 정도 밖에 수정할 게 없습니다")
    public ResponseResult editMemberInfo(Principal principal,
        @RequestBody @Valid @ApiParam("닉네임") EditMemberInfoRequest request) {
        MemberDto memberDto = memberService.editMemberInfo(principal.getName(), request.getName());
        return ResponseResult.ok(memberDto);
    }

    @PutMapping("/edit-password")
    @ApiOperation("비밀번호를 변경합니다")
    public ResponseResult editPassword(Principal principal,
        @RequestBody @Valid @ApiParam("이전 비밀번호, 새 비밀번호") EditPasswordRequest request) {
        memberService.editPassword(principal.getName(),
            request.getOldPassword(), request.getNewPassword());
        return ResponseResult.ok(true);
    }

    @DeleteMapping("/withdraw")
    @ApiOperation("회원 탈퇴를 합니다")
    public ResponseResult withdraw(Principal principal,
        @RequestBody @ApiParam("비밀번호") String password) {
        memberService.withdraw(principal.getName(), password);
        return ResponseResult.ok(true);
    }

    @GetMapping("/likes")
    @ApiOperation("찜 한 레시피들을 받아옵니다")
    public ResponseResult getLikes(Principal principal){
        List<String> likes = memberService.getLikes(principal.getName());
        return ResponseResult.ok(likes);
    }
}

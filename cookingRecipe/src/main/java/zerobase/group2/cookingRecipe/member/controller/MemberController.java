package zerobase.group2.cookingRecipe.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.member.service.MemberService;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
}

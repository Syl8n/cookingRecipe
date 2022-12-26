package zerobase.group2.cookingRecipe.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import zerobase.group2.cookingRecipe.comment.entity.Comment;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    @ApiModelProperty("회원 ID")
    private String email;
    @ApiModelProperty("회원 닉네임")
    private String name;
    @ApiModelProperty("회원 가입일")
    private LocalDateTime registeredAt;
    @ApiModelProperty("회원 정보 수정일")
    private LocalDateTime updatedAt;
    private List<LikeEntity> likeEntityList;
    private List<Comment> commentList;
    private List<Rating> ratingList;

    public static MemberDto from(Member member){
        return MemberDto.builder()
            .email(member.getEmail())
            .name(member.getName())
            .registeredAt(member.getRegisteredAt())
            .updatedAt(member.getUpdatedAt())
            .likeEntityList(member.getLikeEntityList())
            .commentList(member.getCommentList())
            .ratingList(member.getRatingList())
            .build();
    }
}

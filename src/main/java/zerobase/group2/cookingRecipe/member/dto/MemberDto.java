package zerobase.group2.cookingRecipe.member.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.comment.entity.Comment;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    private String email;
    private String name;
    private LocalDateTime registeredAt;
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

package zerobase.group2.cookingRecipe.comment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.comment.entity.Comment;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private long id;
    private String memberEmail;
    private String recipeTitle;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public static CommentDto from(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .memberEmail(comment.getMember().getEmail())
                .recipeTitle(comment.getRecipe().getTitle())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .updateAt(comment.getUpdateAt())
                .build();
    }
}

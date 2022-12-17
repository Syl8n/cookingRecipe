package zerobase.group2.cookingRecipe.member.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.group2.cookingRecipe.comment.entity.Comment;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    private String email;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private String emailAuthKey;
    private LocalDateTime emailAuthDue;
    private boolean emailAuthYn;

    private String passwordResetKey;
    private LocalDateTime passwordResetDue;

    private boolean admin;

    @CreatedDate
    private LocalDateTime registeredAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "member",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private List<LikeEntity> likeEntityList;

    @OneToMany(mappedBy = "member",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "member",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private List<Rating> ratingList;

    public boolean validatePassword(String pw) {
        return !password.equals(pw);
    }

    public boolean validateKeyAndDue() {
        return !Objects.isNull(passwordResetKey) && passwordResetKey.length() > 0 &&
            LocalDateTime.now().isBefore(passwordResetDue);
    }
}

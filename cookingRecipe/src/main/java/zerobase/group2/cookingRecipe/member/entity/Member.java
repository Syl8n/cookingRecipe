package zerobase.group2.cookingRecipe.member.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    String email;
    String password;
    String name;

    @Enumerated(EnumType.STRING)
    MemberStatus status;

    String emailAuthKey;
    LocalDateTime emailAuthDue;
    boolean emailAuthYn;

    String passwordResetKey;
    LocalDateTime passwordResetDue;

    boolean admin;

    @CreatedDate
    LocalDateTime registeredAt;
    @LastModifiedDate
    LocalDateTime updatedAt;

    public boolean validatePassword(String pw){
        return !password.equals(pw);
    }

    public boolean validateKeyAndDue() {
        return !Objects.isNull(passwordResetKey) && passwordResetKey.length() > 0 &&
            LocalDateTime.now().isBefore(passwordResetDue);
    }
}

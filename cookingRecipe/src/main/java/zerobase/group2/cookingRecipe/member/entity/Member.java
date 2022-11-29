package zerobase.group2.cookingRecipe.member.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
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
import org.springframework.stereotype.Service;
import zerobase.group2.cookingRecipe.member.type.MemberStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member {
    @Id
    String email;
    String password;
    String name;

    @Enumerated(EnumType.STRING)
    MemberStatus status;

    String email_auth_key;
    LocalDateTime email_auth_dt;
    boolean email_auth_yn;

    @CreatedDate
    LocalDateTime registeredAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
}

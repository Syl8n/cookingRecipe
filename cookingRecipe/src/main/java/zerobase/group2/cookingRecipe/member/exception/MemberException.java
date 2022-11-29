package zerobase.group2.cookingRecipe.member.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.member.type.MemberError;

@Getter
@Setter
@Builder
public class MemberException extends RuntimeException{
    private MemberError error;

    public MemberException(MemberError error){
        super(error.getDescription());
        this.error = error;
    }
}

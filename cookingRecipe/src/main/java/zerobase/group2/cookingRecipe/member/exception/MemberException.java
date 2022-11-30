package zerobase.group2.cookingRecipe.member.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zerobase.group2.cookingRecipe.type.ErrorCode;

@Getter
@Setter
@Builder
public class MemberException extends RuntimeException{
    private ErrorCode error;

    public MemberException(ErrorCode error){
        super(error.getDescription());
        this.error = error;
    }
}

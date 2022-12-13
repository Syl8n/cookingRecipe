package zerobase.group2.cookingRecipe.common.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;

@Getter
@Setter
@Builder
public class CustomException extends RuntimeException{
    private ErrorCode error;

    public CustomException(ErrorCode error){
        super(error.getDescription());
        this.error = error;
    }
}

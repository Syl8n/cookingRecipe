package zerobase.group2.cookingRecipe.common.model;

import lombok.AllArgsConstructor;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;

@AllArgsConstructor
public class ExceptionResult {
    ErrorCode errorCode;
    String message;
}

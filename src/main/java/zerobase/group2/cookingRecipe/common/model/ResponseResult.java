package zerobase.group2.cookingRecipe.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zerobase.group2.cookingRecipe.common.type.StatusCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseResult {
    private StatusCode status;
    private Object body;

    public static ResponseResult ok(Object obj){
        return ResponseResult.builder()
            .status(StatusCode.OK)
            .body(obj)
            .build();
    }
}

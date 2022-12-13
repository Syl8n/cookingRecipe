package zerobase.group2.cookingRecipe.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.api.service.OpenApiRecipeClient;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;

@RestController
@RequiredArgsConstructor
public class ApiController {
    private final OpenApiRecipeClient openApiRecipeClient;

    @GetMapping("/api")
    public ResponseResult getApiData(){
        // 추후 Spring Batch로 구현할 예정이지만, 연결된 다른 서비스들의 테스트를 위해 임시로 api 작성.
        openApiRecipeClient.getRecipeFromApi();
        return ResponseResult.ok(true);
    }
}

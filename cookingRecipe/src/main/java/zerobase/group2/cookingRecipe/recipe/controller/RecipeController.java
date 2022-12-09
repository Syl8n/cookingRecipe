package zerobase.group2.cookingRecipe.recipe.controller;

import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.group2.cookingRecipe.common.model.ResponseResult;
import zerobase.group2.cookingRecipe.recipe.dto.RecipeInput;
import zerobase.group2.cookingRecipe.recipe.service.RecipeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/create")
    public ResponseResult createRecipe(
        @RequestBody @Valid RecipeInput.Request request, Principal principal){
        return ResponseResult.ok(
            recipeService.createRecipe(
                request.getTitle(),
                request.getMainImagePathSmall(),
                request.getMainImagePathBig(),
                request.getType1(),
                request.getType2(),
                request.getIngredients(),
                request.getKcal(),
                request.getManual(),
                request.getManualImagePath(),
                principal.getName()
            )
        );
    }

    @GetMapping("/{recipeId}")
    public ResponseResult readRecipe(@PathVariable String recipeId){
        return ResponseResult.ok(recipeService.readRecipe(recipeId));
    }

    @GetMapping("/edit/{recipeId}")
    public ResponseResult requestEditRecipe(@PathVariable String recipeId,
        Principal principal){
        return ResponseResult.ok(recipeService.requestEditRecipe(recipeId, principal.getName()));
    }

    @PutMapping("/edit/{recipeId}")
    public ResponseResult processEditRecipe(
        @PathVariable String recipeId,
        @RequestBody RecipeInput.Request request,
        Principal principal){
        return ResponseResult.ok(
            recipeService.processEditRecipe(
                recipeId,
                request.getTitle(),
                request.getMainImagePathSmall(),
                request.getMainImagePathBig(),
                request.getType1(),
                request.getType2(),
                request.getIngredients(),
                request.getKcal(),
                request.getManual(),
                request.getManualImagePath(),
                principal.getName()
            )
        );
    }

    @DeleteMapping("/delete/{recipeId}")
    public ResponseResult deleteRecipe(@PathVariable String recipeId,
        Principal principal){
        return ResponseResult.ok(recipeService.deleteRecipe(recipeId, principal.getName()));
    }
}

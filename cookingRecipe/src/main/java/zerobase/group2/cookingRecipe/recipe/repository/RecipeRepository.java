package zerobase.group2.cookingRecipe.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {

}

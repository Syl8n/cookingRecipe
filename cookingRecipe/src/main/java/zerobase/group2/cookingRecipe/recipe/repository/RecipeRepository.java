package zerobase.group2.cookingRecipe.recipe.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>,
    JpaSpecificationExecutor<Recipe> {

    Optional<Recipe> findByVisualId(String visualId);
    boolean existsById(Long id);
}

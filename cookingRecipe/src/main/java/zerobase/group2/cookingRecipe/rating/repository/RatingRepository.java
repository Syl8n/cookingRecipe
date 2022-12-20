package zerobase.group2.cookingRecipe.rating.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.entity.MemberRecipeCompKey;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;

@Repository
public interface RatingRepository extends JpaRepository<Rating, MemberRecipeCompKey> {
    Optional<Rating> findByMemberAndRecipe(Member member, Recipe recipe);
}

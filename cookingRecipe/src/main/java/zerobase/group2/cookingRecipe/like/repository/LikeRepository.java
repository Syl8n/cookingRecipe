package zerobase.group2.cookingRecipe.like.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.member.entity.Member;
import zerobase.group2.cookingRecipe.member.entity.MemberRecipeCompKey;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, MemberRecipeCompKey> {
    Optional<LikeEntity> findByMemberAndRecipe(Member member, Recipe recipe);

    List<LikeEntity> findAllByMember(Member member);
    void deleteAllByRecipe(Recipe recipe);

    boolean existsByMemberAndRecipe(Member member, Recipe recipe);
}

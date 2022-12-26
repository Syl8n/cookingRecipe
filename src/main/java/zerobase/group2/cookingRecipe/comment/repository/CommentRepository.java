package zerobase.group2.cookingRecipe.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.group2.cookingRecipe.comment.entity.Comment;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByRecipe(Recipe recipe);
    void deleteAllByRecipe(Recipe recipe);
}

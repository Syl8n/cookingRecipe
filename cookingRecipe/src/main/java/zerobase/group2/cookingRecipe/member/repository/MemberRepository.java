package zerobase.group2.cookingRecipe.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.group2.cookingRecipe.member.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

}

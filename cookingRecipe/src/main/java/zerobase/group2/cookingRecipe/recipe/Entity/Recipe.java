package zerobase.group2.cookingRecipe.recipe.Entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.group2.cookingRecipe.comment.entity.Comment;
import zerobase.group2.cookingRecipe.like.entity.LikeEntity;
import zerobase.group2.cookingRecipe.rating.Entity.Rating;
import zerobase.group2.cookingRecipe.recipe.converter.RecipeConverter;
import zerobase.group2.cookingRecipe.recipe.type.RecipeStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Recipe {

    @Id
    private Long id;// RCP_SEQ
    private String visualId;
    private String title; // RCP_NM
    private String mainImagePathSmall; // ATT_FILE_NO_MAIN
    private String mainImagePathBig; // ATT_FILE_NO_MK
    private String type1; // RCP_WAY2
    private String type2; // RCP_PAT2

    @Column(length = 1000)
    private String ingredients; // RCP_PARTS_DTLS
    private double kcal; // INFO_ENG

    @Column(length = 1000)
    @Convert(converter = RecipeConverter.class)
    private List<String> manual;

    @Column(length = 1000)
    @Convert(converter = RecipeConverter.class)
    private List<String> manualImagePath;

    @Enumerated(EnumType.STRING)
    private RecipeStatus status;
    private long views;

    private String email;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "recipe",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private List<LikeEntity> likeEntityList;
    private long likeCount;

    @OneToMany(mappedBy = "recipe",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "member",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY)
    private List<Rating> ratingList;
    private long totalScore;
    private long ratingCount;

    public Recipe(long id){
        this.id = id;
        this.visualId = UUID.randomUUID().toString().replace("-", "");
    }

    public void fill(JSONObject jsonObject, List<String> manual, List<String> manualImagePath,
        String memberEmail) {
        this.title = (String) jsonObject.get("RCP_NM");
        this.mainImagePathSmall = (String) jsonObject.get("ATT_FILE_NO_MAIN");
        this.mainImagePathBig = (String) jsonObject.get("ATT_FILE_NO_MK");
        this.type1 = (String) jsonObject.get("RCP_WAY2");
        this.type2 = (String) jsonObject.get("RCP_PAT2");
        this.ingredients = (String) jsonObject.get("RCP_PARTS_DTLS");
        this.kcal = Double.parseDouble((String) jsonObject.get("INFO_ENG"));
        this.manual = manual;
        this.manualImagePath = manualImagePath;
        this.status = RecipeStatus.REGISTERED;
        this.email = memberEmail;
    }
}

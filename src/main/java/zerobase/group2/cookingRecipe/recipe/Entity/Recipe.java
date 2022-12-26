package zerobase.group2.cookingRecipe.recipe.Entity;

import lombok.*;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import zerobase.group2.cookingRecipe.recipe.converter.RecipeConverter;
import zerobase.group2.cookingRecipe.recipe.type.RecipeStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // RCP_SEQ
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
    private long likeCount;
    private long totalScore;
    private long ratingCount;

    public Recipe(long id) {
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

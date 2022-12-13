package zerobase.group2.cookingRecipe.recipe.Entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
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
    private String id;
    private Long seq;// RCP_SEQ
    private String title; // RCP_NM
    private String mainImagePathSmall; // ATT_FILE_NO_MAIN
    private String mainImagePathBig; // ATT_FILE_NO_MK
    private String type1; // RCP_WAY2
    private String type2; // RCP_PAT2

    @Column(length=1000)
    private String ingredients; // RCP_PARTS_DTLS
    private double kcal; // INFO_ENG

    @Column(length=1000)
    @Convert(converter = RecipeConverter.class)
    private List<String> manual;

    @Column(length=1000)
    @Convert(converter = RecipeConverter.class)
    private List<String> manualImagePath;

    private RecipeStatus status;

    private String email;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public static Recipe from(JSONObject jsonObject, List<String> manual, List<String> manualImagePath,
                                String user){
        return Recipe.builder()
            .id(UUID.randomUUID().toString().replace("-", ""))
            .seq(Long.parseLong((String) jsonObject.get("RCP_SEQ")))
            .title((String) jsonObject.get("RCP_NM"))
            .mainImagePathSmall((String) jsonObject.get("ATT_FILE_NO_MAIN"))
            .mainImagePathBig((String) jsonObject.get("ATT_FILE_NO_MK"))
            .type1((String) jsonObject.get("RCP_WAY2"))
            .type2((String) jsonObject.get("RCP_PAT2"))
            .ingredients((String) jsonObject.get("RCP_PARTS_DTLS"))
            .kcal(Double.parseDouble((String) jsonObject.get("INFO_ENG")))
            .manual(manual)
            .manualImagePath(manualImagePath)
            .status(RecipeStatus.REGISTERED)
            .email(user)
            .build();
    }
}

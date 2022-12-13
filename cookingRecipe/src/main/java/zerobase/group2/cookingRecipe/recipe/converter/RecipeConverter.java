package zerobase.group2.cookingRecipe.recipe.converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RecipeConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = "\n";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return String.join(SPLIT_CHAR, attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(SPLIT_CHAR))
            .collect(Collectors.toList());
    }
}

package zerobase.group2.cookingRecipe.api.utils;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;

public class ApiUtil {

    public static final String COMPANY_EMAIL = "ez.cooking.recipe@gmail.com";
    public static final String API_URL = "http://openapi.foodsafetykorea.go.kr/api/";
    public static final String API_TABLE = "COOKRCP01";
    public static final String API_RESPONSE_TYPE = "/json/";
    public static final int NUM_OF_RECORD = 999;
    public static final String MANUAL = "MANUAL";
    public static final String MANUAL_IMG = "MANUAL_IMG";

    public static List<Recipe> parseRecipe(JSONArray recipeJson, RecipeRepository recipeRepository) {
        List<Recipe> list = new ArrayList<>();
        for (Object obj : recipeJson) {
            JSONObject jsonObject = (JSONObject) obj;
            List<String> manuals = fieldToList(jsonObject, MANUAL);
            List<String> manualImages = fieldToList(jsonObject, MANUAL_IMG);

            long recipeId = Long.parseLong((String) jsonObject.get("RCP_SEQ"));
            Recipe recipe = recipeRepository.findById(recipeId)
                .orElse(new Recipe(recipeId));
            recipe.fill(jsonObject, manuals, manualImages, COMPANY_EMAIL);
            list.add(recipe);
        }
        return list;
    }

    private static List<String> fieldToList(JSONObject jsonObject, String fieldName) {
        // DB의 field가 난잡하지 않게 20개로 분산 저장되어 있는 메뉴얼들을 List로 묶음.

        List<String> list = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String s = (String) jsonObject.get(fieldName + (i < 10 ? "0" + i : i));
            if (s.length() > 0) {
                list.add(s);
            }
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public static JSONArray getRecipeString(String apiKey) {
        int start = 1;
        int end = 1;
        int maxEnd = 1;
        JSONArray recipes = new JSONArray();
        do {
            String apiUrl = API_URL + apiKey + "/" + API_TABLE + API_RESPONSE_TYPE + start + "/" + end;
            try {
                RestTemplate restTemplate = new RestTemplate();
                // 20개의 필드로 분산되어 있는 메뉴얼 부분을 반복문으로 쉽게 처리하기 위해 String으로 받아오기로 함.
                ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, String.class);

                JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.getBody());
                jsonObject = (JSONObject) jsonObject.get("COOKRCP01");

                if (end == 1) {
                    maxEnd = Integer.parseInt((String) jsonObject.get("total_count"));
                } else {
                    recipes.addAll((JSONArray) jsonObject.get("row"));
                }

                start = end + 1;
                end = Math.min(start + NUM_OF_RECORD, maxEnd);

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } while (start <= maxEnd);

        return recipes;
    }
}

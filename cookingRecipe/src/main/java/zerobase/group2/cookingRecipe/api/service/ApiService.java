package zerobase.group2.cookingRecipe.api.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.group2.cookingRecipe.recipe.Entity.Recipe;
import zerobase.group2.cookingRecipe.recipe.repository.RecipeRepository;

@Service
@RequiredArgsConstructor
public class ApiService {

    public static final String COMPANY_EMAIL = "ez.cooking.recipe@gmail.com";
    private final RecipeRepository recipeRepository;

    @Value("${api-key}")
    private String apiKey;

    public void getRecipeFromApi() {
        JSONArray recipeJson = getRecipeString();
        List<Recipe> recipes = parseRecipe(recipeJson);
        recipeRepository.saveAll(recipes);
    }

    private List<Recipe> parseRecipe(JSONArray recipeJson) {
        List<Recipe> list = new ArrayList<>();
        for (Object obj : recipeJson) {
            JSONObject jsonObject = (JSONObject) obj;
            String[] manualStr = manualToString(jsonObject);
            list.add(Recipe.from(jsonObject, manualStr[0], manualStr[1], COMPANY_EMAIL));
        }
        return list;
    }

    private String[] manualToString(JSONObject jsonObject) {
        // DB의 field가 난잡하지 않게 20개로 분산 저장되어 있는 메뉴얼들을 하나로 통합.
        // 리턴값 = [메뉴얼 통합 문자열, 메뉴얼 이미지 경로 통합 문자열]

        StringBuilder sbManual = new StringBuilder();
        StringBuilder sbManualImagePath = new StringBuilder();

        for (int i = 1; i <= 20; i++) {
            String s = (String) jsonObject.get("MANUAL" + (i < 10 ? "0" + i : i));
            if (s.length() > 0) {
                if (sbManual.length() > 0) {
                    sbManual.append("\n");
                }
                sbManual.append(s);
            }
            s = (String) jsonObject.get("MANUAL_IMG" + (i < 10 ? "0" + i : i));
            if (s.length() > 0) {
                if (sbManualImagePath.length() > 0) {
                    sbManualImagePath.append("\n");
                }
                sbManualImagePath.append(s);
            }
        }
        return new String[]{sbManual.toString(), sbManualImagePath.toString()};
    }

    private JSONArray getRecipeString() {
        int start = 1;
        int end = 1;
        int maxEnd = 1;
        JSONArray recipes = null;
        do {
            String apiUrl = "http://openapi.foodsafetykorea.go.kr/api/" + apiKey
                + "/COOKRCP01/json/" + start + "/" + end;
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();

                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();

                JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.toString());
                jsonObject = (JSONObject) jsonObject.get("COOKRCP01");

                if (end == 1) {
                    maxEnd = Integer.parseInt((String) jsonObject.get("total_count"));
                } else {
                    if (recipes == null) {
                        recipes = new JSONArray();
                    }
                    recipes.addAll((JSONArray) jsonObject.get("row"));
                }
                start = end + 1;
                end = Math.min(start + 999, maxEnd);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } while (start <= maxEnd);

        return recipes;
    }
}

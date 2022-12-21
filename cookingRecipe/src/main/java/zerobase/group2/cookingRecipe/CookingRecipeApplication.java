package zerobase.group2.cookingRecipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CookingRecipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CookingRecipeApplication.class, args);
	}

}

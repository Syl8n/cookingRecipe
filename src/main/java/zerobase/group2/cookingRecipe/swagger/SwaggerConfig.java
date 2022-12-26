package zerobase.group2.cookingRecipe.swagger;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("COOKING RECIPE")
                .description("요리 레시피를 등록 및 공유합니다" +
                        "\n이메일 인증 하지 않으면 서비스가 제대로 동작하지 않습니다" +
                        "\n테스트용 계정으론 zb@zerobase.com / 1234 이용하시면 됩니다" +
                        "\nJWT 방식 인증 사용하고 있습니다")
                .version("v1.0")
                .build();
    }
}

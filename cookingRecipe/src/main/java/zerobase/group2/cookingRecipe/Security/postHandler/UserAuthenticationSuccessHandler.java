package zerobase.group2.cookingRecipe.Security.postHandler;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import zerobase.group2.cookingRecipe.Security.authProvider.JwtProvider;
import zerobase.group2.cookingRecipe.cache.CacheKey;
import zerobase.group2.cookingRecipe.member.dto.JwtIssue;

@RequiredArgsConstructor
public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RedisTemplate redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        JwtIssue jwtIssue = jwtProvider.generateTokens(authentication.getName(),
            authentication.getAuthorities().stream().map(Object::toString).collect(
                Collectors.toList()));

        String jsonString = new Gson().toJson(jwtIssue);

        try (Writer writer = response.getWriter()) {
            response.setStatus(HttpStatus.ACCEPTED.value());
            writer.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        redisTemplate.opsForValue()
            .set(CacheKey.REFRESH_TOKEN + "::" + authentication.getName(),
                jwtIssue.getRefreshToken(), CacheKey.DEFAULT_EXPIRE_SEC, TimeUnit.SECONDS);
    }
}

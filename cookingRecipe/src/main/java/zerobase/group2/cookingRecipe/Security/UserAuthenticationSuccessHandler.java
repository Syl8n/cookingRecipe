package zerobase.group2.cookingRecipe.Security;

import java.io.IOException;
import java.io.Writer;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@RequiredArgsConstructor
public class UserAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) {

        String token = jwtProvider.generateToken(authentication.getName(),
            authentication.getAuthorities().stream().map(Object::toString).collect(
                Collectors.toList()));

        try(Writer writer = response.getWriter()) {
            response.setStatus(HttpStatus.ACCEPTED.value());
            writer.write(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

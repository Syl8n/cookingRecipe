package zerobase.group2.cookingRecipe.Security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    public static final String JWT_HEADER_KEY = "Authorization";
    public static final String TYPE_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String token = resolveTokenFromRequest(request);

        if(StringUtils.hasText(token) && jwtProvider.validateToken(token)){
            Authentication auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request){
        String token = request.getHeader(JWT_HEADER_KEY);

        if(!ObjectUtils.isEmpty(token) && token.startsWith(TYPE_PREFIX)) {
            return token.substring(TYPE_PREFIX.length());
        }

        return null;
    }
}

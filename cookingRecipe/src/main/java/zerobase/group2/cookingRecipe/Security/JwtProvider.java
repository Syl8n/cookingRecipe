package zerobase.group2.cookingRecipe.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String KEY_ROLES = "roles";
    private static final long EXPIRE_TIME = 1000 * 60 * 30;
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
        "Zerobase.CookingRecipe.SecretKey.For.JsonWebToken.Authentication".getBytes());

    private final UserDetailsService userDetailsService;

    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        Date now = new Date();

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + EXPIRE_TIME))
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();
    }

    public boolean validateToken(String token) {
        System.out.println("진짜키 : " + SECRET_KEY);
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = getClaims(token);
        if (claims == null) {
            return false;
        }
        return !claims.getExpiration().before(new Date());
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserName(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }

    private String getUserName(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (SignatureException e) {
            throw new BadCredentialsException("잘못된 비밀키", e);
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("만료된 토큰", e);
        } catch (MalformedJwtException e) {
            throw new BadCredentialsException("유효하지 않은 구성의 토큰", e);
        } catch (UnsupportedJwtException e) {
            throw new BadCredentialsException("지원되지 않는 형식이나 구성의 토큰", e);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("잘못된 입력값", e);
        }
        return claims;
    }
}

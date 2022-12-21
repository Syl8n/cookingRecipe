package zerobase.group2.cookingRecipe.Security.authProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import zerobase.group2.cookingRecipe.common.exception.CustomException;
import zerobase.group2.cookingRecipe.common.type.ErrorCode;
import zerobase.group2.cookingRecipe.member.dto.JwtIssue;
import zerobase.group2.cookingRecipe.member.service.MemberService;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String KEY_ROLES = "roles";
    private static final long EXPIRE_TIME = 1000 * 60 * 30;
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
        "Zerobase.CookingRecipe.SecretKey.For.JsonWebToken.Authentication".getBytes());
    private static final String REFRESH_SUBJECT = "REFRESH";

    private final MemberService memberService;

    public JwtIssue generateTokens(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        Date now = new Date();

        String accessToken = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + EXPIRE_TIME))
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();

        claims.setSubject(REFRESH_SUBJECT);

        String refreshToken = Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + EXPIRE_TIME * 2))
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();

        return JwtIssue.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Claims claims = getClaims(token);
        if (claims == null) {
            return false;
        }
        if (claims.getExpiration().before(new Date()) ||
            ObjectUtils.isEmpty(memberService.getRefreshToken(claims.getSubject()))){
            throw new CustomException(ErrorCode.TOKEN_NOT_VALID);
        }
        return true;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberService.loadUserByUsername(getUserName(token));

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
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TOKEN_NOT_VALID);
        }
        return claims;
    }
}

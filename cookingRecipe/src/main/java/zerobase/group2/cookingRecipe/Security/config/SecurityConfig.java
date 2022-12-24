package zerobase.group2.cookingRecipe.Security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import zerobase.group2.cookingRecipe.Security.authProvider.JwtProvider;
import zerobase.group2.cookingRecipe.Security.filter.JsonUsernamePasswordAuthFilter;
import zerobase.group2.cookingRecipe.Security.filter.JwtAuthFilter;
import zerobase.group2.cookingRecipe.Security.postHandler.UserAuthenticationSuccessHandler;
import zerobase.group2.cookingRecipe.member.service.MemberService;
import zerobase.group2.cookingRecipe.member.type.MemberRole;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN_PROCESSES_URL = "/auth/login";

    private final MemberService memberService;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtProvider jwtProvider;

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserAuthenticationSuccessHandler getSuccessHandler() {
        return new UserAuthenticationSuccessHandler(jwtProvider, memberService);
    }

    @Bean
    JsonUsernamePasswordAuthFilter getJsonUsernamePasswordAuthFilter() throws Exception {
        JsonUsernamePasswordAuthFilter jsonUsernamePasswordAuthFilter = new JsonUsernamePasswordAuthFilter(
            LOGIN_PROCESSES_URL);
        jsonUsernamePasswordAuthFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordAuthFilter.setAuthenticationSuccessHandler(getSuccessHandler());
        return jsonUsernamePasswordAuthFilter;
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/auth/**", "/api", "/recipe/read/**", "/recipe/find").permitAll()
            .antMatchers("/admin/**").hasRole(MemberRole.ADMIN)
            .anyRequest().hasRole(MemberRole.USER)
            .and()
            .addFilterAt(getJsonUsernamePasswordAuthFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, JsonUsernamePasswordAuthFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService)
            .passwordEncoder(getPasswordEncoder());
    }
}

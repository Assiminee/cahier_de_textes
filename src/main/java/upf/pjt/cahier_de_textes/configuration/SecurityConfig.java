package upf.pjt.cahier_de_textes.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationSuccessHandler authSuccessHandler;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(AuthenticationSuccessHandler authSuccessHandler, CustomAccessDeniedHandler accessDeniedHandler) {
        this.authSuccessHandler = authSuccessHandler;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/filieres").hasAnyRole("ADMIN", "SS")
                        .requestMatchers(HttpMethod.POST, "/filieres").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/filieres/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users/*").hasAnyRole("SS", "ADMIN", "SP", "PROF")
                        .requestMatchers(HttpMethod.POST, "/users/*/password").hasAnyRole("SS", "ADMIN", "SP", "PROF")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/filieres/*/affectations").hasRole("SS")
                        .requestMatchers(HttpMethod.PUT, "/filieres/*/affectations/**").hasRole("SS")
                        .requestMatchers(HttpMethod.DELETE, "/filieres/*/affectations/**").hasRole("SS")
                        .requestMatchers(HttpMethod.GET, "/filieres/*/affectations").hasRole("SS")
                        .requestMatchers(HttpMethod.GET, "/affectations").hasAnyRole("SS", "SP")
                        .requestMatchers(HttpMethod.GET, "/professeurs/**").hasRole("PROF")
                        .requestMatchers(HttpMethod.GET, "/professeurs/*/affectations").hasRole("PROF")
                        .requestMatchers(HttpMethod.GET, "/profile/**").hasAnyRole("SS", "ADMIN", "SP", "PROF")
                        .requestMatchers(HttpMethod.GET, "/cahiers/**").hasAnyRole("SS", "SP", "PROF")
                        .requestMatchers("/cahiers/**").hasRole("PROF")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .successHandler(authSuccessHandler)
                        .failureUrl("/auth/login?error")
                        .permitAll()
                )
                // .httpBasic()
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler))
        ;
        return http.build();
    }
}

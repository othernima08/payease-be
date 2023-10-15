package alpha.payeasebe.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtFilter jwtFilter;

    // password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // disable csrf
        httpSecurity.csrf(csrf -> {
            csrf.disable();
        });

        // session management
        httpSecurity.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        // authorize request
        httpSecurity.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/users/login").permitAll()
                    .requestMatchers("/users/register").permitAll()
                    .requestMatchers("/users/login").permitAll()
                    .requestMatchers("/users/create-pin").permitAll()
                    .requestMatchers("/users/find-email-reset").permitAll()
                    .requestMatchers("/users/reset-password").permitAll()
                    .requestMatchers("/users/change-password").permitAll()
                    .requestMatchers(HttpMethod.GET,"/users/update-image/**").permitAll()
                    .requestMatchers("/otp/**").permitAll()
                    .requestMatchers("/providers/**").permitAll()
                    .requestMatchers("/transaction-categories/**").permitAll()
                    .anyRequest().fullyAuthenticated();
        });

        // set auth provider
        httpSecurity.authenticationProvider(authenticationProvider());

        // set filter
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // basic auth
        // httpSecurity.httpBasic();

        return httpSecurity.build();
    }

    // digunakan untuk mengautentikasi user yang mau akses req dan atau login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // DaoAuth : provider untuk mencari email dan match-kan passswordnya
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }
}

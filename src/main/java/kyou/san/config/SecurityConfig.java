package kyou.san.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(Customizer.withDefaults()) // Giữ CSRF bật
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll() // Cho phép tất cả truy cập (tạm thời vì chưa có login)
        );

        return http.build();
    }
}

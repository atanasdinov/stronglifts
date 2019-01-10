package com.scalefocus.sl.configuration;


import com.scalefocus.sl.filter.AuthenticationFilter;
import com.scalefocus.sl.filter.CorsFilter;
import com.scalefocus.sl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;


/**
 * <b>Spring Security configuration class.</b>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    /**
     * Spring Security configuration:
     * CSRF disabled, Spring's CORS disabled;
     * Added custom Authentication and CORS filters.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/**").permitAll()
                    .and()
                .csrf()
                    .disable()
                .cors()
                    .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .addFilterAfter(new CorsFilter(), ChannelProcessingFilter.class)
                .addFilterAfter(new AuthenticationFilter(userService), CorsFilter.class);
    }

    /**
     * Password encoder config bean used for authentication password check.
     *
     * @return {@link BCryptPasswordEncoder} object.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


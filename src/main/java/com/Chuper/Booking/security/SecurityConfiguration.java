package com.Chuper.Booking.security;

import com.Chuper.Booking.rest.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    private final UserService userService;
    private final UserPrincipalDetailsService userPrincipalDetailsService;

    public SecurityConfiguration(UserService userService, UserPrincipalDetailsService userPrincipalDetailsService) {
        this.userService = userService;
        this.userPrincipalDetailsService = userPrincipalDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(),this.userService))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(),this.userService))
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/login").permitAll()
                .antMatchers(HttpMethod.POST,"/user/signUp").permitAll()
                .antMatchers(HttpMethod.POST,"/user/signUpCustomer").permitAll()
                .antMatchers(HttpMethod.POST,"/accommodation/save").hasRole(UserRoleList.EMPLOYER)
                .antMatchers(HttpMethod.GET,"/accommodation/getAll").permitAll()
                .antMatchers("/employer/*").hasRole(UserRoleList.EMPLOYER)
                .antMatchers("/accommodation/*").permitAll()
                .antMatchers("/organization/*").hasRole(UserRoleList.EMPLOYER)
                .antMatchers("/search/*").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }


    @Bean
    DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userPrincipalDetailsService);
        return authenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

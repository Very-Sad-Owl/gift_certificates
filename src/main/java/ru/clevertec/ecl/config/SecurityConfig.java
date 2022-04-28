//package ru.clevertec.ecl.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private final AuthenticationService authenticationService;
//
//    @Autowired
//    public SecurityConfig(AuthenticationService authenticationService) {
//        this.authenticationService = authenticationService;
//    }
//
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeRequests()
//                .antMatchers("/").permitAll()
//                .antMatchers(HttpMethod.POST, "/login").permitAll()
//                .antMatchers("/users/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.POST, "/users/**", "/certificates/**", "/tags/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.DELETE, "/users/**", "/certificates/**", "/tags/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.PUT, "/users/**", "/certificates/**", "/tags/**").hasRole("ADMIN")
//                .antMatchers("/buy/**").authenticated()
//        ;
//    }
//
//    @Override
//    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .eraseCredentials(true)
//                .authenticationProvider(authenticationService);
//    }
//}

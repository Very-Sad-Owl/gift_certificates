//package ru.clevertec.ecl.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import ru.clevertec.ecl.dto.UserDto;
//import ru.clevertec.ecl.entity.Role;
//import ru.clevertec.ecl.service.UserService;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//@Component
//public class AuthenticationService implements AuthenticationProvider {
//
//    private UserService userService;
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public AuthenticationService(UserService userService, PasswordEncoder passwordEncoder) {
//        this.userService = userService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String login = authentication.getName();
//        if (!userService.findByName(login).isPresent()) {
//            throw new UsernameNotFoundException("");
//        }
//        UserDto user = userService.findByName(login).get();
//        String password = authentication.getCredentials().toString();
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new BadCredentialsException("");
//        }
//        return new UsernamePasswordAuthenticationToken(user.getName(), null,
//                Arrays.asList(Role.valueOf(user.getRole())));
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return authentication.equals(UsernamePasswordAuthenticationToken.class);
//    }
//}

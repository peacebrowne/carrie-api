package com.example.carrie.services.impl;

import com.example.carrie.dto.UserPrincipal;
import com.example.carrie.errors.custom.InternalServerError;
import com.example.carrie.errors.custom.NotFound;
import com.example.carrie.mappers.AuthorMapper;
import com.example.carrie.models.Author;
import com.example.carrie.models.Login;
import com.example.carrie.services.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements UserDetailsService {

    @Lazy
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AuthorMapper authorMapper;

    @Autowired
    private JWTService jwtService;

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) {
        try{
            Optional<Login> userDetail = Optional.ofNullable(authorMapper.findLoginDetails(username));

            if (userDetail.isEmpty()){
                throw new NotFound("Invalid username or password");
            }

            return new UserPrincipal(userDetail.get());
        }  catch (NotFound e) {
            log.error("ERROR: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            throw new InternalServerError(
                    "An unexpected error occurred while fetching the user.");
        }
    }

    public String verify (Login login) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        login.getUsername(),login.getPassword()
                )
        );

        if (authentication.isAuthenticated()){
            Optional<Author> author = authorMapper.findByEmailOrUsername(login.getUsername());
            if (author.isPresent()){
                return jwtService.generateToken(login.getUsername()) + ":" + author.get().getId();
            }
        }

        return  "Fail";
    }
}

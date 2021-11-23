package be.kuleuven.distributedsystems.cloud.auth;

import be.kuleuven.distributedsystems.cloud.entities.User;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import com.google.api.client.json.Json;
import com.google.gson.JsonObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var session = WebUtils.getCookie(request, "session");
        if (session != null) {
            var token = session.getValue();
            String role = "";
            String email = "";
            try {
                DecodedJWT jwt = JWT.decode(token);
                Map<String, Claim> payloads = jwt.getClaims();
                role = payloads.get("role").asString();
                email = payloads.get("email").asString();
            } catch (Exception e) {
                e.printStackTrace();
            }


            // TODO: (level 1) decode Identity Token and assign correct email and role
            // TODO: (level 2) verify Identity Token
            User user = new User(email, role);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new FirebaseAuthentication(user));
        }
        filterChain.doFilter(request, response);
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        var session = WebUtils.getCookie(request, "session");
//        User user;
//        String role = "";
//        String email = "";
//
//        if (session != null) {
//            String token = session.getValue();
//            try {
//                DecodedJWT jwt = JWT.decode(token);
//
//                if (!jwt.getClaim("role").isNull()) {
//                    role = jwt.getClaim("role").asString();
//                }
//                email = jwt.getClaim("email").asString();
//
//            } catch (JWTDecodeException exception) {
//                exception.printStackTrace();
//            }
//
//            // TODO: (level 1) decode Identity Token and assign correct email and role
//            if ("manager".equals(role)) {
//                user = new User(email, "manager");
//            } else {
//                user = new User(email, "customer");
//            }
//
//            // TODO: (level 2) verify Identity Token
//
//            SecurityContext context = SecurityContextHolder.getContext();
//            context.setAuthentication(new FirebaseAuthentication(user));
//        }
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.equals("/authenticate") || path.endsWith(".html") || path.endsWith(".js") || path.endsWith(".css");
    }

    private static class FirebaseAuthentication implements Authentication {
        private final User user;

        FirebaseAuthentication(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            if (user.isManager()) {
                return List.of(new SimpleGrantedAuthority("manager"));
            } else{
                return new ArrayList<>();
            }
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public User getPrincipal() {
            return this.user;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean b) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return null;
        }
    }
}


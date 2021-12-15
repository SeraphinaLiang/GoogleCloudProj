package be.kuleuven.distributedsystems.cloud.auth;

import be.kuleuven.distributedsystems.cloud.entities.User;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
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
                // verify the JWT
                AuthenticationJWT authentication = new AuthenticationJWT();
                if (authentication.verify(token)) {
                    // decode Identity Token and assign correct email and role
                    DecodedJWT jwt = JWT.decode(token);
                    Map<String, Claim> payloads = jwt.getClaims();
                    //role = payloads.get("role").asString();
                    role =  "manager";
                    email = payloads.get("email").asString();
                } else {
                    throw new Exception("Verify JWT fail");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            User user = new User(email, role);

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(new FirebaseAuthentication(user));
        }
        filterChain.doFilter(request, response);
    }


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
            } else {
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


package oneseoktwojo.ohtalkhae.domain.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.User;
import oneseoktwojo.ohtalkhae.domain.auth.dto.CustomUserDetails;
import oneseoktwojo.ohtalkhae.domain.auth.enums.Role;
import oneseoktwojo.ohtalkhae.domain.auth.jwt.JWTUtil;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authorizationHeader.substring(7);

        if (jwtUtil.isExpired(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(new ObjectMapper().writeValueAsString(
                    ApiResponse.error(401, "Token has expired")
            ));
            return;
        }

        try {
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            User user = new User();
            user.setUsername(username);
            user.setPassword("n/a");
            user.setRole(Role.valueOf(role));

            CustomUserDetails userDetails = new CustomUserDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(
                    ApiResponse.error(401, "Invalid or tampered token")
            ));
            return;
        }

        filterChain.doFilter(request, response);
    }
}

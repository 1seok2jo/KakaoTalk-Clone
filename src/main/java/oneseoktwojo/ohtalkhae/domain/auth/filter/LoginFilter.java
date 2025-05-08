package oneseoktwojo.ohtalkhae.domain.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.dto.CustomUserDetails;
import oneseoktwojo.ohtalkhae.domain.auth.dto.TokenResponse;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserLoginRequest;
import oneseoktwojo.ohtalkhae.domain.auth.entity.RefreshToken;
import oneseoktwojo.ohtalkhae.domain.auth.jwt.JWTUtil;
import oneseoktwojo.ohtalkhae.domain.auth.service.RefreshTokenService;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    private Long accessTokenExpirationTime;

    public LoginFilter(@Value("${spring.jwt.access-token-expiration-time}") Long accessTokenExpirationTime, AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        // Set the URL to which the filter will respond
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserLoginRequest loginRequest;
        try {
            loginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationException("Authentication failed") {
                @Override
                public String getMessage() {
                    return "Invalid username or password";
                }
            };
        }


        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = userDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        String role = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
        String token = jwtUtil.createJwt(username, role, accessTokenExpirationTime); // 1 hour expiration time
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(username);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                ApiResponse.success(200, new TokenResponse(token, refreshToken.getToken()))
        ));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws AuthenticationException, IOException {
        // Handle unsuccessful authentication
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(new ObjectMapper().writeValueAsString(
                ApiResponse.error(401, "Invalid username or password")
        ));
    }
}

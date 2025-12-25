package blog.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import blog.entity.User;
import blog.exceptions.UserNotFoundException;
import blog.services.UserDetailsServiceImpl;
import blog.util.BanMessageFormatter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.contains("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            String username = jwtUtils.getUsernameFromJwt(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Check if user is banned
                if (userDetails instanceof User user) {
                    if (user.isActiveBan()) {
                        String banMessage = BanMessageFormatter.formatBanMessage(user.getBannedUntil(), user.getBanReason());
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\":\"" + banMessage + "\",\"status\":403,\"error\":\"Your account has been banned\"}");
                        return;
                    }
                }

                if (userDetails != null && jwtUtils.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(userDetails);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // JWT invalid
                    request.setAttribute("jwt_error", "Token has expired. Please login again.");
                    return;
                }
            }
        } catch (DisabledException ex) {
            // User is banned
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"" + ex.getMessage() + "\",\"status\":403,\"error\":\"Your account has been banned\"}");
            return;
        } catch (UserNotFoundException ex) {
            // User not found in DB
            request.setAttribute("jwt_error", "User not found");
            return;
        } catch (Exception ex) {
            // Any other error (JWT parsing, DB error, etc.)
            request.setAttribute("jwt_error", "Authentication failed");
            return;
        }
        filterChain.doFilter(request, response);

    }

}

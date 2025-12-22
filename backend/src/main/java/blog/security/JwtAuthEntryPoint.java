package blog.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import blog.dto.response.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage = "Authentication required to access this resource";

        Object jwtError = request.getAttribute("jwt_error");
        if (jwtError != null) {
            errorMessage = jwtError.toString();
        }

        ErrorResponseDto err = new ErrorResponseDto("Unauthorized", 401, errorMessage);

        String jsonResponse = objectMapper.writeValueAsString(err);
        response.getWriter().write(jsonResponse);
    }

}

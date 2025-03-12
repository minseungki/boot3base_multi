package com.example.demo.security;

import com.example.demo.dto.common.JwtAuthenticationVo;
import com.example.demo.dto.common.enumeration.ErrorCode;
import com.example.demo.dto.common.enumeration.JwtType;
import com.example.demo.util.Aes256Util;
import com.example.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtService {

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${jwt.platform-type}")
    private String PLATFORM_TYPE;

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final Aes256Util aes256Util;

    public boolean isValidToken(HttpServletRequest req, String token) {
        try {
            JwtAuthenticationVo jwtAuthenticationVo = jwtUtil.getTokenVo(token);
            String platformType = aes256Util.decrypt(jwtUtil.extractClaim(token, Claims::getSubject));

            if (!PLATFORM_TYPE.equals(platformType)
                    || !JwtType.access.equals(jwtAuthenticationVo.getJwtType())
                    || !profile.equals(jwtAuthenticationVo.getProfile().getDesc())
            ) {
                throw new UnsupportedJwtException("Unsupported Type or Profile");
            }
        } catch (ExpiredJwtException ex) {
            // 토큰 만료
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_001);
            req.setAttribute("Exception", ex);
            return false;
        } catch (JwtException | IllegalArgumentException  ex) {
            // 토큰 위변조
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_002);
            req.setAttribute("Exception", ex);
            return false;
        } catch (Exception ex) {
            // 토큰 에러
            req.setAttribute("ERROR_CODE", ErrorCode.ERR401_999);
            req.setAttribute("Exception", ex);
            return false;
        }
        return true;
    }

    public Authentication getAuthentication(String token) {
        JwtAuthenticationVo jwtAuthenticationVo = jwtUtil.getTokenVo(token);
        String userId = jwtAuthenticationVo.getId();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

}

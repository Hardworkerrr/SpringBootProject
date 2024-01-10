package com.example.app.filter;

import com.example.app.utill.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${jwt.header}")
    private String header;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(header);
        String jwtToken = null;
        String username = null;
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            jwtToken = authHeader.replace("Bearer ","");
            try{
                username = jwtTokenUtil.getUsername(jwtToken);
                if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            jwtTokenUtil.getRoles(jwtToken).stream().map(SimpleGrantedAuthority::new).toList()
                    );
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
                filterChain.doFilter(request,response);
            } catch (ExpiredJwtException | SignatureException e) {
                handlerExceptionResolver.resolveException(request,response,null,e);
            }
        }
        else
            filterChain.doFilter(request,response);
    }
}

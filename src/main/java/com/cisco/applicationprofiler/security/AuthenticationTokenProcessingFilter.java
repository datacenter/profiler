package com.cisco.applicationprofiler.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.cisco.applicationprofiler.models.UserResponse;
import com.cisco.applicationprofiler.security.util.JWTUtil;
import com.google.gson.Gson;

@Component
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

	@Value("${profiler.security.jwt.token}")
	private String TOKEN ;

	@Inject
	private JWTUtil jwtUtil;

	@Inject
	private Gson gson;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String tokenValue = httpRequest.getHeader(TOKEN);

		if (tokenValue != null && !tokenValue.isEmpty()) {
			logger.info("valid token found");
			UserResponse userResponse = gson.fromJson(jwtUtil.parseJWT(tokenValue), UserResponse.class);

			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority(userResponse.getRole()));

			Authentication authentication = new UsernamePasswordAuthenticationToken(userResponse, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} else {
			logger.info("invalid token");
		}
		// continue thru the filter chain
		chain.doFilter(request, response);
	}
}
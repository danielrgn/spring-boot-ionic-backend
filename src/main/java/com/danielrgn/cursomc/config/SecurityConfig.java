package com.danielrgn.cursomc.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.danielrgn.cursomc.security.JWTAuthenticationFilter;
import com.danielrgn.cursomc.security.JWTAuthorizationFilter;
import com.danielrgn.cursomc.security.JWTUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	private static final String[] PUBLIC_MACHERS_GET = {
		"/produtos/**",
		"/categorias/**"
	};
	
	private static final String[] PUBLIC_MACHERS = {
		"/h2-console/**",
	};
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		http.cors().and().csrf().disable();
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET, PUBLIC_MACHERS_GET).permitAll()
		.antMatchers(PUBLIC_MACHERS).permitAll()
		.anyRequest().authenticated();
		http.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtUtil));
		http.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtUtil, userDetailsService));
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	  final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	  source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
	  return source;
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

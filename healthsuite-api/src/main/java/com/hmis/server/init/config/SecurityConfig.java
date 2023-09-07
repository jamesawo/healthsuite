package com.hmis.server.init.config;

import com.hmis.server.hmis.common.constant.HmisConfigConstants;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.user.service.UserDetailServiceImpl;
import com.hmis.server.init.security.JwtAuthenticationEntryPoint;
import com.hmis.server.init.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static String[] UI = {
			"/**.ttf", "/**.ico",
			"/**.woff{**}", "/**.css",
			"/**.js", "/**.png",
			"/**.jpg",
			"/**.jpeg",
			"/**.svg",
			"/assets/**",
	};

	private static String[] H2_CONSOLE = {
			"/h2-console",
			"/h2-console/**"
	};

	private static String[] STATIC = {
			"/static/**",
			"/images/**"
	};

	private static String[] SOCKET = {
			HmisConfigConstants.SOCKET_ENDPOINT,
			HmisConfigConstants.SOCKET_ENDPOINT + "/**",
	};

	private static String[] WHITELIST = {
			HmisConstant.API_PREFIX + "mobile/**",
			HmisConstant.API_PREFIX + "auth/**",
			HmisConstant.API_PREFIX + "mobile/**",
	};

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(getUserDetails());
	}

	@Bean
	public UserDetailsService getUserDetails() {
		return new UserDetailServiceImpl(); // Implementation class
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.headers().frameOptions().sameOrigin();
		http.authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers(STATIC).permitAll()
				.antMatchers(UI).permitAll()
				.antMatchers(H2_CONSOLE).permitAll()
				.antMatchers(SOCKET).permitAll()
				.antMatchers(WHITELIST).permitAll()
				.anyRequest().authenticated()
				// .and().headers().frameOptions().sameOrigin()
				.and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		http.exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint());
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(getUserDetails()).passwordEncoder(passwordEncoder());
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// @Override
	// protected MethodSecurityExpressionHandler expressionHandler() {
	// DefaultMethodSecurityExpressionHandler expressionHandler =
	// new DefaultMethodSecurityExpressionHandler();
	// expressionHandler.setPermissionEvaluator(new
	// CustomInterfaceImplementation());
	// return expressionHandler;
	// }

}

package com.kiran.securesockets.security;

import com.kiran.securesockets.common.authentication.CustomUserDetailsService;
import com.kiran.securesockets.security.authhandler.CustomAuthenticationFailureHandler;
import com.kiran.securesockets.security.authhandler.CustomAuthenticationProvider;
import com.kiran.securesockets.security.authhandler.CustomAuthenticationSuccessHandler;
import com.kiran.securesockets.security.authhandler.CustomLogoutSuccessHandler;
import com.kiran.securesockets.security.session.CustomSessionInformationExpiredStrategy;
import com.kiran.securesockets.security.xss.XSSFilter;
import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Configuration
@EnableWebSecurity(debug = false)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	private CustomAuthenticationProvider customAuthenticationProvider;
	private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
	private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	@Autowired
	@Qualifier("customAuthenticationProvider")
	public void setCustomAuthenticationProvider(CustomAuthenticationProvider customAuthenticationProvider) {
		this.customAuthenticationProvider = customAuthenticationProvider;
	}

	@Autowired
	@Qualifier("customAuthenticationSuccessHandler")
	public void setAuthenticationSuccessHandler(CustomAuthenticationSuccessHandler authenticationSuccessHandler) {
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}

	@Autowired
	@Qualifier("customAuthenticationFailureHandler")
	public void setCustomAuthenticationFailureHandler(CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
	}

	public SpringSecurityConfig() {
		super(false);
	}

	 @Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public CustomUserDetailsService customUserDetailsService() {
		return new CustomUserDetailsService();
	}


	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
	public CustomAuthenticationProvider customAuthenticationProvider(CustomUserDetailsService customUserDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		CustomAuthenticationProvider provider = new CustomAuthenticationProvider();
		provider.setUserDetailsService(customUserDetailsService);
		provider.setPasswordEncoder(bCryptPasswordEncoder);
		return provider;
	}

	@Bean
	public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler();
	}

	@Bean
	public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
		return new CustomAuthenticationFailureHandler();
	}

	@Bean
	public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
		return new CustomLogoutSuccessHandler();
	}

	@Bean
	public DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
		//entryPoints.put(new AntPathRequestMatcher("/**"), new BasicAuthenticationEntryPoint());
		entryPoints.put(new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"), new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
		DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
		entryPoint.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
		return entryPoint;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(customAuthenticationProvider);
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public CompositeSessionAuthenticationStrategy concurrentSession() {

		ConcurrentSessionControlAuthenticationStrategy concurrentAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
//		concurrentAuthenticationStrategy.setMaximumSessions(1);
//		concurrentAuthenticationStrategy.setExceptionIfMaximumExceeded(true);
		List<SessionAuthenticationStrategy> delegateStrategies = new ArrayList<SessionAuthenticationStrategy>();
		delegateStrategies.add(concurrentAuthenticationStrategy);
		delegateStrategies.add(new SessionFixationProtectionStrategy());
		delegateStrategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry()));

		return new CompositeSessionAuthenticationStrategy(delegateStrategies);
	}

//	@Bean
//	ConcurrentSessionFilter concurrentSessionFilter() {
//		CustomSessionInformationExpiredStrategy redirectStrategy = new CustomSessionInformationExpiredStrategy("/login");
//		CustomConcurrentSessionFilter concurrentSessionFilter = new CustomConcurrentSessionFilter(sessionRegistry(), redirectStrategy);
//		return concurrentSessionFilter;
//	}

	@Bean
	SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
		return new CustomSessionInformationExpiredStrategy("/login");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.sessionManagement()
				.sessionAuthenticationStrategy(concurrentSession())
				.maximumSessions(-1)
				.expiredSessionStrategy(sessionInformationExpiredStrategy())
			    .expiredUrl("/login*")
				.maxSessionsPreventsLogin(true);
		http
				.csrf()
				.ignoringAntMatchers("/forgot-password", "/reset-password", "/verify/*", "/resources/**")
				//.requireCsrfProtectionMatcher(excludeFromCSRF)
				//.disable()
				.csrfTokenRepository(new HttpSessionCsrfTokenRepository())
				.and()
				.authorizeRequests()
				// .antMatchers("/user/**").anonymous()
				.antMatchers("/login*", "/signin*", "/forgot-password", "/reset-password", "/verify/*", "/resources/**").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/process-login")
				.usernameParameter("username").passwordParameter("password")
				.successHandler(authenticationSuccessHandler)
				.failureHandler(customAuthenticationFailureHandler)
				.and()
				.logout()
				//.logoutUrl("/logout")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessHandler(customLogoutSuccessHandler())
				.logoutSuccessUrl("/login")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID", "XSRF-TOKEN")
				.and()
				.requestCache()
				.and()
				.sessionManagement()
				.enableSessionUrlRewriting(false)
				.sessionFixation()
				.newSession()
				.and()
				.headers()
				.contentTypeOptions()
				.and()
				.xssProtection()
				.and()
				.cacheControl()
				.and()
				.httpStrictTransportSecurity()
				.and()
				.frameOptions()
				.and()
				.referrerPolicy()
				.and()
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(delegatingAuthenticationEntryPoint());
		http.addFilterBefore(new XSSFilter(), WebAsyncManagerIntegrationFilter.class);
		http.addFilterBefore(new RemoteIpFilter(), XSSFilter.class);
//		http.sessionManagement().sessionAuthenticationStrategy(concurrentSession());
//		http.addFilterBefore(concurrentSessionFilter(), ConcurrentSessionFilter.class);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/assets/**", "/customjs/**");
	}
}

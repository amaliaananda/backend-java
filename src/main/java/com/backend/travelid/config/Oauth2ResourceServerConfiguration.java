package com.backend.travelid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(securedEnabled = true) //secure definition
public class Oauth2ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * Manage resource server.
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
    }
//    private static final String SECURED_PATTERN = "/api/**";
    /**
     * Manage endpoints.
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/","/showFile/**","/v1/showFile/**","/v1/upload","/v1/user-register/**","/v1/merchant-register/**","/swagger-ui/**","/swagger-ui.html","/v3/api-docs/**","/v1/user-login/**",
                        "/v1/forget-password/**","/oauth/authorize**","/login**","/error**","/flight/**","/booking/**","/bookingDetail/**","/seat/**","/customers/**", "/user-login/**")
                .permitAll()
                .antMatchers("/v1/role-test-global/list-barang").hasAnyAuthority("ROLE_READ")
                .antMatchers("/v1/role-test-global/post-barang").hasAnyAuthority("ROLE_WRITE")
//                .antMatchers("/productsAuth/**").hasAnyAuthority("ROLE_MERCHANT")
//                .antMatchers("/merchantsAuth/**").hasAnyAuthority("ROLE_MERCHANT")
//                .antMatchers("/orders/generateReport").hasAnyAuthority("ROLE_MERCHANT")
//                .antMatchers("/customersAuth/**").hasAnyAuthority("ROLE_USER")
//                .antMatchers("/orders/generateInvoice").hasAnyAuthority("ROLE_USER")
                .antMatchers("/v1/role-test-global/post-barang-admin").hasAnyAuthority("ROLE_ADMIN")
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .permitAll()
        ;
    }
}

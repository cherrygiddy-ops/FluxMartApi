package com.fluxmartApi.admin;

import com.fluxmartApi.common.SecurityRules;
import com.fluxmartApi.users.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class AdminSecurityRules implements SecurityRules {
    @Override
    public void config(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/admin/users").hasRole(Role.ADMIN.name());
        registry.requestMatchers(HttpMethod.POST,"/products/**").hasRole(Role.ADMIN.name());
        registry.requestMatchers(HttpMethod.PUT,"/products/**").hasRole(Role.ADMIN.name());
        registry.requestMatchers(HttpMethod.DELETE,"/products/**").hasRole(Role.ADMIN.name());
    }
}

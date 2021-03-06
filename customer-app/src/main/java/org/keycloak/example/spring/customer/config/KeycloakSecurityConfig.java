package org.keycloak.example.spring.customer.config;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.io.IOException;
import java.io.InputStream;

/**
 * Application security configuration.
 *
 * @author Scott Rossillo
 */
@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)  //necessary for KeycloakClientRequestFactory to be found
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter //this class causes failing due to keycloag.json missing..
{

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KeycloakRestTemplate keycloakRestTemplate(KeycloakClientRequestFactory keycloakClientRequestFactory) {
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(
            KeycloakAuthenticationProcessingFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    @ConditionalOnClass(SpringBootApplication.class)
    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(
            KeycloakPreAuthActionsFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        super.configure(http);
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/customers*").hasRole("USER")
                .antMatchers("/prepost/**").permitAll()
                .antMatchers("/admin*").hasRole("ADMIN")
                .anyRequest().permitAll();
    }

//    @Value("${keycloak.configurationFile:WEB-INF/keycloak.json}")
//    private Resource mainRealmConfigFileResource;
//    public   KeycloakDeployment keycloakDeployment;
//
//    //unneccessary
//    private KeycloakDeployment gainAccessToKeycloakDeployment(String realmName, Resource realmConfigFileResource) {
//        InputStream is = null;
//        try {
//            is = realmConfigFileResource.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(is);
//        this.cache.put(realmName, deployment);
//        return deployment;
//    }

}

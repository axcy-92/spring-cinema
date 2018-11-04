package com.axcy.springcinema.config.web;

import com.axcy.springcinema.config.core.CoreConfiguration;
import com.axcy.springcinema.service.Roles;
import com.axcy.springcinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @author Aleksei_Cherniavskii
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import(CoreConfiguration.class)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserService userService;

    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication();
        auth.userDetailsService(userService)
            .passwordEncoder(bCryptPasswordEncoder());
    }

    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        authenticationMgr.inMemoryAuthentication()
                .withUser("admin")
                .password("admin")
                .roles("BOOKING_MANAGER");
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/logout", "/login", "/resources/**").permitAll()
                .antMatchers("/import")
                    .access("hasRole('" + Roles.BOOKING_MANAGER + "') and hasRole('" + Roles.REGISTERED_USER + "')")
                .antMatchers("/booking/result/event/**")
                    .access("hasRole('" + Roles.BOOKING_MANAGER + "')")
                .antMatchers("/booking")
                    .access("hasAnyRole('" + Roles.REGISTERED_USER + "', '" + Roles.BOOKING_MANAGER + "')")
                .antMatchers("/event/add")
                    .access("hasRole('" + Roles.BOOKING_MANAGER + "')")
                .antMatchers("/user/**")
                    .access("hasAnyRole('" + Roles.REGISTERED_USER + "', '" + Roles.BOOKING_MANAGER + "')")
                .and()
                .exceptionHandling().accessDeniedPage("/accessDenied")
                .and()
                .formLogin().loginPage("/login")
                            .defaultSuccessUrl("/")
                            .failureUrl("/login?error")
                            .usernameParameter("name").passwordParameter("password")
                .and()
                .rememberMe().rememberMeParameter("remember-me")
                             .tokenRepository(persistentTokenRepository())
                             .tokenValiditySeconds(86400)
                .and()
                .csrf()
                .and()
                .logout().logoutSuccessUrl("/login?logout")
                         .deleteCookies("remember-me");

    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        tokenRepositoryImpl.setDataSource(dataSource);
        return tokenRepositoryImpl;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
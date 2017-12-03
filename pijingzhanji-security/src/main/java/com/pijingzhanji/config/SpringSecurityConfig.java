package com.pijingzhanji.config;

import com.pijingzhanji.security.JwtAuthenticationEntryPoint;
import com.pijingzhanji.security.JwtAuthenticationTokenFilter;
import com.pijingzhanji.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
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

/**
 * @author : 披荆斩棘
 * @date : 2017/6/8
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true )
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public void configureAuthentication ( AuthenticationManagerBuilder authenticationManagerBuilder ) throws Exception {
        authenticationManagerBuilder
                .userDetailsService( this.userDetailsService )
                .passwordEncoder( passwordEncoder() );
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean () throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint () {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil () {
        return new JwtTokenUtil();
    }

    @Override
    protected void configure ( HttpSecurity httpSecurity ) throws Exception {
        httpSecurity
                // jwt不需要csrf
                .csrf().disable()
                // 开启 cors 的支持
                .cors().and()
                // jwt不需要session , 所以不创建会话
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS ).and()
                // 异常处理
                .exceptionHandling().authenticationEntryPoint( jwtAuthenticationEntryPoint() ).and()
                .authorizeRequests()
                // 允许匿名资源请求
                .antMatchers(
                        HttpMethod.GET ,
                        "/" ,
                        "/*.html" ,
                        "/favicon.ico" ,
                        "/**/*.html" ,
                        "/**/*.css" ,
                        "/**/*.js"
                ).permitAll()
                .antMatchers( "/**" ).permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();
        // 基于定制JWT安全过滤器
        httpSecurity.addFilterBefore( authenticationTokenFilterBean() , UsernamePasswordAuthenticationFilter.class );
        // 禁用页面缓存
        httpSecurity.headers().cacheControl();
    }

}

package fun.codefarmer.jwt.config;

import fun.codefarmer.jwt.filter.JwtFilter;
import fun.codefarmer.jwt.filter.JwtLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @ @ClassName SecutityConfig
 * @ Descriotion TODO
 * @ Author admin
 * @ Date 2020/2/16 19:54
 **/
@Configuration
public class SecutityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //设置用户
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("codefarmer")
                .password("$2a$10$IrUlsbJbc/qmp/EfrBN92uDUsCI0z9VmSUHAbS.otupX6LFXU1d8W")
                .roles("user")
                .and()
                .withUser("admin")
                .password("$2a$10$Z0R37dHgMdGgBdExvQ.sTulAE7Uxrxd4KA3lMswE9sOy8NSrSn3T2")
                .roles("admin");
    }

    /**
     * 路径过滤
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/hello")
                .hasRole("user")
                .antMatchers("/admin")
                .hasRole("admin")
                .antMatchers(HttpMethod.POST,"/login")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                // 加过滤器，先加登录过滤器
                .addFilterBefore(new JwtLoginFilter("/login",authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                //加校验过滤器
                .addFilterBefore(new JwtFilter(),UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }
}

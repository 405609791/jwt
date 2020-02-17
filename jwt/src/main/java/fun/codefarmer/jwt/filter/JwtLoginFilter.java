package fun.codefarmer.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fun.codefarmer.jwt.mode.User;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt 登录设置
 * @ @ClassName JwtFilter
 * @ Descriotion TODO
 * @ Author admin
 * @ Date 2020/2/16 20:45
 **/
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
    //除了实现下面的attemptAuthentication方法以外，还需要实现构造方法

    /**
     *
     * @param defaultFilterProcessesUrl 默认要处理的地址
     * @param authenticationManager 校验的时候用到
     */
    public JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        //给AbstractAuthenticationProcessingFilter 他中的属性赋值，校验的时候通过get方法获得authenticationManager
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //这是以json格式
        User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
    }

    // ctr + o 快捷键 ：方法 1，2
    //1.成功回调：登录成功

    /**
     * 登录成功发一个token
     * @param request
     * @param response
     * @param chain
     * @param authResult 登录成功存储用户消息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //获取登录用户的角色
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        StringBuffer sb = new StringBuffer();
        for (GrantedAuthority authority : authorities) {
            sb.append(authority.getAuthority()).append(",");
        }
        //生成jwt
        String jwt = Jwts.builder()
                //构建用户角色
                .claim("authorities", sb)
                //设置主题
                .setSubject(authResult.getName())
                //token 过期时间
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                //签名哈希512算法                后面是key
                /**
                 * ** 注意错误事项，SignatureAlgorithm.HS512 算法如果写错会导致后面JwtFilter类中
                 * Jws<Claims> jst = Jwts.parser().setSigningKey("codefarmer@123")*** jst= null错误
                 *
                 */
                .signWith(SignatureAlgorithm.HS512, "codefarmer@123")
                .compact();
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        map.put("msg", "登录成功");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(map));
        out.flush();
        out.close();
    }

    //2.失败回调
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Map<String, String> map = new HashMap<>();
        map.put("msg", "登录失败");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(map));
        out.flush();
        out.close();
    }
}

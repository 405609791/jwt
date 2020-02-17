package fun.codefarmer.jwt.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 其他接口请求时，需要验证
 * 作用：
 * 校验每次携带的token是否正确，争取，请求继续执行，不正确请求到此为止。
 * @ @ClassName JwtFilter
 * @ Descriotion TODO
 * @ Author admin
 * @ Date 2020/2/17 12:08
 **/
public class JwtFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //强转成HttpServletRequest
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        //也可以放在参数中，解析json
        //在请求头中，从请求头中获得token,此方法放在请求头中
        String jwtToken = req.getHeader("authorization");
        Jws<Claims> jst = Jwts.parser().setSigningKey("codefarmer@123")
                //token 中会自动加入 Bearer 所以需要用replace方法替换掉
                .parseClaimsJws(jwtToken.replace("Bearer", ""));
        Claims claims = jst.getBody();
        String username = claims.getSubject();
        //获得用户角色
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("authorities"));
        // 将值传入进行校验
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
        //完成后，再设置到token中
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(servletRequest,servletResponse);
    }
}

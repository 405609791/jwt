# jwt
无状态登录：jwt或Oauth2两种解决方案
JSON Web Token（JWT）是一个非常轻巧的规范。这个规范允许我们使用JWT在用户和服务器之间传递安全可靠的信息。
一个JWT实际上就是一个字符串，它由三部分组成，头部、载荷与签名。
忆程千里 公众号中有详细介绍
* jwt 部分语言，任何语言都可以使用，Java中引入jjwt依赖即可 

设置jwt 过滤器 登录成功后生成token
        访问其他接口的时候，校验token 是否正确

**开发遇到的错误
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
                 * 下面 HS512 写成了 ES512 导致 jst= null
                 */
                .signWith(SignatureAlgorithm.HS512, "codefarmer@123")
                .compact();
# 1. JWT的原则

JWT的原则是在服务器身份验证之后，将生成一个JSON对象并将其发送回用户，如下所示。

```json
{
    "UserName": "Chongchong",
    "Role": "Admin",
    "Expire": "2018-08-08 20:15:56"
}
```

之后，当用户与服务器通信时，客户在请求中携带JSON对象。服务器仅依赖于这个JSON对象来标识用户。

为了防止用户篡改数据，服务器将在生成对象时添加签名。

服务器不保存任何会话数据，即服务器变为无状态，使其更容易扩展。



# 2. JWT的数据结构

该对象是一个很长的字符串，字符之间通过"."分隔符分为三个子串。

```
JWT头 . 有效载荷 . 签名
```

注意JWT对象为一个长字串，各字串之间也没有换行符。

每一个子串表示了一个功能块，总共有以下三个部分：

- JWT头

- 有效载荷

- 签名



## 2.1 JWT头(算法和令牌类型)

JWT头部分是一个描述JWT元数据的JSON对象，通常如下所示。

```json
{
    "alg": "HS256",
    "typ": "JWT"
}
```

在上面的代码中，alg属性表示签名使用的算法，默认为HMAC SHA256（写为HS256）；

typ属性表示令牌的类型，JWT令牌统一写为JWT。

最后，使用Base64 URL算法将上述JSON对象转换为字符串保存。



## 2.2 有效载荷(实际存储内容)

有效载荷部分，是JWT的主体内容部分，也是一个JSON对象，包含需要传递的数据。 JWT指定七个默认字段供选择。

- iss：发行人

- exp：到期时间

- sub：主题

- aud：用户

- nbf：在此之前不可用

- iat：发布时间

- jti：JWT ID用于标识该JWT

除以上默认字段外，我们还可以自定义私有字段，如下例：

```json
{
"sub": "1234567890",
"name": "chongchong",
"admin": true
}
```

**请注意，默认情况下JWT是未加密的，任何人都可以解读其内容，因此不要构建隐私信息字段，存放保密信息，以防止信息泄露。**

JSON对象也使用Base64 URL算法转换为字符串保存。



## 2.3 签名哈希(HMAC SHA256)

签名哈希部分是对上面两部分数据签名，通过指定的算法生成哈希，以确保数据不会被篡改。

首先，需要指定一个密码（secret），该密码仅仅为保存在服务器中，并且不能向用户公开。

然后，使用标头中指定的签名算法（默认情况下为HMAC SHA256）根据以下公式生成签名。

```java
HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
    
String message = "需要使用hmac-sha256签名的字符串";
Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(),"HmacSHA256");
sha256_HMAC.init(secret_key);
byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
String hash = byteArrayToHexString(bytes); //Integer.toHexString(b[n] & OXFF),一位0+string,其它返回string
```

在计算出签名哈希后，JWT头，有效载荷和签名哈希的三个部分组合成一个字符串，每个部分用"."分隔，就构成整个JWT对象。



## 2.4 Base64URL算法

如前所述，JWT头和有效载荷序列化的算法都用到了Base64URL。该算法和常见Base64算法类似，稍有差别。

作为令牌的JWT可以放在URL中（例如api.example/?token=xxx）。

 Base64中用的三个字符是"+"，"/"和"="，由于在URL中有特殊含义，因此Base64URL中对他们做了替换：

"="去掉，"+"用"-"替换，"/"用"_"替换，这就是Base64URL算法。



# 3. JWT的用法

客户端接收服务器返回的JWT，将其存储在Cookie或localStorage中。

此后，客户端将在与服务器交互中都会带JWT。如果将它存储在Cookie中，就可以自动发送，但是不会跨域，

因此一般是将它放入HTTP请求的Header Authorization字段中。

Authorization: Bearer access_token

当跨域时，也可以将JWT被放置于POST请求的数据主体中。



# 4. jwt问题和趋势

1、JWT默认不加密，但可以加密。生成原始令牌后，可以使用改令牌再次对其进行加密。

2、当JWT未加密方法是，一些私密数据无法通过JWT传输。

3、JWT不仅可用于认证，还可用于信息交换。善用JWT有助于减少服务器请求数据库的次数。

4、JWT的最大缺点是服务器不保存会话状态，所以在使用期间不可能取消令牌或更改令牌的权限。也就是说，一旦JWT签发，在有效期内将会一直有效。

5、JWT本身包含认证信息，因此一旦信息泄露，任何人都可以获得令牌的所有权限。为了减少盗用，JWT的有效期不宜设置太长。对于某些重要操作，用户在使用时应该每次都进行进行身份验证。

6、为了减少盗用和窃取，JWT不建议使用HTTP协议来传输代码，而是使用加密的HTTPS协议进行传输。



# 5. java-jwt集成

pom引入：

```xml
<dependency>
      <groupId>com.auth0</groupId>
      <artifactId>java-jwt</artifactId>
      <version>3.4.0</version>
</dependency>
```

java使用：

```java
    public String getToken(User user) {
        String token="";
        //把用户的id存入到token中，使用用户的密码作为盐 生成签名
        token= JWT.create().withAudience(user.getId()).sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
	/**---------------------------------------------------------------------------------------**/
	// 获取 token 中的 user id
    String userId;
    try {
        userId = JWT.decode(token).getAudience().get(0);
    } catch (JWTDecodeException j) {
        throw new RuntimeException("401");
    }
	// 这无用
    User user = userService.findUserById(userId);
    // 验证 token
    JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
    try {
        jwtVerifier.verify(token);
    } catch (JWTVerificationException e) {
        throw new RuntimeException("401");
    }
```

**需要自定义两个注解:**

```java
//用来跳过验证的PassToken
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PassToken {
    boolean required() default true;
}
```

```java
//需要登录才能进行操作的注解UserLoginToken
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserLoginToken {
    boolean required() default true;
}
```

**定义一个实体类`User`**

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    String id;
    String username;
    String password;
}
```

**`token`的生成方法**

```java
	public String getToken(User user) {
        String token="";
        token= JWT.create().withAudience(user.getId()).sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
```

**`Algorithm.HMAC256()`:使用`HS256`生成`token`,密钥则是用户的密码，唯一密钥的话可以保存在服务端。
 **

**`withAudience()`存入需要保存在`token`的信息，这里我把用户`ID`存入`token`中**

**接下来需要写一个拦截器去获取`token`并验证`token`**

```java
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        String token = httpServletRequest.getHeader("token");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if(!(object instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)object;
        Method method=handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                // 获取 token 中的 user id
                String userId;
                try {
                    userId = JWT.decode(token).getAudience().get(0);
                } catch (JWTDecodeException j) {
                    throw new RuntimeException("401");
                }
                User user = userService.findUserById(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在，请重新登录");
                }
                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                try {
                    jwtVerifier.verify(token);
                } catch (JWTVerificationException e) {
                    throw new RuntimeException("401");
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, 
                                  HttpServletResponse httpServletResponse, 
                            Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, 
                                          HttpServletResponse httpServletResponse, 
                                          Object o, Exception e) throws Exception {
    }
```

**配置拦截器**

**在配置类上添加了注解`@Configuration`，标明了该类是一个配置类并且会将该类作为一个`SpringBean`添加到`IOC`容器内**

```java
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**");    // 拦截所有请求，通过判断是否有 @LoginRequired 注解 决定是否需要登录
    }
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}
```

`WebMvcConfigurerAdapter`该抽象类其实里面没有任何的方法实现，只是空实现了接口
 `WebMvcConfigurer`内的全部方法，并没有给出任何的业务逻辑处理，这一点设计恰到好处的让我们不必去实现那些我们不用的方法，都交由`WebMvcConfigurerAdapter`抽象类空实现,如果我们需要针对具体的某一个方法做出逻辑处理,仅仅需要在
 `WebMvcConfigurerAdapter`子类中`@Override`对应方法就可以了。

**注：
 在`SpringBoot2.0`及`Spring 5.0`中`WebMvcConfigurerAdapter`已被废弃
 网上有说改为继承`WebMvcConfigurationSupport`，不过试了下，还是过期的**

**解决方法：直接实现`WebMvcConfigurer` （官方推荐）**

```java
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**");   
    }
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}
```

`InterceptorRegistry`内的`addInterceptor`需要一个实现`HandlerInterceptor`接口的拦截器实例，`addPathPatterns`方法用于设置拦截器的过滤路径规则。
 这里我拦截所有请求，通过判断是否有`@LoginRequired`注解 决定是否需要登录

# 6.jjwt集成

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.0</version>
</dependency>
```

```java
/**
 * jwt工具类
 */
public class JwtUtils {
    private static final String SECRET = "qYYjXt7s1C*nEC%9RCwQGFA$YwPr$Jaaarj";
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    private static final String TOKEN_PREFIX = "Bearer ";

    public static String sign(Map<String, Object> map, long maxAge) {
        return sign(map, null, null, maxAge);
    }

    /**
     * 生成JWT token
     *
     * @param map     传入数据
     * @param issuer  签发者
     * @param subject 面向用户
     * @param maxAge  有效期（单位：ms）
     */
    public static String sign(Map<String, Object> map, String issuer, String subject, long maxAge) {
        Date now = new Date(System.currentTimeMillis());
        String jwt = Jwts.builder()
                .setClaims(map) // 设置自定义数据
                .setIssuedAt(now) // 设置签发时间
                .setExpiration(new Date(now.getTime() + maxAge)) // 设置过期时间
                .setIssuer(issuer) // 设置签发者
                .setSubject(subject) // 设置面向用户
                .signWith(signatureAlgorithm, SECRET)
                .compact();
        return TOKEN_PREFIX + jwt;
    }

    public static Map unSign(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
        } catch (Exception e) {
            throw new IllegalStateException("Token验证失败：" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("userName", "admin");
        map.put("userId", "001");
        //2分钟的有效期，过2分钟token就不能解析了
        String token = JwtUtils.sign(map, 2 * 60 * 1000);
        System.out.println("token is ==>" + token);
        Map m = JwtUtils.unSign(token);
        System.out.println(m);
    }
}

```

# 7.对称算法和非对称算法

1. 对称加密
   对称加密指的就是加密和解密使用同一个秘钥，所以叫做对称加密。对称加密只有一个秘钥，作为私钥。
   常见的对称加密算法：DES，AES，3DES等等。

2. 非对称加密
   非对称加密指的是：加密和解密使用不同的秘钥，一把作为公开的公钥，另一把作为私钥。公钥加密的信息，只有私钥才能解密。私钥加密的信息，只有公钥才能解密。
   常见的非对称加密算法：RSA，ECC

3. 区别

   对称加密算法相比非对称加密算法来说，加解密的效率要高得多。但是缺陷在于对于秘钥的管理上，以及在非安全信道中通讯时，密钥交换的安全性不能保障。所以在实际的网络环境中，会将两者混合使用.

rsa秘钥对生成

```java
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RSADemo {

    public static void main(String[] args) throws NoSuchAlgorithmException {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        byte[] publicBytes = pair.getPublic().getEncoded();
        byte[] privateBytes = pair.getPrivate().getEncoded();

        System.out.println("public key: " + base64Encode(publicBytes));
        System.out.println("private key: " + base64Encode(privateBytes));
    }

    static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
```


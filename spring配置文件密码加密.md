

# 1.POM配置文件

```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.2</version>
</dependency>
```



# 2.生成加密串

```java
package com.huawei.aiflow.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class GenerateSecretKey {

    public static void main(String[] args) {
        //加密秘钥，需要在启动命令中添加：java -jar -Djasypt.encryptor.password=enc_password aaa.jar
        String encPassword="enc_password";
        //需要加密的密码，例如 mysql密码，redis密码等，甚至可以对用户名加密
        //将加密后的密码配置到yml配置文件中
        String sourcePassword="123456";
        
        String dstPassword="";
        PooledPBEStringEncryptor p = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("enc_password");
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        p.setConfig(config);
        
        //目标密码，需要替换到yaml或者properties配置文件中
        dstPassword=p.encrypt(sourcePassword);        
        
        //配置文件中配置的包含整个ENC(...)，例password: ENC(ZM1zExzhosdL+KP/P4GljzFM/1111111112adsfaffasfds)
        System.out.println("加密字符串为：ENC("+dstPassword+")");
    }
}

```



# 3.运行

## 3.1 在Idea配置

| 配置项                 | 配置内容                               |
| ---------------------- | -------------------------------------- |
| Environment variables: | jasypt.encryptor.password=enc_password |

单独在windows的环境变量中配置的话，注意在idea中，不需要额外配置，但是在系统变量中配置完毕，需要重启idea。

## 3.2 生产环境

```shell
java -jar -Djasypt.encryptor.password=enc_password aaa.jar
```






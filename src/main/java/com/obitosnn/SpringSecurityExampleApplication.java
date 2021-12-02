package com.obitosnn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.Inet4Address;

/**
 * @author ObitoSnn
 */
@SpringBootApplication
@MapperScan("**.mapper")
public class SpringSecurityExampleApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringSecurityExampleApplication.class, args);

        ConfigurableEnvironment environment = ctx.getEnvironment();

        String port = environment.getProperty("server.port");
        String contextPath = environment.getProperty("server.servlet.context-path");

        String ip = Inet4Address.getLocalHost().getHostAddress();

        System.out.println("\n===========================");
        System.out.println("swagger文档:http://localhost:" + port + contextPath + "/doc.html");
        System.out.println("swagger文档:http://" + ip + ":" + port + contextPath + "/doc.html");
        System.out.println("===========================\n");
    }
}

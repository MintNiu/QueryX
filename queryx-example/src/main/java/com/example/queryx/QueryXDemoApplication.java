package com.example.queryx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * QueryX Demo Application
 */
@SpringBootApplication
@MapperScan("com.example.queryx.mapper")
public class QueryXDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryXDemoApplication.class, args);
    }
}

package com.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.project.*.*mapper")
@EnableScheduling
public class CommonApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(CommonApplication.class, args);
        System.out.println("================================启动成功=========================");
    }
}

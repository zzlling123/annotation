package com.xinkao.erp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动入口
 */
@SpringBootApplication
@MapperScan("com.xinkao.erp.**.mapper")
public class MyApplication {

    public static void main(String[] args) {
      SpringApplication.run(MyApplication.class, args);
    }

}

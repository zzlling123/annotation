package com.xinkao.erp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 配置基类<br>
 * 继承此类,其他与启动springboot一样
 */
@SpringBootTest(classes = MyApplication.class)
@ActiveProfiles("dev")
public class MyApplicationTests {

}

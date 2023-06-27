package me.youmeng;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j  //log 方法记录日志
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement //开始事务
@EnableCaching //开启缓存
public class MyHISApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyHISApplication.class,args);
        log.info("项目启动成功了！");
    }
}

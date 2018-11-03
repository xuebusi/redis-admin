package com.xuebusi.cms;

import com.xuebusi.cms.base.modules.sys.service.SystemService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 *
 * @author: shiyanjun
 * @Date: 2018/10/14 下午5:30
 */
@EnableCaching
@SpringBootApplication
@ServletComponentScan("com.xuebusi.cms")
@ComponentScan(value = "com.xuebusi.cms", lazyInit = true)
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class).web(true).run(args);
        SystemService.printKeyLoadMessage();
    }
}

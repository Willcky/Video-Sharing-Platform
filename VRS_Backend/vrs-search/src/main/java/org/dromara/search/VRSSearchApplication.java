package org.dromara.search;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 搜索模块
 *
 * @author Kaiyuan Chen
 */
@EnableDubbo
@SpringBootApplication
@EnableScheduling
public class VRSSearchApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VRSSearchApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  搜索模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}

package org.dromara.video;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 演示模块
 *
 * @author Lion Li
 */
@EnableDubbo
@SpringBootApplication
@EnableScheduling
public class VRSVideoApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VRSVideoApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  视频模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}

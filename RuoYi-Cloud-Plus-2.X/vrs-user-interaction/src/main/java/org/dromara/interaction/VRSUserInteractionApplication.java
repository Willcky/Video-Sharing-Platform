package org.dromara.interaction;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 演示模块
 *
 * @author Lion Li
 */
@EnableDubbo
@SpringBootApplication
public class VRSUserInteractionApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VRSUserInteractionApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  互动模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}

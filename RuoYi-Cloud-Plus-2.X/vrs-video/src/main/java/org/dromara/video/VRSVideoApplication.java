package org.dromara.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 演示模块
 *
 * @author Lion Li
 */
@SpringBootApplication
public class VRSVideoApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(VRSVideoApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  视频模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}

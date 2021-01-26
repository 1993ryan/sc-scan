package nuaa.dp.hole;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.config.ParamHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Copyright NUAA
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/10/22 11:45 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/10/22      Dapeng Yan         v1.0.0
 */
@Slf4j
@SpringBootApplication
//@EnableScheduling
@EnableConfigurationProperties(ParamHandler.class)
@MapperScan(basePackages = { "nuaa.dp.hole.dal" })
public class HoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(HoleApplication.class, args);
    }

}
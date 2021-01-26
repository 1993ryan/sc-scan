package nuaa.dp.hole.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ParamHandler {

    @Getter
    private Delayq delayq;

    @Data
    public static class Delayq {
        private String server;
        private Integer origin;
    }

}
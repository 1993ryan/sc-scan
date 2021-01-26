package nuaa.dp.hole.dto;

import lombok.Data;

@Data
public class SaveProjectRequest {
    private Long id;
    private String groupId;
    private String projectName;
    private String remoteUrl;
    private String sourcePath;
    private String orderStatus;
}

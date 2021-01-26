package nuaa.dp.hole.dto;

import lombok.Data;

@Data
public class TaskItemQueryDto {
    private Long taskId;
    private String artifactId;
    private String dependentName;
    private String orderStatus;
    private boolean onlyMatched;
    private boolean matchedHistory;
}

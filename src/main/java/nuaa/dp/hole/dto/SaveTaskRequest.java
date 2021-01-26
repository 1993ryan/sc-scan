package nuaa.dp.hole.dto;

import lombok.Data;

/**
 * @Copyright LiuTian
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/11/9 8:46 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/11/9      Dapeng Yan         v1.0.0
 */
@Data
public class SaveTaskRequest {
    private Long taskId;
    private String groupId;
    private String artifactId;
    private String orderStatus;
}

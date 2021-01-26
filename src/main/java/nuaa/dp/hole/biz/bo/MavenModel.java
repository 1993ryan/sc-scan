package nuaa.dp.hole.biz.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/11/7 5:28 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/11/7      Dapeng Yan          v1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MavenModel {
    private String groupId;
    private String artifactId;
    private String version;
}

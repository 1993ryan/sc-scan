package nuaa.dp.hole.biz.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import nuaa.dp.hole.utils.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/11/4 8:03 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/11/4      Dapeng Yan         v1.0.0
 */
@Data
@AllArgsConstructor
public class TagItem {
    private String artifactId;
    private String version;
    private Date commitTime;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TagItem) {
            TagItem other = (TagItem)obj;
            return this.artifactId.equals(other.artifactId) && StringUtils.isNotBlank(this.version) && this.version.equals(other.version);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, version);
    }
}

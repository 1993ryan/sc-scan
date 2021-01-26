package nuaa.dp.hole.biz.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.*;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/10/23 5:55 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/10/23      Dapeng Yan          v1.0.0
 */
@Data
public class QueryResultBO {
    private String projectName;
    private String keyword;
    private List<ResultItem> currentItems = new ArrayList<>();
    private List<BeforeItem> lastItems = new ArrayList<>();
    private Set<TagItem> tagItems = new LinkedHashSet<>();
    private Integer beforeCommitCount = 0;

    @JsonIgnore
    private Set<String> beforeCommitIdSet = new HashSet<>();

    public void addCurrent(ResultItem item) {
        this.currentItems.add(item);
    }

    public void addBefore(BeforeItem item) {
        this.lastItems.add(item);

        this.beforeCommitIdSet.add(item.getBeforeRevision());
        this.beforeCommitCount = this.beforeCommitIdSet.size();
    }

    public void addTag(TagItem item) {
        if(!this.tagItems.contains(item)) {
            this.tagItems.add(item);
        }
    }
}

package nuaa.dp.hole.biz.bo;

import lombok.Data;

import java.util.Date;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/10/24 1:49 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/10/24      Dapeng Yan          v1.0.0
 */
@Data
public class BeforeItem {
    private String lastRevision;
    private Date lastCommitTime;
    private String filePath;
    private String newLine;
    private String lastAuthor;

    private String beforeRevision;
    private Date beforeCommitTime;
    private String beforeAuthor;
}

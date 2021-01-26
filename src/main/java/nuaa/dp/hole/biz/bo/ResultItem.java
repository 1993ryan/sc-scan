package nuaa.dp.hole.biz.bo;

import lombok.Data;

import java.util.Date;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/10/23 5:35 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/10/23      Dapeng Yan         v1.0.0
 */
@Data
public class ResultItem {
    private String revision;
    private Date commitTime;
    private String author;
    private String email;
    private String historyMessage;
    private String different;
}

package nuaa.dp.hole.task.bean;

import lombok.Data;

import java.util.Date;

/**
 * @Copyright MiXuan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2019-05-25 19:11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2019-05-25     Dapeng Yan         v1.0.0
 */
@Data
public class TaskBean {

    private long id;
    private Integer topic = -1;
    private long bizId;
    private String extInfo = "";
    private int exeCount;
    private String endpoint = "";

    private Date gmtCreate;
    private Date nextTime;

}


package nuaa.dp.hole.dal.dao;

import java.util.List;
import nuaa.dp.hole.dal.model.TaskItem;
import nuaa.dp.hole.dal.model.TaskItemExample;
import org.apache.ibatis.annotations.Param;

public interface TaskItemMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    long countByExample(TaskItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int deleteByExample(TaskItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int insert(TaskItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int insertSelective(TaskItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    List<TaskItem> selectByExampleWithBLOBs(TaskItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    List<TaskItem> selectByExample(TaskItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    TaskItem selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int updateByExampleSelective(@Param("record") TaskItem record, @Param("example") TaskItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int updateByExampleWithBLOBs(@Param("record") TaskItem record, @Param("example") TaskItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int updateByExample(@Param("record") TaskItem record, @Param("example") TaskItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int updateByPrimaryKeySelective(TaskItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int updateByPrimaryKeyWithBLOBs(TaskItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_task_item
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    int updateByPrimaryKey(TaskItem record);

    void initData(long id);
}
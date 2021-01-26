package nuaa.dp.hole.dal.dao;

import java.util.List;
import nuaa.dp.hole.dal.model.ProjectInfo;
import nuaa.dp.hole.dal.model.ProjectInfoExample;
import org.apache.ibatis.annotations.Param;

public interface ProjectInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    long countByExample(ProjectInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    int deleteByExample(ProjectInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    int insert(ProjectInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    int insertSelective(ProjectInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    List<ProjectInfo> selectByExample(ProjectInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    ProjectInfo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    int updateByExampleSelective(@Param("record") ProjectInfo record, @Param("example") ProjectInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    int updateByExample(@Param("record") ProjectInfo record, @Param("example") ProjectInfoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    int updateByPrimaryKeySelective(ProjectInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_project_info
     *
     * @mbg.generated Fri Nov 27 20:37:49 CST 2020
     */
    int updateByPrimaryKey(ProjectInfo record);
}
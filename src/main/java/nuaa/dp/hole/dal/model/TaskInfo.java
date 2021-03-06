package nuaa.dp.hole.dal.model;

import java.util.Date;

public class TaskInfo {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_info.id
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_info.gmt_create
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    private Date gmtCreate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_info.project_name
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    private String projectName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_info.keyword
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    private String keyword;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_info.order_status
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    private String orderStatus;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_info.id
     *
     * @return the value of t_task_info.id
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_info.id
     *
     * @param id the value for t_task_info.id
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_info.gmt_create
     *
     * @return the value of t_task_info.gmt_create
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_info.gmt_create
     *
     * @param gmtCreate the value for t_task_info.gmt_create
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_info.project_name
     *
     * @return the value of t_task_info.project_name
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_info.project_name
     *
     * @param projectName the value for t_task_info.project_name
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName == null ? null : projectName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_info.keyword
     *
     * @return the value of t_task_info.keyword
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_info.keyword
     *
     * @param keyword the value for t_task_info.keyword
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_info.order_status
     *
     * @return the value of t_task_info.order_status
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_info.order_status
     *
     * @param orderStatus the value for t_task_info.order_status
     *
     * @mbg.generated Sun Nov 29 11:05:23 CST 2020
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }
}
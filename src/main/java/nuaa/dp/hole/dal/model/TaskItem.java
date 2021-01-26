package nuaa.dp.hole.dal.model;

import java.util.Date;

public class TaskItem {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.gmt_create
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private Date gmtCreate;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.task_id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private Long taskId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.artifact_id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private String artifactId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.dependent_name
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private String dependentName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.matched_version
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private String matchedVersion;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.status_code
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private Integer statusCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.other_version
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private String otherVersion;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.order_status
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private String orderStatus;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_task_item.found_versions
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    private String foundVersions;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.id
     *
     * @return the value of t_task_item.id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.id
     *
     * @param id the value for t_task_item.id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.gmt_create
     *
     * @return the value of t_task_item.gmt_create
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.gmt_create
     *
     * @param gmtCreate the value for t_task_item.gmt_create
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.task_id
     *
     * @return the value of t_task_item.task_id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public Long getTaskId() {
        return taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.task_id
     *
     * @param taskId the value for t_task_item.task_id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.artifact_id
     *
     * @return the value of t_task_item.artifact_id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.artifact_id
     *
     * @param artifactId the value for t_task_item.artifact_id
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId == null ? null : artifactId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.dependent_name
     *
     * @return the value of t_task_item.dependent_name
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public String getDependentName() {
        return dependentName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.dependent_name
     *
     * @param dependentName the value for t_task_item.dependent_name
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setDependentName(String dependentName) {
        this.dependentName = dependentName == null ? null : dependentName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.matched_version
     *
     * @return the value of t_task_item.matched_version
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public String getMatchedVersion() {
        return matchedVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.matched_version
     *
     * @param matchedVersion the value for t_task_item.matched_version
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setMatchedVersion(String matchedVersion) {
        this.matchedVersion = matchedVersion == null ? null : matchedVersion.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.status_code
     *
     * @return the value of t_task_item.status_code
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.status_code
     *
     * @param statusCode the value for t_task_item.status_code
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.other_version
     *
     * @return the value of t_task_item.other_version
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public String getOtherVersion() {
        return otherVersion;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.other_version
     *
     * @param otherVersion the value for t_task_item.other_version
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setOtherVersion(String otherVersion) {
        this.otherVersion = otherVersion == null ? null : otherVersion.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.order_status
     *
     * @return the value of t_task_item.order_status
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public String getOrderStatus() {
        return orderStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.order_status
     *
     * @param orderStatus the value for t_task_item.order_status
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_task_item.found_versions
     *
     * @return the value of t_task_item.found_versions
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public String getFoundVersions() {
        return foundVersions;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_task_item.found_versions
     *
     * @param foundVersions the value for t_task_item.found_versions
     *
     * @mbg.generated Tue Dec 29 21:38:39 CST 2020
     */
    public void setFoundVersions(String foundVersions) {
        this.foundVersions = foundVersions == null ? null : foundVersions.trim();
    }
}
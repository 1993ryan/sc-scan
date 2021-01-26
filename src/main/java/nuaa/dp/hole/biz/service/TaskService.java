package nuaa.dp.hole.biz.service;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.cons.OrderStatus;
import nuaa.dp.hole.dal.common.PageInfo;
import nuaa.dp.hole.dal.common.PageResult;
import nuaa.dp.hole.dal.dao.CommonDao;
import nuaa.dp.hole.dal.dao.TaskChildMapper;
import nuaa.dp.hole.dal.dao.TaskInfoMapper;
import nuaa.dp.hole.dal.dao.TaskItemMapper;
import nuaa.dp.hole.dal.model.*;
import nuaa.dp.hole.dto.TaskItemQueryDto;
import nuaa.dp.hole.utils.DateUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Copyright LiuTian
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/11/10 9:17 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/11/10      Dapeng Yan         v1.0.0
 */
@Slf4j
@Service
public class TaskService {

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private TaskItemMapper taskItemMapper;

    @Autowired
    private TaskChildMapper taskChildMapper;

    @Autowired
    private CommonDao commonDao;

    public long insert(TaskInfo taskInfo) {
        taskInfo.setGmtCreate(DateUtils.now());
        taskInfo.setOrderStatus(OrderStatus.CREATE.name());
        this.taskInfoMapper.insertSelective(taskInfo);

        long id = this.commonDao.getLastInsertId();
        taskInfo.setId(id);
        return id;
    }

    public TaskInfo loadById(Long id) {
        return this.taskInfoMapper.selectByPrimaryKey(id);
    }

    public TaskItem getTaskItem(long itemId) {
        return this.taskItemMapper.selectByPrimaryKey(itemId);
    }

    public int deleteById(long id) {
        return this.taskInfoMapper.deleteByPrimaryKey(id);
    }

    public void saveTaskInfo(TaskInfo taskInfo) {
        this.taskInfoMapper.updateByPrimaryKey(taskInfo);
    }

    public List<TaskInfo> queryForList() {
        TaskInfoExample example = new TaskInfoExample();
        return this.taskInfoMapper.selectByExample(example);
    }

    public long queryTaskItemMatchedCount(long taskId, String artifactId) {
        TaskItemExample example = new TaskItemExample();
        TaskItemExample.Criteria criteria = example.createCriteria();
        criteria.andTaskIdEqualTo(taskId).andMatchedVersionIsNotNull();
        if(StringUtils.isNotBlank(artifactId)) {
            criteria.andArtifactIdEqualTo(artifactId);
        }
        example.setDistinct(true);
        return this.taskItemMapper.countByExample(example);
    }

    public List<TaskChild> queryTaskChildren(Long taskId) {
        TaskChildExample example = new TaskChildExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        return this.taskChildMapper.selectByExampleWithBLOBs(example);
    }

    public List<TaskItem> queryTaskItems(Long taskId, String artifactId) {
        TaskItemExample example = new TaskItemExample();
        TaskItemExample.Criteria criteria = example.createCriteria();
        criteria.andTaskIdEqualTo(taskId);
        if(StringUtils.isNotBlank(artifactId)) {
            criteria.andArtifactIdEqualTo(artifactId);
        }
        return this.taskItemMapper.selectByExample(example);
    }

    public PageResult<TaskItem> queryForItems(TaskItemQueryDto queryDto, PageInfo pageInfo) {
        TaskItemExample example = new TaskItemExample();
        TaskItemExample.Criteria criteria = example.createCriteria();
        if(Objects.nonNull(queryDto.getTaskId())) {
            criteria.andTaskIdEqualTo(queryDto.getTaskId());
        }
        if(StringUtils.isNotBlank(queryDto.getArtifactId())) {
            criteria.andArtifactIdEqualTo(queryDto.getArtifactId());
        }
        if(StringUtils.isNotBlank(queryDto.getDependentName())) {
            criteria.andDependentNameEqualTo(queryDto.getDependentName());
        }
        if(StringUtils.isNotBlank(queryDto.getOrderStatus())) {
            criteria.andOrderStatusEqualTo(queryDto.getOrderStatus());
        }
        if(queryDto.isOnlyMatched()) {
            criteria.andMatchedVersionIsNotNull();
        }
        if(queryDto.isMatchedHistory()) {
            criteria.andOtherVersionIsNotNull();
        }

        int rowCount = (int)this.taskItemMapper.countByExample(example);
        pageInfo.setRowCount(rowCount);
        if (rowCount <= 0) {
            return new PageResult<>(Collections.EMPTY_LIST, pageInfo);
        }

        example.setOrderByClause("id asc limit " + pageInfo.getStartIndex() + ", " + pageInfo.getPageSize());
        List<TaskItem> dataList = this.taskItemMapper.selectByExampleWithBLOBs(example);
        return new PageResult<>(dataList, pageInfo);
    }
}

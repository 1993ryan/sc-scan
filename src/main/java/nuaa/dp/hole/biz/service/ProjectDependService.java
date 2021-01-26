package nuaa.dp.hole.biz.service;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.cons.OrderStatus;
import nuaa.dp.hole.dal.common.PageInfo;
import nuaa.dp.hole.dal.common.PageResult;
import nuaa.dp.hole.dal.dao.ProjectDependentMapper;
import nuaa.dp.hole.dal.model.ProjectDependent;
import nuaa.dp.hole.dal.model.ProjectDependentExample;
import nuaa.dp.hole.dto.ProjectDependentQueryDto;
import nuaa.dp.hole.utils.DateUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ProjectDependService {

    @Autowired
    private ProjectDependentMapper projectDependentMapper;

    public void insertDependent(long projectId, String artifactId, String dependent) {
        Date now = DateUtils.now();
        ProjectDependent item = new ProjectDependent();
        item.setGmtCreate(now);
        item.setProjectId(projectId);
        item.setArtifactId(artifactId);
        item.setSimpleName(dependent);
        item.setOrderStatus(OrderStatus.CREATE.name());

        this.projectDependentMapper.insertSelective(item);
    }

    public  ProjectDependent loadById(long id) {
        return this.projectDependentMapper.selectByPrimaryKey(id);
    }

    public void saveDependent(ProjectDependent dependent) {
        dependent.setGmtCreate(DateUtils.now());
        this.projectDependentMapper.updateByPrimaryKeyWithBLOBs(dependent);
    }

    public ProjectDependent checkIfTaskItemExists(ProjectDependent taskItem) {
        ProjectDependentQueryDto queryDto = new ProjectDependentQueryDto();
        queryDto.setSimpleName(taskItem.getSimpleName());

        PageResult<ProjectDependent> pageResult = this.queryForList(queryDto, new PageInfo());
        if(pageResult.isSuccess()) {
            for (ProjectDependent item : pageResult.getData()) {
                if(Objects.nonNull(item.getStatusCode()) && 200 == item.getStatusCode()) {
                    return item;
                }
            }
        }
        return null;
    }

    public PageResult<ProjectDependent> queryForList(ProjectDependentQueryDto queryDto, PageInfo pageInfo) {
        ProjectDependentExample example = new ProjectDependentExample();
        ProjectDependentExample.Criteria criteria = example.createCriteria();
        if(Objects.nonNull(queryDto.getProjectId())) {
            criteria.andProjectIdEqualTo(queryDto.getProjectId());
        }
        if(StringUtils.isNoneBlank(queryDto.getSimpleName())) {
            criteria.andSimpleNameEqualTo(queryDto.getSimpleName());
        }

        int rowCount = (int)this.projectDependentMapper.countByExample(example);
        pageInfo.setRowCount(rowCount);
        if (rowCount <= 0) {
            return new PageResult<>(Collections.EMPTY_LIST, pageInfo);
        }

        example.setOrderByClause("id asc limit " + pageInfo.getStartIndex() + ", " + pageInfo.getPageSize());
        List<ProjectDependent> dataList = this.projectDependentMapper.selectByExample(example);
        return new PageResult<>(dataList, pageInfo);
    }
}

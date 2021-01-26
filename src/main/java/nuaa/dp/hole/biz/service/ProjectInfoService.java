package nuaa.dp.hole.biz.service;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.dal.common.PageInfo;
import nuaa.dp.hole.dal.common.PageResult;
import nuaa.dp.hole.dal.dao.CommonDao;
import nuaa.dp.hole.dal.dao.ProjectInfoMapper;
import nuaa.dp.hole.dal.model.ProjectInfo;
import nuaa.dp.hole.dal.model.ProjectInfoExample;
import nuaa.dp.hole.dal.model.TaskItem;
import nuaa.dp.hole.dal.model.TaskItemExample;
import nuaa.dp.hole.dto.ProjectInfoQueryDto;
import nuaa.dp.hole.dto.TaskItemQueryDto;
import nuaa.dp.hole.utils.DateUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ProjectInfoService {

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Autowired
    private CommonDao commonDao;

    public long insert(ProjectInfo projectInfo) {
        projectInfo.setGmtCreate(DateUtils.now());
        this.projectInfoMapper.insertSelective(projectInfo);

        long id = this.commonDao.getLastInsertId();
        projectInfo.setId(id);
        return id;
    }

    public ProjectInfo loadById(Long id) {
        return this.projectInfoMapper.selectByPrimaryKey(id);
    }

    public ProjectInfo findByName(String name) {
        ProjectInfoExample example = new ProjectInfoExample();
        ProjectInfoExample.Criteria criteria = example.createCriteria();
        criteria.andProjectNameEqualTo(name);

        List<ProjectInfo> dataList = this.projectInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        return dataList.get(0);
    }


    public int deleteById(long id) {
        return this.projectInfoMapper.deleteByPrimaryKey(id);
    }

    public long save(ProjectInfo projectInfo) {
        if(Objects.isNull(projectInfo.getId()) || Long.MAX_VALUE == projectInfo.getId()) {
            return this.insert(projectInfo);
        } else {
            return this.projectInfoMapper.updateByPrimaryKeySelective(projectInfo);
        }
    }

    public PageResult<ProjectInfo> queryForList(ProjectInfoQueryDto queryDto, PageInfo pageInfo) {
        ProjectInfoExample example = new ProjectInfoExample();
        ProjectInfoExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(queryDto.getProjectName())) {
            criteria.andProjectNameEqualTo(queryDto.getProjectName());
        }

        int rowCount = (int)this.projectInfoMapper.countByExample(example);
        pageInfo.setRowCount(rowCount);
        if (rowCount <= 0) {
            return new PageResult<>(Collections.EMPTY_LIST, pageInfo);
        }

        example.setOrderByClause("id asc limit " + pageInfo.getStartIndex() + ", " + pageInfo.getPageSize());
        List<ProjectInfo> dataList = this.projectInfoMapper.selectByExample(example);
        return new PageResult<>(dataList, pageInfo);
    }
}

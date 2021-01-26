package nuaa.dp.hole.biz.repository;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.bo.QueryResultBO;
import nuaa.dp.hole.biz.bo.TagItem;
import nuaa.dp.hole.biz.service.ITaskQueueService;
import nuaa.dp.hole.biz.service.ProjectInfoService;
import nuaa.dp.hole.biz.service.TaskService;
import nuaa.dp.hole.cons.OrderStatus;
import nuaa.dp.hole.dal.common.Result;
import nuaa.dp.hole.dal.dao.ProjectDependentMapper;
import nuaa.dp.hole.dal.dao.TaskChildMapper;
import nuaa.dp.hole.dal.dao.TaskItemMapper;
import nuaa.dp.hole.dal.model.*;
import nuaa.dp.hole.task.base.TaskTopic;
import nuaa.dp.hole.task.bean.TaskCreateDto;
import nuaa.dp.hole.utils.BizUtils;
import nuaa.dp.hole.utils.DateUtils;
import nuaa.dp.hole.utils.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Copyright Dapeng Yan
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/11/8 6:04 PM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/11/8      Dapeng Yan          v1.0.0
 */
@Slf4j
@Component
public class TaskRepository {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private ITaskQueueService taskQueueService;

    @Autowired
    private ProjectDependentMapper projectDependentMapper;

    @Autowired
    private TaskChildMapper taskChildMapper;

    @Autowired
    private TaskItemMapper taskItemMapper;

    public void saveTaskInfoForQueryResult(QueryResultBO resultBo) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKeyword(resultBo.getKeyword());
        taskInfo.setProjectName(resultBo.getProjectName());
        long taskId = this.taskService.insert(taskInfo);

        Set<TagItem> tagItems = resultBo.getTagItems();
        Timestamp now = DateUtils.now();
        Map<String, Set<String>> versionMap = new ConcurrentHashMap<>();
        for (TagItem tagItem : tagItems) {
            Set<String> set = versionMap.get(tagItem.getArtifactId());
            if(Objects.isNull(set)) {
                set = new HashSet<>();
            }
            set.add(tagItem.getVersion());
            versionMap.put(tagItem.getArtifactId(), set);
        }

        for(String key: versionMap.keySet()) {
            TaskChild child = new TaskChild();
            child.setGmtCreate(now);
            child.setTaskId(taskId);
            child.setArtifactId(key);
            child.setFoundVersions(StringUtils.toString(versionMap.get(key)));
            child.setOrderStatus(OrderStatus.CREATE.name());
            this.taskChildMapper.insert(child);
        }
        this.taskItemMapper.initData(taskId);
    }

    public Result<Integer> doVersionMatch(TaskInfo taskInfo) {
        taskInfo.setOrderStatus(OrderStatus.PROCESS.name());
        this.taskService.saveTaskInfo(taskInfo);

        List<TaskChild> children = this.taskService.queryTaskChildren(taskInfo.getId());
        if(CollectionUtils.isEmpty(children)) {
            return Result.failed("child is empty");
        }

        ProjectInfo projectInfo = this.projectInfoService.findByName(taskInfo.getProjectName());
        if(Objects.isNull(projectInfo)) {
            return Result.failed("project not found");
        }

        for (TaskChild child : children) {
            List<TaskItem> itemList = this.taskService.queryTaskItems(taskInfo.getId(), child.getArtifactId());
            child.setOrderStatus(OrderStatus.PROCESS.name());
            this.taskChildMapper.updateByPrimaryKeySelective(child);

            int matchCount = 0;
            for (TaskItem taskItem : itemList) {
                ProjectDependentExample example = new ProjectDependentExample();
                example.createCriteria().andProjectIdEqualTo(projectInfo.getId())
                        .andArtifactIdEqualTo(child.getArtifactId())
                        .andSimpleNameEqualTo(taskItem.getDependentName());
                List<ProjectDependent> dependents = this.projectDependentMapper.selectByExampleWithBLOBs(example);
                if(CollectionUtils.isEmpty(dependents)) {
                    taskItem.setStatusCode(600);
                    this.taskItemMapper.updateByPrimaryKeySelective(taskItem);
                    continue;
                }

                try {
                    ProjectDependent dependent = dependents.get(0);
                    taskItem.setStatusCode(dependent.getStatusCode());

                    if(Objects.isNull(dependent.getStatusCode())) {
//                        return Result.failed("Dependent not complete.");
                        continue;
                    }

                    if(200 == dependent.getStatusCode() && StringUtils.isNotBlank(dependent.getPomContent())) {
                        String matchedVersion = this.parsePomFile(projectInfo.getGroupId(), child, dependent.getPomContent());
                        if(StringUtils.isNotBlank(matchedVersion)) {
                            taskItem.setMatchedVersion(matchedVersion);
                            matchCount ++;
                        } else {
                            TaskCreateDto tcd = new TaskCreateDto(TaskTopic.REPOSITORY_CLONE);
                            tcd.setBizId(taskItem.getId());
                            this.taskQueueService.insert(tcd);

                            this.taskItemMapper.updateByPrimaryKeySelective(taskItem);
                            continue;
                        }
                    }
                    taskItem.setOrderStatus(OrderStatus.SUCCESS.name());
                    this.taskItemMapper.updateByPrimaryKeySelective(taskItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            child.setMatchCount(matchCount);
            child.setOrderStatus(OrderStatus.SUCCESS.name());
            this.taskChildMapper.updateByPrimaryKeySelective(child);
        }

        taskInfo.setOrderStatus(OrderStatus.SUCCESS.name());
        this.taskService.saveTaskInfo(taskInfo);

        return Result.success(children.size());
    }

    public String parsePomFile(String groupId, TaskChild child, String content) throws IOException, XmlPullParserException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(inputStream);
        DependencyManagement dependencyManagement = model.getDependencyManagement();
        if(Objects.nonNull(dependencyManagement)) {
            return this.checkDependency(groupId, child, dependencyManagement.getDependencies(), model.getProperties());
        } else {
            return this.checkDependency(groupId, child, model.getDependencies(), model.getProperties());
        }
    }

    private String checkDependency(String groupId, TaskChild child, List<Dependency> dependencyList, final Properties properties) {
        if(Objects.isNull(child)) {
            return null;
        }

        List<String> foundVersions = Splitter.on(",").splitToList(child.getFoundVersions());
        for (Dependency key : dependencyList) {
            String version = key.getVersion();
            if(StringUtils.isBlank(version)) {
                continue;
            }
            if(version.contains("${")) {
                String propKey = version.replaceAll("\\$\\{", "").replaceAll("}", "").trim();
                version = properties.getProperty(propKey);
            }

            if(!key.getGroupId().equals(groupId)) {
                continue;
            }

            if(!key.getArtifactId().equals(child.getArtifactId())) {
                continue;
            }

            if(BizUtils.hasElem(foundVersions, version)) {
                return version;
            }
        }
        return null;
    }

    public void refreshMatchedCount(long taskId, String artifactId) {
        TaskItemExample example = new TaskItemExample();
        example.createCriteria().andTaskIdEqualTo(taskId).andArtifactIdEqualTo(artifactId);
        List<TaskItem> taskItems = this.taskItemMapper.selectByExample(example);
        int matchedCount = 0;
        int matchedHistory = 0;
        for (TaskItem taskItem : taskItems) {
            if(StringUtils.isNotBlank(taskItem.getMatchedVersion())) {
                matchedCount ++;
            }
            if(StringUtils.isNotBlank(taskItem.getOtherVersion())) {
                matchedHistory ++;
            }
        }

        TaskChildExample childExample = new TaskChildExample();
        childExample.createCriteria().andTaskIdEqualTo(taskId).andArtifactIdEqualTo(artifactId);

        TaskChild child = new TaskChild();
        child.setMatchCount(matchedCount);
        child.setMatchHistory(matchedHistory);
        this.taskChildMapper.updateByExampleSelective(child, childExample);
    }
}

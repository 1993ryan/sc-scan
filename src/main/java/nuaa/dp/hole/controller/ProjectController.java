package nuaa.dp.hole.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.repository.DependentRepository;
import nuaa.dp.hole.biz.service.ProjectInfoService;
import nuaa.dp.hole.dal.common.ErrorCode;
import nuaa.dp.hole.dal.common.PageInfo;
import nuaa.dp.hole.dal.common.PageResult;
import nuaa.dp.hole.dal.common.Result;
import nuaa.dp.hole.dal.model.ProjectInfo;
import nuaa.dp.hole.dto.ProjectInfoQueryDto;
import nuaa.dp.hole.dto.SaveProjectRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private DependentRepository dependentRepository;

    @Autowired
    private ProjectInfoService projectInfoService;

    @RequestMapping
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

        return "project_list";
    }

    @PostMapping("query")
    @ResponseBody
    public JSONObject queryForItem(HttpServletRequest request, HttpServletResponse response,
                                   ProjectInfoQueryDto queryDto, PageInfo pageInfo) {
        JSONObject json = new JSONObject();
        PageResult<ProjectInfo> pageResult = this.projectInfoService.queryForList(queryDto, pageInfo);

        json.put("items", pageResult.getData());
        json.put("total", pageResult.getPageInfo().getRowCount());
        return json;
    }

    @PostMapping("save")
    @ResponseBody
    public Result<String> save(HttpServletRequest request, HttpServletResponse response,
                                   SaveProjectRequest saveProjectRequest) {
        ProjectInfo projectInfo = this.projectInfoService.loadById(saveProjectRequest.getId());
        if(Objects.isNull(projectInfo)) {
            projectInfo = new ProjectInfo();
        }

        BeanUtils.copyProperties(saveProjectRequest, projectInfo);
        this.projectInfoService.save(projectInfo);
        return Result.success("ok");
    }

    @RequestMapping("findDependents")
    @ResponseBody
    public Result<Integer> findDependents(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam("projectId") Long projectId) throws Exception {
        ProjectInfo projectInfo = this.projectInfoService.loadById(projectId);
        if(Objects.isNull(projectInfo)) {
            return Result.failed("TaskNotFound");
        }

        return this.dependentRepository.findDependents(projectInfo);
    }

    @RequestMapping("fetchPomFiles")
    @ResponseBody
    public Result<Integer> fetchPomFiles(HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam("projectId") Long projectId) throws Exception {
        ProjectInfo projectInfo = this.projectInfoService.loadById(projectId);
        if(Objects.isNull(projectInfo)) {
            return Result.failed("TaskNotFound");
        }

        return this.dependentRepository.fetchPomFiles(projectInfo);
    }

    @PostMapping("delete")
    @ResponseBody
    public Result<String> delete(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("projectId") long projectId) {
        ProjectInfo projectInfo = this.projectInfoService.loadById(projectId);
        if(Objects.isNull(projectInfo)) {
            return Result.failed(ErrorCode.OBJECT_NOT_EXISTS);
        }

        this.projectInfoService.deleteById(projectId);
        return Result.success("ok");
    }

}

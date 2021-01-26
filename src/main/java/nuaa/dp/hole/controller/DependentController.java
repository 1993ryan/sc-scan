package nuaa.dp.hole.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.service.ProjectDependService;
import nuaa.dp.hole.biz.service.ProjectInfoService;
import nuaa.dp.hole.dal.common.PageInfo;
import nuaa.dp.hole.dal.common.PageResult;
import nuaa.dp.hole.dal.model.ProjectDependent;
import nuaa.dp.hole.dal.model.ProjectInfo;
import nuaa.dp.hole.dal.model.TaskInfo;
import nuaa.dp.hole.dal.model.TaskItem;
import nuaa.dp.hole.dto.ProjectDependentQueryDto;
import nuaa.dp.hole.dto.TaskItemQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/api/dependent")
public class DependentController {

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private ProjectDependService projectDependService;

    @GetMapping("get")
    public String get(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("id") Long id) throws Exception {
        ProjectInfo projectInfo = this.projectInfoService.loadById(id);
        if(Objects.isNull(projectInfo)) {
            projectInfo = new ProjectInfo();
            projectInfo.setId(Long.MAX_VALUE);
        }
        request.setAttribute("entity", projectInfo);
        return "project_item";
    }

    @PostMapping("query")
    @ResponseBody
    public JSONObject query(HttpServletRequest request, HttpServletResponse response,
                                   ProjectDependentQueryDto queryDto, PageInfo pageInfo) {
        JSONObject json = new JSONObject();
        PageResult<ProjectDependent> pageResult = this.projectDependService.queryForList(queryDto, pageInfo);

        json.put("items", pageResult.getData());
        json.put("total", pageResult.getPageInfo().getRowCount());
        return json;
    }

}

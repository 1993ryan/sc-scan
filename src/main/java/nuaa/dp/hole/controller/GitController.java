package nuaa.dp.hole.controller;

import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.bo.ResultItem;
import nuaa.dp.hole.biz.bo.QueryResultBO;
import nuaa.dp.hole.biz.repository.GitRepository;
import nuaa.dp.hole.biz.repository.TaskRepository;
import nuaa.dp.hole.dal.common.Result;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Copyright LiuTian
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/10/23 10:21 AM
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/10/23      Dapeng Yan          v1.0.0
 */
@Slf4j
@Controller
@RequestMapping("/api/git")
public class GitController {

    @Autowired
    private GitRepository gitRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "index";
    }

    @PostMapping("query")
    @ResponseBody
    public Result<QueryResultBO> query(HttpServletRequest request, HttpServletResponse response,
                                       @RequestParam("projectName") String projectName, @RequestParam("keyword") String keyword) {
        QueryResultBO resultBO = null;
        try {
            Result<Git> gitResult = this.gitRepository.parseGit(projectName);
            if(!gitResult.isSuccess()) {
                return Result.failed(gitResult.getMessage());
            }

            resultBO = this.gitRepository.queryByKeyword(gitResult.getData(), keyword);
            if(Objects.isNull(resultBO)) {
                return Result.failed("Query fail");
            }

            //这里将搜索结果保存起来，用于下次做关联的任务记录
            resultBO.setProjectName(projectName);
            resultBO.setKeyword(keyword);
            this.taskRepository.saveTaskInfoForQueryResult(resultBO);
            log.info("keyword: {}, found {} related, {} reference.", keyword, resultBO.getCurrentItems().size(), resultBO.getLastItems().size());
            return Result.success(resultBO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(e.getMessage());
        }
    }

    @GetMapping("findByRevision")
    public String findByRevision(HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam("projectName") String projectName, @RequestParam("revision") String revision) throws Exception {
        Result<Git> gitResult = this.gitRepository.parseGit(projectName);
        if(gitResult.isSuccess()) {
            Result<ResultItem> result = this.gitRepository.findByRevision(gitResult.getData(), revision);
            if(result.isSuccess()) {
                request.setAttribute("item", result.getData());
            }
        }
        return "revision_detail";
    }

}

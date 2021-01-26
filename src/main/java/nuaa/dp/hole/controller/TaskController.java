package nuaa.dp.hole.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nuaa.dp.hole.biz.repository.TaskRepository;
import nuaa.dp.hole.biz.service.TaskService;
import nuaa.dp.hole.cons.OrderStatus;
import nuaa.dp.hole.dal.common.ErrorCode;
import nuaa.dp.hole.dal.common.PageInfo;
import nuaa.dp.hole.dal.common.PageResult;
import nuaa.dp.hole.dal.common.Result;
import nuaa.dp.hole.dal.model.TaskChild;
import nuaa.dp.hole.dal.model.TaskInfo;
import nuaa.dp.hole.dal.model.TaskItem;
import nuaa.dp.hole.dto.SaveTaskRequest;
import nuaa.dp.hole.dto.TaskItemQueryDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @PostMapping("query")
    @ResponseBody
    public Result<List<TaskInfo>> query(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<TaskInfo> taskList = this.taskService.queryForList();
        return Result.success(taskList);
    }

    @RequestMapping("detail")
    public String showDetail(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("taskId") Long taskId, PageInfo pageInfo) throws Exception {
        TaskInfo taskInfo = this.taskService.loadById(taskId);
        if(Objects.isNull(taskInfo)) {
            request.setAttribute("error", "TaskNotFound");
        } else {
            request.setAttribute("taskInfo", taskInfo);
            long matchedRows = this.taskService.queryTaskItemMatchedCount(taskId, null);
            request.setAttribute("matchedRows", matchedRows);

            List<TaskChild> children = this.taskService.queryTaskChildren(taskId);
            request.setAttribute("children", children);
        }
        return "task_detail";
    }

    @RequestMapping("showItem")
    public String showItem(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam("taskId") Long taskId, PageInfo pageInfo) throws Exception {
        TaskInfo taskInfo = this.taskService.loadById(taskId);
        if(Objects.isNull(taskInfo)) {
            request.setAttribute("error", "TaskNotFound");
        } else {
            request.setAttribute("taskInfo", taskInfo);
        }
        return "task_item";
    }

    @PostMapping("queryForItem")
    @ResponseBody
    public JSONObject queryForItem(HttpServletRequest request, HttpServletResponse response,
                                   TaskItemQueryDto queryDto, PageInfo pageInfo) {
        JSONObject json = new JSONObject();
        PageResult<TaskItem> pageResult = this.taskService.queryForItems(queryDto, pageInfo);

        json.put("items", pageResult.getData());
        json.put("total", pageResult.getPageInfo().getRowCount());
        return json;
    }

    @PostMapping("delete")
    @ResponseBody
    public Result<String> deleteTask(HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam("taskId") long taskId) {
        TaskInfo taskInfo = this.taskService.loadById(taskId);
        if(Objects.isNull(taskInfo)) {
            return Result.failed(ErrorCode.OBJECT_NOT_EXISTS);
        }

        this.taskService.deleteById(taskId);
        return Result.success("ok");
    }

    @PostMapping("save")
    @ResponseBody
    public Result<String> saveTask(HttpServletRequest request, HttpServletResponse response,
                                   SaveTaskRequest saveTaskRequest) {
        TaskInfo taskInfo = this.taskService.loadById(saveTaskRequest.getTaskId());
        if(Objects.isNull(taskInfo)) {
            return Result.failed("param error");
        }

        BeanUtils.copyProperties(saveTaskRequest, taskInfo);
        this.taskService.saveTaskInfo(taskInfo);
        return Result.success("ok");
    }

    @RequestMapping("trigger")
    @ResponseBody
    public Result<Integer> triggerTask(HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam("taskId") Long taskId) throws Exception {
        TaskInfo taskInfo = this.taskService.loadById(taskId);
        if(Objects.isNull(taskId)) {
            return Result.failed("TaskNotFound");
        } else if(OrderStatus.SUCCESS.name().equals(taskInfo.getOrderStatus())) {
            return Result.failed("Task Status has changed.");
        }

        return this.taskRepository.doVersionMatch(taskInfo);
    }

}

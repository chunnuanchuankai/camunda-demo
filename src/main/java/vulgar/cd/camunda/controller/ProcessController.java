package vulgar.cd.camunda.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.web.bind.annotation.*;
import vulgar.cd.camunda.domain.input.ProcessStartParam;
import vulgar.cd.camunda.domain.input.TaskClaimParam;
import vulgar.cd.camunda.domain.input.TaskReviewParam;
import vulgar.cd.camunda.domain.output.ResultBean;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api
@RestController
public class ProcessController {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private IdentityService identityService;
    @Resource
    private HistoryService historyService;

    /**
     * 发起流程
     *
     * @param param ProcessStartParam
     * @return ResultBean
     */
    @ApiOperation("发起流程")
    @PostMapping("/process/start")
    public ResultBean<?> startProcess(@Valid @RequestBody ProcessStartParam param) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(param.getProcessDefinitionId());
        Map<String, Object> map = new HashMap<>();
        map.put("processDefinitionId", processInstance.getProcessDefinitionId());
        map.put("processInstanceId", processInstance.getProcessInstanceId());
        map.put("businessKey", processInstance.getBusinessKey());
        map.put("caseInstanceId", processInstance.getCaseInstanceId());
        map.put("rootProcessInstanceId", processInstance.getRootProcessInstanceId());
        return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", map);
    }

    /**
     * 查询自己的任务列表
     *
     * @param assignee 任务所属者
     * @return ResultBean
     */
    @ApiOperation("查询自己的任务列表")
    @GetMapping("/self/tasks")
    public ResultBean<?> selfTaskList(@RequestParam("assignee") String assignee) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(assignee).list();
        if (tasks != null && !tasks.isEmpty()) {
            List<Map<String, Object>> resultList = new ArrayList<>(tasks.size());
            tasks.forEach(task -> {
                Map<String, Object> map = new HashMap<>(3);
                map.put("processDefinitionId", task.getProcessDefinitionId());
                map.put("processInstanceId", task.getProcessInstanceId());
                map.put("taskId", task.getId());
                resultList.add(map);
            });
            return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", resultList);
        } else {
            return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", "未查询到任务列表");
        }
    }

    /**
     * 可用任务列表
     *
     * @param username 用户名称
     * @return ResultBean
     */
    @ApiOperation("可用任务列表")
    @GetMapping("/available/tasks")
    public ResultBean<?> availableTaskList(@RequestParam("username") String username) {
        //1、根据用户名查询所在组
        List<Group> list = identityService.createGroupQuery().groupMember(username).list();
        if (list != null && !list.isEmpty()) {
            //2、获取所有组名
            List<String> collect = list.stream().map(Group::getId).collect(Collectors.toList());
            //3、根据组名查询所有任务
            List<Task> tasks = taskService.createTaskQuery().taskCandidateGroupIn(collect).list();
            if (tasks != null && !tasks.isEmpty()) {
                List<Map<String, Object>> resultList = new ArrayList<>(tasks.size());
                tasks.forEach(task -> {
                    Map<String, Object> map = new HashMap<>(3);
                    map.put("processDefinitionId", task.getProcessDefinitionId());
                    map.put("processInstanceId", task.getProcessInstanceId());
                    map.put("taskId", task.getId());
                    resultList.add(map);
                });
                return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", resultList);
            } else {
                return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", "未查询到任务列表");
            }
//            return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", tasks);
        } else {
            return new ResultBean<>(HttpServletResponse.SC_NOT_FOUND, "ERROR", "未查询到所属组");
        }
    }

    /**
     * 任务领取
     *
     * @param param TaskClaimParam
     * @return ResultBean
     */
    @ApiOperation("任务领取")
    @PostMapping("/claim/task")
    public ResultBean<String> claimTask(@Valid @RequestBody TaskClaimParam param) {
        //1、查询组名
        List<Group> list = identityService.createGroupQuery().groupMember(param.getUsername()).list();
        if (list != null && !list.isEmpty()) {
            List<String> collect = list.stream().map(Group::getId).collect(Collectors.toList());
            //2、查询任务
            Task task = taskService.createTaskQuery().taskCandidateGroupIn(collect).processInstanceId(param.getProcessInstanceId()).singleResult();
            if (task != null) {
                //3、判断该任务是否被领取
                if (StringUtils.isBlank(task.getAssignee())) {
                    taskService.claim(task.getId(), param.getUsername());
                    return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", "任务领取成功");
                } else {
                    return new ResultBean<>(HttpServletResponse.SC_FOUND, "SUCCESS", "任务已被他人领取");
                }
            } else {
                return new ResultBean<>(HttpServletResponse.SC_NOT_FOUND, "ERROR", "未查询到任务");
            }
        } else {
            return new ResultBean<>(HttpServletResponse.SC_NOT_FOUND, "ERROR", "未查询到所属组");
        }
    }

    /**
     * 任务审核
     *
     * @param param TaskReviewParam
     * @return ResultBean
     */
    @ApiOperation("任务审核")
    @PostMapping("/review/task")
    public ResultBean<String> review(@Valid @RequestBody TaskReviewParam param) {
        //1、查询任务
        Task task = taskService.createTaskQuery().taskId(param.getTaskId()).taskAssignee(param.getUsername()).singleResult();
        if (task != null) {
            taskService.complete(param.getTaskId());
            return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCESS", "任务审核成功");
        } else {
            return new ResultBean<>(HttpServletResponse.SC_NOT_FOUND, "ERROR", "未查询到任务");
        }
    }

    /**
     * 流程历史记录
     *
     * @param processInstanceId 流程实例id
     * @return ResultBean
     */
    @ApiOperation("流程历史记录")
    @GetMapping("/histories")
    public ResultBean<?> processHistories(@RequestParam("processInstanceId") String processInstanceId) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
        if (list != null && !list.isEmpty()) {
            return new ResultBean<>(HttpServletResponse.SC_OK, "SUCCCESS", list);
        } else {
            return new ResultBean<>(HttpServletResponse.SC_NOT_FOUND, "ERROR", "未查询到流程历史");
        }
    }
}
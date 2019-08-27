package vulgar.cd.camunda.domain.input;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class TaskReviewParam implements Serializable {
    private static final long serialVersionUID = 7825176656778655402L;

    /**
     * 任务id
     */
    @NotBlank(message = "任务id")
    private String taskId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名")
    private String username;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

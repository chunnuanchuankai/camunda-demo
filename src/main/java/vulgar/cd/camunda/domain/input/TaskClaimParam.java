package vulgar.cd.camunda.domain.input;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class TaskClaimParam implements Serializable {
    private static final long serialVersionUID = 853920625348598345L;

    /**
     * 流程实例id
     */
    @NotBlank(message = "流程实例id不允许为空")
    private String processInstanceId;

    /**
     * 用户名称
     */
    @NotBlank(message = "用户名称不允许为空")
    private String username;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package vulgar.cd.camunda.domain.input;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class ProcessStartParam implements Serializable {
    private static final long serialVersionUID = 853920625348598345L;

    /**
     * 流程定义文件id
     */
    @NotBlank(message = "流程定义文件id不允许为空")
    private String processDefinitionId;

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }
}

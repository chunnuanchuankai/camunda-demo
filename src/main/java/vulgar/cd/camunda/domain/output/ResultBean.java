package vulgar.cd.camunda.domain.output;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public class ResultBean<T> implements Serializable {
    private static final long serialVersionUID = 6956006431997532529L;

    private int code;

    private String message;

    private T content;

    public ResultBean() {
        this(HttpServletResponse.SC_OK, "SUCCESS");
    }

    public ResultBean(int code, String message) {
        this(code, message, null);
    }

    public ResultBean(int code, String message, T content) {
        this.code = code;
        this.message = message;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}

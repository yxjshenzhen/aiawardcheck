package cn.com.xiaofabo.scia.aiawardcheck.common;

/**
 * code 枚举
 */
public enum ResultCodeEnum {

    //SUCCESS(200, "转换成功"),
    FILE_INPUT_NOT_FOUND(404, "未上传文件"),
    FILE_OUTPUT_NOT_FOUND(405, "生成文件失败"),
    FILE_IS_EMPTY(406, "上传文件内容为空"),
    SYSTEM_READ_FAIL(407, "系统读取文件失败"),
    SYSTEM_DEAL_FAIL(500, "系统处理失败"),
    SYSTEM_INPUT_IO_EXCEPTION(501, "系统写入IO异常"),
    SYSTEM_OUTPUT_IO_EXCEPTION(502, "系统读取IO异常"),
    SYSTEM_CREATE_FILE_EXCEPTION(503, "系统创建临时文件失败");

    private int resultCode;
    private String resultMsg;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    ResultCodeEnum(int resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }
}

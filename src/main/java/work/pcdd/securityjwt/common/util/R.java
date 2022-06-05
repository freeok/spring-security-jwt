package work.pcdd.securityjwt.common.util;

import lombok.Data;

/**
 * @author pcdd
 * @date 2021/3/24
 */
@Data
public class R {

    private Integer code;
    private String msg;
    private Object data;

    public static R ok() {
        return R.ok("success", null);
    }

    public static R ok(Object data) {
        return R.ok("success", data);
    }

    public static R ok(String msg, Object data) {
        R r = new R();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static R fail(String msg) {
        return R.fail(-1, msg);
    }

    public static R fail(Integer code, String msg) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

}

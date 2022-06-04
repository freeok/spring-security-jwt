package work.pcdd.securityjwt.model.vo;


import lombok.Data;

/**
 * @author 1907263405@qq.com
 * @date 2021/3/24 3:34
 */
@Data
public class Result {


    private Integer code;

    private String msg;

    private Object data;


    public static Result success() {
        return Result.success("成功", null);
    }

    public static Result success(Object data) {
        return Result.success("成功", data);
    }

    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static Result fail(String msg) {
        return Result.fail(null, msg);
    }

    public static Result fail(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

}

package com.mmorpg.qx.module.condition;

/**
 * 验证结果
 *
 * @author wang ke
 * @since v1.0 2018年2月25日
 */
public class Result {
    /**
     * 每一个result都具有唯一性.不可变更,code 为0表示成功
     */
    private final int code;


    public static final Result SUCCESS = Result.valueOf(0);

    public static final Result FAILURE = Result.valueOf(1);

    private Result(int code) {
        this.code = code;
    }

    public static Result valueOf(int code) {

        return new Result(code);
    }

    public boolean isSuccess() {
        return code == SUCCESS.code;
    }

    public boolean isFailure() {
        return code == FAILURE.code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Result other = (Result) obj;
        if (code != other.code)
            return false;
        return true;
    }

}

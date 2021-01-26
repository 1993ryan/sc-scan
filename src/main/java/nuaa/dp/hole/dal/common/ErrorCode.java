package nuaa.dp.hole.dal.common;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {
    /**
     * 默认的请求成功错误码
     */
    SUCCESS(0, "success"),
    /**
     * 默认的请求失败错误码
     */
    FAIL(1, "fail"),
    OBJECT_ALREADY_EXISTS(2, "对象已存在"),
    TOKEN_EXPIRED(3, "TOKEN过期"),
    TOKEN_ERROR(4, "TOKEN信息错误"),
    STATUS_CHANGED(5, "状态已变更"),
    STATUS_ERROR(6, "状态错误"),
    PERMISSION_DENIED(7, "权限拒绝"),
    OBJECT_NOT_EXISTS(8, "对象不存在"),
    INTERNAL_ERROR(9, "内部错误"),

    OBJECT_INVALID(1002, "对象已失效"),
    AGE_FROZEN(1003, "年龄超过50周岁"),
    SPU_ID_IS_EMPTY(1009, "商品ID为空"),
    USER_INFO_EMPTY(1010, "用户信息为空"),
    PARAM_IS_NULL(1011, "参数为空"),
    PAY_PASSWORD_ERROR(1013, "支付密码错误"),
    ;

    public Integer code;
    public String desc;

}
package com.tielu.common.base.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "成功"),
    SERVER_ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),

    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    PHONE_ALREADY_REGISTERED(1004, "手机号已注册"),
    VERIFY_CODE_ERROR(1005, "验证码错误"),
    VERIFY_CODE_EXPIRED(1006, "验证码已过期"),

    TRAIN_NOT_EXIST(2001, "车次不存在"),
    TRAIN_NOT_BOOKABLE(2002, "该车次不可预订"),
    STATION_NOT_EXIST(2003, "车站不存在"),

    TICKET_NOT_ENOUGH(3001, "余票不足"),
    SEAT_LOCK_FAILED(3002, "座位锁定失败"),
    SEAT_ALREADY_SOLD(3003, "座位已售"),

    ORDER_NOT_EXIST(4001, "订单不存在"),
    ORDER_CANNOT_CANCEL(4002, "订单不可取消"),
    ORDER_ALREADY_PAID(4003, "订单已支付"),
    ORDER_ALREADY_CANCELLED(4004, "订单已取消"),
    ORDER_STATUS_ERROR(4005, "订单状态错误"),

    PAY_FAILED(5001, "支付失败"),
    PAY_TIMEOUT(5002, "支付超时"),
    PAY_SIGN_ERROR(5003, "支付签名验证失败"),
    REFUND_FAILED(5004, "退款失败"),
    PAYMENT_RECORD_NOT_EXIST(5005, "支付记录不存在"),

    PASSENGER_NOT_EXIST(6001, "乘车人不存在"),
    PASSENGER_LIMIT_REACHED(6002, "乘车人数量已达上限"),
    ID_CARD_FORMAT_ERROR(6003, "身份证号格式错误");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

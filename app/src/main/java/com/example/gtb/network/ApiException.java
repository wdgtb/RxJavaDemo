package com.example.gtb.network;

public class ApiException extends RuntimeException {

    public static final int USER_NOT_EXIST = 100;

    public ApiException(int resultCode) {
        this(getApiExceptionMessage(resultCode));
    }

    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     */
    private static String getApiExceptionMessage(int code) {
        String message;
        switch (code) {
            case USER_NOT_EXIST:
                message = "XX错误";
                break;
            default:
                message = "未知错误";
        }
        return message;
    }
}


package com.enation.javashop.net.engine.utils;

/**
 * ErrorBody
 */

public class ErrorBody {


    /**
     * error_code : IllegalArgumentException
     * error_message : For input string: "asd"
     */

    private String error_code;
    private String error_message;

    public ErrorBody(String error_message) {
        this.error_message = error_message;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }
}

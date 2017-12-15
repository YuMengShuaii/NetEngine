package com.enation.javashop.net.engine.plugin.exception;

import com.enation.javashop.net.engine.utils.ErrorBody;

import java.io.IOException;

/**
 * Created by LDD on 2017/12/14.
 */

public class RestfulException extends IOException {
    private ErrorBody errorBody;

    public RestfulException(String message) {
        super(message);
    }



    public ErrorBody getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(ErrorBody errorBody) {
        this.errorBody = errorBody;
    }
}

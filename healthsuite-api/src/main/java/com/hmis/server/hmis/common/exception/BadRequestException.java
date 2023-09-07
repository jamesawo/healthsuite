package com.hmis.server.hmis.common.exception;

public class BadRequestException extends BaseCommonException {

    public BadRequestException(String code, String description){
        this.code = code;
        this.description = description;

    }

}

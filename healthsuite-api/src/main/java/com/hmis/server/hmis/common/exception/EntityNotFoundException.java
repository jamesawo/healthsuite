package com.hmis.server.hmis.common.exception;

public class EntityNotFoundException extends BaseCommonException {

    public EntityNotFoundException(String code, String description) {
        this.code = code;
        this.description = description;
    }

}

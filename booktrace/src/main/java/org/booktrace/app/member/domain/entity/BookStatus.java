package org.booktrace.app.member.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum BookStatus {
    DONE, READING, WISH;

    @JsonCreator
    public static BookStatus from(String value){
        return BookStatus.valueOf(value.toUpperCase());
    }
}
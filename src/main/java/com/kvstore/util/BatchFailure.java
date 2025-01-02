package com.kvstore.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchFailure{

    private String key;
    private String reason;

    public BatchFailure(String key, String reason){
        this.key = key;
        this.reason = reason;
    }
}

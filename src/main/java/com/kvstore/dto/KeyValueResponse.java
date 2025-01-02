package com.kvstore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValueResponse {
    private String key;
    private Object data;

    public KeyValueResponse(String key, Object data){
        this.key = key;
        this.data = data;
    }
}

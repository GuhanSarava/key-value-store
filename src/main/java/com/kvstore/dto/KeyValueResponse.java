package com.kvstore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValueResponse {
    private String key;
    private ObjectData data;

    public KeyValueResponse(String key, ObjectData data){
        this.key = key;
        this.data = data;
    }
}

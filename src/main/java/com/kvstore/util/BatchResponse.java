package com.kvstore.util;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BatchResponse {
    private List<String> successes = new ArrayList<>();
    private List<BatchFailure> failures = new ArrayList<>();

    public void addSuccess(String key){
        successes.add(key);
    }

    public void addFailure(String key, String reason){
        failures.add(new BatchFailure(key, reason));
    }

}

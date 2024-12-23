package com.kvstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValueRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 32, message = "Username must not exceed 32 characters")
    private String key;

    @Valid
    private ObjectData data;
    private Long ttl;

}

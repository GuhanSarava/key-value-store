package com.kvstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ObjectData {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Email(message = "Email is not in a valid format")
    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;

}

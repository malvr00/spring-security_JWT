package com.salt.hed_admin.feature.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto implements Serializable {

    @NotNull(message = "userId cannot be null")
    @Size(min = 4, max = 12, message = "Must be at least 4 characters and not more than 12 characters.")
    private String userId;

    @NotNull(message = "password cannot be null")
    @Size(min = 10, max = 16, message = "Must be at least 10 characters and not more than 16 characters.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
            message = "At least 1 'number', 'letter', or 'special character', and " +
                    "'minimum 10 to maximum 16' postic characters are allowed." +
                    " (Special characters can only be used as defined characters)")
    private String password;

}

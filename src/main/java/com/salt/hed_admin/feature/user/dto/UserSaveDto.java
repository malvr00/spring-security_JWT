package com.salt.hed_admin.feature.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSaveDto implements Serializable {

    @NotNull(message = "userId cannot be null")
    @Size(min = 4, max = 12, message = "Must be at least 4 characters and not more than 12 characters.")
    private String userId;

    @NotNull(message = "permissionId cannot be null")
    private Long permissionId;

    @NotNull(message = "password cannot be null")
    @Size(min = 10, max = 16, message = "Must be at least 10 characters and not more than 16 characters.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
            message = "At least 1 'number', 'letter', or 'special character', and " +
                    "'minimum 10 to maximum 16' postic characters are allowed." +
                    " (Special characters can only be used as defined characters)")
    private String password;

    @NotNull(message = "password2 cannot be null")
    @Size(min = 10, max = 16, message = "Must be at least 10 characters and not more than 16 characters.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
            message = "At least 1 'number', 'letter', or 'special character', and " +
                    "'minimum 10 to maximum 16' postic characters are allowed." +
                    " (Special characters can only be used as defined characters)")
    private String password2;

    @NotNull(message = "name cannot be null")
    @Size(min = 2, max = 50, message = "Must be at least 2 characters and not more than 50 characters.")
    private String name;

    private String phone;
}

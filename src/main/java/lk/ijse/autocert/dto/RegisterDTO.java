package lk.ijse.autocert.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String email;
    private String password;
    private String phone;
    private String role;
}

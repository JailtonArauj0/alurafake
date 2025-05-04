package br.com.alura.AluraFake.dtos.request;

import br.com.alura.AluraFake.domain.model.user.Role;
import br.com.alura.AluraFake.domain.model.user.User;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public class NewUserDTO {

    @NotNull
    @Length(min = 3, max = 50, message = "Name should be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email
    private String email;

    @NotNull
    private Role role;

    @NotNull
    @Length(min = 6, max = 20, message = "Password should be between 6 and 20 characters")
    private String password;

    public NewUserDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public User toModel() {
        return new User(name, email, role, password);
    }

}

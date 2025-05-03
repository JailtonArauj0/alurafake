package br.com.alura.AluraFake.dtos.request;

import br.com.alura.AluraFake.domain.model.user.Role;
import br.com.alura.AluraFake.domain.model.user.User;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public class NewUserDTO {

    @NotNull
    @Length(min = 3, max = 50, message = "O campo nome deve ter entre 3 e 50 caracteres")
    private String name;

    @NotBlank(message = "O campo email não pode ser vazio")
    @Email
    private String email;

    @NotNull
    private Role role;

    @NotNull
    @Length(min = 6, max = 20, message = "A senha deve ter entre 6 e 20 caracteres")
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

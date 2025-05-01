package br.com.alura.AluraFake.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class LoginRequestDTO {
    @NotBlank(message = "O Email não deve ser nulo")
    private String email;
    @NotBlank(message = "A senha não deve ser nula")
    private String password;

    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequestDTO that = (LoginRequestDTO) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }
}

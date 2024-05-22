package es.asun.StoryCrafters.model;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto
{
    private int id;
    @NotEmpty
    private String firstName;

    private String lastName;
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    @NotEmpty(message = "Password should not be empty")
    private String password;

    private String firmaAutor;

    private boolean activo;

    private Avatar avatar;
}

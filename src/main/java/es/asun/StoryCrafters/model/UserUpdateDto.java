package es.asun.StoryCrafters.model;

import es.asun.StoryCrafters.entity.Avatar;
import es.asun.StoryCrafters.entity.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @NotEmpty
    @NotBlank
    @Size(max = 60)
    @Pattern(regexp = "^[a-zA-ZáéíóúüÁÉÍÓÚÜñÑ\\s]+$")
    private String firstName;

    @Size(max = 60)
    @Pattern(regexp = "^[a-zA-ZáéíóúüÁÉÍÓÚÜñÑ\\s]+$")
    private String lastName;

    @Size(max = 60)
    @Pattern(regexp = "^[a-zA-ZáéíóúüÁÉÍÓÚÜñÑ\\s]+$")
    private String firmaAutor;

    private Avatar avatar;

    public UserUpdateDto(Usuario usuario) {
        firstName = usuario.getFirstName();
        lastName = usuario.getLastName();
        firmaAutor = usuario.getFirmaAutor();
        avatar = usuario.getAvatar();
    }
}

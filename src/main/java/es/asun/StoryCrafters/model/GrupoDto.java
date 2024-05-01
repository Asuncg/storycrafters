package es.asun.StoryCrafters.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoDto {

    private int idGrupo;
    private int idUsuario;
    @Size(max = 50)
    private String nombre;
    @Size(max = 500)
    private String Descripcion;
}

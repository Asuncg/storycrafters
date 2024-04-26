package es.asun.StoryCrafters.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatoDto {
    private int id;
    private int idUsuario;
    @Size(max = 60)
    private String titulo;
    @Size(max = 4000)
    private String texto;
    private List<Integer> categorias;
    @Size(max = 60)
    private String firmaAutor;
    private int idImagen;
    private Date fechaActualizacion;
}

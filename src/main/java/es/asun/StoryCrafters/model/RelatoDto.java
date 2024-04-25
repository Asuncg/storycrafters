package es.asun.StoryCrafters.model;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Imagen;
import es.asun.StoryCrafters.entity.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}

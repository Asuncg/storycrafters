package es.asun.StoryCrafters.model;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Imagen;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RelatoPreviewDto {

    private int id;
    private String titulo;
    private Imagen imagen;
    private List<Categoria> categorias;
    private Date fechaActualizacion;
}

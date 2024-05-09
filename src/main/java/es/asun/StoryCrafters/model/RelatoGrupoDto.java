package es.asun.StoryCrafters.model;

import es.asun.StoryCrafters.entity.Categoria;
import es.asun.StoryCrafters.entity.Imagen;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
public class RelatoGrupoDto {
    private int id;
    private String titulo;
    private String texto;
    private Imagen imagen;
    private List<Categoria> categorias;
    private String firmaAutor;
    private Date fechaPublicacion;

}

package es.asun.StoryCrafters.utilidades;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.model.RelatoDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Mappings {
    public static Relato mapToRelato(RelatoDto relatoDto, Imagen imagen) {
        // Mapear los datos del DTO al objeto Relato

        Relato relato = new Relato();
        relato.setId(relatoDto.getId());
        relato.setTitulo(relatoDto.getTitulo());
        relato.setTexto(relatoDto.getTexto());
        relato.setFirmaAutor(relatoDto.getFirmaAutor());
        relato.setImagen(imagen);
        relato.setFechaActualizacion(new Date());

        return relato;
    }

    public static Grupo mapToGrupo(GrupoDto grupoDto) {
        Grupo grupo = new Grupo();
        grupo.setNombre(grupoDto.getNombre());
        grupo.setDescripcion(grupoDto.getDescripcion());
        return grupo;
    }

}

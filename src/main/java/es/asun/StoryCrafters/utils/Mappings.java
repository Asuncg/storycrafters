package es.asun.StoryCrafters.utils;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoGrupoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;

import java.util.Date;

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

    public static RelatoGrupoDto mapToRelatoGrupoDto(RelatoGrupo relatoGrupo) {
        RelatoGrupoDto relatoGrupoDto = new RelatoGrupoDto();
        relatoGrupoDto.setId(relatoGrupo.getId());
        relatoGrupoDto.setTitulo(relatoGrupo.getTitulo());
        relatoGrupoDto.setTexto(relatoGrupo.getTexto());
        relatoGrupoDto.setImagen(relatoGrupo.getImagen());
        relatoGrupoDto.setCategorias(relatoGrupo.getCategorias());
        relatoGrupoDto.setFirmaAutor(relatoGrupo.getFirmaAutor());
        relatoGrupoDto.setFechaPublicacion(relatoGrupo.getFechaPublicacion());
        relatoGrupoDto.setCalificacion(relatoGrupo.getCalificacion());
        relatoGrupoDto.setFeedback(relatoGrupo.getFeedback());
        relatoGrupoDto.setUsuario(relatoGrupo.getRelato().getUsuario());

        return relatoGrupoDto;
    }

    public static RelatoPreviewDto mapToRelatoVistaDto (Relato relato) {
        RelatoPreviewDto relatoPreviewDto = new RelatoPreviewDto();
        relatoPreviewDto.setId(relato.getId());
        relatoPreviewDto.setTitulo(relato.getTitulo());
        relatoPreviewDto.setImagen(relato.getImagen());
        relatoPreviewDto.setCategorias(relato.getCategorias());
        relatoPreviewDto.setFechaActualizacion(relato.getFechaActualizacion());

        return relatoPreviewDto;
    }
}

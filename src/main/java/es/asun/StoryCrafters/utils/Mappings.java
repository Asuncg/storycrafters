package es.asun.StoryCrafters.utils;

import es.asun.StoryCrafters.entity.*;
import es.asun.StoryCrafters.model.GrupoDto;
import es.asun.StoryCrafters.model.RelatoDto;
import es.asun.StoryCrafters.model.RelatoGrupoDto;
import es.asun.StoryCrafters.model.RelatoPreviewDto;

import java.util.Date;

/**
 * Clase de utilidad para mapear entre diferentes modelos y entidades.
 */
public class Mappings {

    /**
     * Mapea un DTO de relato a una entidad de relato.
     *
     * @param relatoDto El DTO de relato.
     * @param imagen La imagen asociada al relato.
     * @return La entidad de relato mapeada.
     */
    public static Relato mapToRelato(RelatoDto relatoDto, Imagen imagen) {
        Relato relato = new Relato();
        relato.setId(relatoDto.getId());
        relato.setTitulo(relatoDto.getTitulo());
        relato.setTexto(relatoDto.getTexto());
        relato.setImagen(imagen);
        relato.setFechaActualizacion(new Date());

        return relato;
    }

    /**
     * Mapea un DTO de grupo a una entidad de grupo.
     *
     * @param grupoDto El DTO de grupo.
     * @return La entidad de grupo mapeada.
     */
    public static Grupo mapToGrupo(GrupoDto grupoDto) {
        Grupo grupo = new Grupo();
        grupo.setNombre(grupoDto.getNombre());
        grupo.setDescripcion(grupoDto.getDescripcion());
        return grupo;
    }

    /**
     * Mapea una relación entre relato y grupo a un DTO de relato en grupo.
     *
     * @param relatoGrupo La relación entre relato y grupo.
     * @return El DTO de relato en grupo mapeado.
     */
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

    /**
     * Mapea un relato a un DTO de vista previa de relato.
     *
     * @param relato El relato.
     * @return El DTO de vista previa de relato mapeado.
     */
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

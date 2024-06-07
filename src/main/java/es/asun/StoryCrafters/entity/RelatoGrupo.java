package es.asun.StoryCrafters.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import static es.asun.StoryCrafters.utils.Constantes.ESTADO_APROBADO;

/**
 * Representa la relación entre un relato y un grupo en la aplicación.
 * Un relato puede estar asociado a varios grupos.
 */
@Getter
@Setter
@Entity
public class RelatoGrupo {

    /**
     * Identificador único de la relación relato-grupo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Relato asociado a la relación.
     */
    @ManyToOne
    @JoinColumn(name = "id_relato")
    private Relato relato;

    /**
     * Grupo asociado a la relación.
     */
    @ManyToOne
    @JoinColumn(name = "id_grupo", nullable = false)
    private Grupo grupo;

    /**
     * Título del relato dentro del grupo.
     */
    @Column(nullable = false, name = "titulo", length = 150)
    private String titulo;

    /**
     * Texto del relato dentro del grupo.
     */
    @Column(nullable = false, name = "texto", length = 10000)
    private String texto;

    /**
     * Imagen asociada al relato dentro del grupo.
     */
    @ManyToOne
    @JoinColumn(name = "id_imagen")
    private Imagen imagen;

    /**
     * Categorías asociadas al relato dentro del grupo.
     */
    @ManyToMany
    @JoinTable(
            name = "relatogrupo_categoria",
            joinColumns = @JoinColumn(name = "relato_grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;

    /**
     * Firma del autor del relato dentro del grupo.
     */
    @Column(nullable = false, name = "firmaAutor", length = 60)
    private String firmaAutor;

    /**
     * Estado del relato dentro del grupo.
     * Por defecto, el estado es 1 (aprobado).
     */
    @Column(name = "estado")
    private int estado = 1;

    /**
     * Feedback asociado al relato dentro del grupo.
     */
    @Column(name = "feedback", length = 500)
    private String feedback;

    /**
     * Calificación del relato dentro del grupo.
     */
    @Column(name = "calificacion")
    private double calificacion;

    /**
     * Fecha de publicación del relato dentro del grupo.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPublicacion;

    /**
     * Fecha de última modificación del relato dentro del grupo.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    /**
     * Método que se ejecuta antes de actualizar la entidad.
     * Genera automáticamente la fecha de publicación si el estado es aprobado y la fecha de publicación es nula.
     */
    @PreUpdate
    private void preUpdate() {
        if (this.estado == ESTADO_APROBADO && this.fechaPublicacion == null) {
            this.fechaPublicacion = new Date();
        }
    }
}

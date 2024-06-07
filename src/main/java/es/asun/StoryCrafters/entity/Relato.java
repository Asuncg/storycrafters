package es.asun.StoryCrafters.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Representa un relato en la aplicación.
 * Un relato es creado por un usuario y puede pertenecer a varias categorías.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Relatos")
public class Relato {

    /**
     * Identificador único del relato.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Usuario que creó el relato.
     */
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /**
     * Título del relato.
     */
    @Column(name = "titulo", length = 150)
    private String titulo;

    /**
     * Texto del relato.
     */
    @Column(name = "texto", length = 4500)
    private String texto;

    /**
     * Imagen asociada al relato.
     */
    @ManyToOne
    @JoinColumn(name = "id_imagen")
    private Imagen imagen;

    /**
     * Lista de categorías a las que pertenece el relato.
     */
    @ManyToMany
    @JoinTable(
            name = "categoria_relato",
            joinColumns = @JoinColumn(name = "relato_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;

    /**
     * Firma del autor del relato.
     */
    @Column(name = "firmaAutor", length = 60)
    private String firmaAutor;

    /**
     * Fecha de creación del relato.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    /**
     * Fecha de última actualización del relato.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_actualizacion")
    private Date fechaActualizacion;

    /**
     * Indica si el relato está archivado.
     */
    @Column(name = "archivado")
    private boolean archivado = false;
}

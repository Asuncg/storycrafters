package es.asun.StoryCrafters.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Representa un grupo en la aplicación.
 * Un grupo es creado por un usuario y puede tener varios usuarios miembros.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Grupos")
public class Grupo {

    /**
     * Identificador único del grupo.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Usuario que creó el grupo.
     */
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /**
     * Nombre del grupo.
     */
    @Column(nullable = false, name = "titulo", length = 50)
    private String nombre;

    /**
     * Descripción del grupo.
     */
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    /**
     * Lista de usuarios miembros del grupo.
     */
    @ManyToMany
    @JoinTable(
            name = "usuario_grupo",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> usuarios;

    /**
     * Código de acceso único para unirse al grupo.
     */
    @Column(nullable = false, unique = true, length = 15)
    private String codigoAcceso;

    /**
     * Lista de relaciones entre relatos y grupos.
     */
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelatoGrupo> relatoGrupos;

    /**
     * Lista de solicitudes de ingreso al grupo.
     */
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitud> solicitudesIngresoGrupos;
}

package es.asun.StoryCrafters.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Representa un usuario en la aplicación.
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Usuarios")
public class Usuario {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    /**
     * Nombre del usuario.
     */
    @Column(nullable = false, name = "nombre", length = 60)
    private String firstName;

    /**
     * Apellidos del usuario.
     */
    @Column(name = "apellidos", length = 60)
    private String lastName;

    /**
     * Firma del autor utilizada en sus relatos.
     */
    @Column(name = "firma", length = 60)
    private String firmaAutor;

    /**
     * Dirección de correo electrónico del usuario.
     */
    @Column(nullable = false, name = "email", unique = true, length = 50)
    private String email;

    /**
     * Contraseña del usuario.
     */
    @Column(nullable = false, length = 200)
    private String password;

    /**
     * Token utilizado para restablecer la contraseña.
     */
    @Column(name= "resetToken", length = 100)
    private String resetToken;

    /**
     * Estado de activación de la cuenta del usuario.
     */
    @Column(name = "activo", length = 1, columnDefinition = "int default 1")
    private boolean activo = true;

    /**
     * Grupos a los que pertenece el usuario.
     */
    @ManyToMany
    @JoinTable(
            name = "usuario_grupo",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private List<Grupo> grupos;

    /**
     * Avatar del usuario.
     */
    @ManyToOne
    @JoinColumn(name = "id_avatar")
    private Avatar avatar;

}

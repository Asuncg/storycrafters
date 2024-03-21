package es.asun.StoryCrafters.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id_usuario;

    @Column(nullable = false)
    private String nombre;
    private String apellidos;

    @Column(nullable = false, name = "email")
    private String email;

    @Column(nullable = false)
    private String password;

    public User(String nombre, String password) {
    }
}

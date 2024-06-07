package es.asun.StoryCrafters.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un avatar en la aplicación.
 * Un avatar tiene una URL que apunta a su imagen.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "avatars")
public class Avatar {

    /**
     * Identificador único del avatar.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * URL de la imagen del avatar.
     */
    @Column(name = "url", length = 200)
    private String url;
}

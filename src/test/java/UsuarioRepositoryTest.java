import static org.assertj.core.api.Assertions.assertThat;

import es.asun.StoryCrafters.entity.Usuario;
import es.asun.StoryCrafters.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repo;

    @Test
    public void testCreateUser() {
        Usuario usuario = new Usuario();
        usuario.setEmail("ravikumar@gmail.com");
        usuario.setPassword("ravi2020");
        usuario.setFirstName("Ravi");
        usuario.setLastName("Kumar");

        Usuario savedUsuario = repo.save(usuario);

        Usuario existUsuario = entityManager.find(Usuario.class, savedUsuario.getId());

        assertThat(usuario.getEmail()).isEqualTo(existUsuario.getEmail());

    }
}

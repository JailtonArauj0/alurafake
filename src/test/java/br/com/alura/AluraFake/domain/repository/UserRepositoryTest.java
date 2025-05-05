package br.com.alura.AluraFake.domain.repository;

import br.com.alura.AluraFake.domain.model.user.Role;
import br.com.alura.AluraFake.domain.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByEmail__should_return_existis_user() {
        User caio = new User("Caio", "caio1@alura.com.br", Role.STUDENT);
        entityManager.persist(caio);
        entityManager.flush();

        Optional<User> result = userRepository.findByEmail("caio1@alura.com.br");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Caio");

        result = userRepository.findByEmail("sergio@alura.com.br");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail__should_return_true_when_user_existis() {
        User caio = new User("Caio", "caio2@alura.com.br", Role.STUDENT);
        entityManager.persist(caio);
        entityManager.flush();

        assertThat(userRepository.existsByEmail("caio2@alura.com.br")).isTrue();
        assertThat(userRepository.existsByEmail("sergio@alura.com.br")).isFalse();
    }
}
package br.com.alura.AluraFake.infra;

import br.com.alura.AluraFake.domain.model.course.Course;
import br.com.alura.AluraFake.domain.model.user.Role;
import br.com.alura.AluraFake.domain.model.user.User;
import br.com.alura.AluraFake.domain.repository.CourseRepository;
import br.com.alura.AluraFake.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (!"dev".equals(activeProfile)) return;
        // REMOVI A GERAÇÃO AUTOMATICA DE SENHA PARA FACILITAR O LOGIN, DEVIDO AO HASH DA SENHA NO BANCO
        if (userRepository.count() == 0) {
            User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT, new BCryptPasswordEncoder().encode("123456"));
            User paulo = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR, new BCryptPasswordEncoder().encode("123456"));
            userRepository.saveAll(Arrays.asList(caio, paulo));
            courseRepository.save(new Course("Java", "Aprenda Java com Alura", paulo));
        }
    }
}
package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static io.turntabl.ttbay.enums.OfficeLocation.SONNIDOM_HOUSE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest{
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach(){
        userRepository.save(User.builder().email("emma@gmail.com").username("Mick").fullName("Michael Jackson").profileUrl("testingImage.com/image.png").role(Role.USER).officeLocation(SONNIDOM_HOUSE).build());
    }
    @AfterEach
    void tearDown(){
        userRepository.deleteById("emma@gmail.com");
    }

    @Test
    void saveUser_shouldSavedUserWithGivenEmail(){
        String email = "emma@gmail.com";
        boolean expected = userRepository.findByEmail(email).isPresent();
        assertThat(expected).isTrue();
    }

    @Test
    void saveUser_shouldSaveUserWithGivenRole(){
        Role expectedRole = Role.USER;
        User user = userRepository.findByEmail("emma@gmail.com").orElseThrow();
        Assertions.assertEquals(user.getRole(), expectedRole);
    }
}
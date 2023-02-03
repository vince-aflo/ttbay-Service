package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.model.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach(){
        userRepository.save(new User(1L,
                "Tkayy",
                "Emmanuel Tweneboah",
                "emma@gmail.com",
                "picture",
                Role.USER,
                "AP"));
    }
    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }


    @Test
    void testToCheckThatUserSavedWIthAParticularEmailIsPresent() {
        //given
        String email = "emma@gmail.com";

        //when
        boolean expected = userRepository.findByEmail(email).isPresent();
        //then
        assertThat(expected).isTrue();
    }

    @Test
    void testThatVerifiesThatASavedUserHasAUserRole(){
        //given
        Role expectedRole = Role.USER;
        //when
        User user = userRepository.findByEmail("emma@gmail.com").orElseThrow();
        //then
        Assertions.assertEquals(user.getRole(), expectedRole);
    }

}
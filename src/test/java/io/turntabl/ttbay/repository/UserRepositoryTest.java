package io.turntabl.ttbay.repository;

import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.model.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
    }


    @Test
    void testToCheckThatUserSavedWIthAParticularEmailIsPresent() {

        String email = "emma@gmail.com";
        //given
        userRepository.save(new User(1L,"Tkayy","Emmanuel Tweneboah","emma@gmail.com","picture", Role.USER,"AP"));

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
        User user = new User(1L,"Tkayy","Emmanuel Tweneboah","emma@gmail.com","picture", Role.USER,"AP");
        //then
        Assertions.assertEquals(user.getRole(), expectedRole);
    }

    @Test
    void TestToFindAParticularUserWithEmailInTheDB(){
        User user = new User(1L,"Tkayy","Emmanuel Tweneboah","emma@gmail.com","picture", Role.USER,"AP");
        userRepository.save(user);
        Assertions.assertNotNull(userRepository.findByEmail("emma@gmail.com"));
    }

}
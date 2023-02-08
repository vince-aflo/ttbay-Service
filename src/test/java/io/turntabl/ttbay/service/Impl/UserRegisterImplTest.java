package io.turntabl.ttbay.service.Impl;

import  io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;




@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRegisterImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserAuthImpl serviceUnderTest;


    @Test
    void testThatUSerIsRetrievedFromTHeUsingHisEmail() {

        User savedUser = userRepository
                .save(new User(
                        "emma@gmail.com",
                        "tkayy",
                        "Michael Jackson",
                        "testingImage.com/image.png",
                        Role.USER,
                        OfficeLocation.SONNIDOM_HOUSE,
                        List.of()
                ));

        User retrievedUserByEmail = serviceUnderTest.findByEmail("emma@gmail.com");

        Assertions.assertEquals(retrievedUserByEmail, savedUser);
    }

    @Test
    void findByEmailShouldReturnNUllWhenEmailIsNotFoundInDb() {
        var retrievedUserByEmail = serviceUnderTest.findByEmail("albert@gmail.com");

        Assertions.assertNull(retrievedUserByEmail);
    }

    //TODO find a way to test jwt web tokens. and check the claims
}
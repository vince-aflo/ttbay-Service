package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.model.enums.Role;
import io.turntabl.ttbay.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRegisterImplTest {

    @Autowired
    private UserRepository userRepository;
    private UserRegisterImpl serviceUnderTest;


    @Test
    void register() {
    }

    @Test
    void findByEmail() {
    }
}
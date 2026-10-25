package com.safeops.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByFirebaseUid() {
        User user = new User("firebase-uid-test-001", "test@example.com");
        user.setDisplayName("Test User");
        userRepository.save(user);

        Optional<User> found = userRepository.findByFirebaseUid("firebase-uid-test-001");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getDisplayName()).isEqualTo("Test User");
    }

    @Test
    void shouldReturnEmptyForUnknownFirebaseUid() {
        Optional<User> found = userRepository.findByFirebaseUid("nonexistent-uid");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckExistenceByFirebaseUid() {
        User user = new User("firebase-uid-exists", "exists@example.com");
        userRepository.save(user);

        assertThat(userRepository.existsByFirebaseUid("firebase-uid-exists")).isTrue();
        assertThat(userRepository.existsByFirebaseUid("does-not-exist")).isFalse();
    }
}

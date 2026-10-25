package com.safeops.user;

import com.safeops.common.exception.ResourceNotFoundException;
import com.safeops.user.dto.UserDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserDto> findByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid).map(this::toDto);
    }

    public UserDto findById(UUID id) {
        return userRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public UserDto findOrCreateByFirebaseToken(String firebaseUid, String email, String displayName, boolean emailVerified) {
        Optional<User> existingOpt = userRepository.findByFirebaseUid(firebaseUid);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            existing.setEmail(email);
            existing.setDisplayName(displayName);
            existing.setEmailVerified(emailVerified);
            return toDto(userRepository.save(existing));
        }

        User newUser = new User(firebaseUid, email);
        newUser.setDisplayName(displayName);
        newUser.setEmailVerified(emailVerified);
        return toDto(userRepository.save(newUser));
    }

    private UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getFirebaseUid(), u.getEmail(), u.getDisplayName(), u.getPhotoUrl(), u.isActive(), u.isEmailVerified(), u.getCustomer() != null ? u.getCustomer().getId() : null, u.getCreatedAt(), u.getUpdatedAt());
    }
}

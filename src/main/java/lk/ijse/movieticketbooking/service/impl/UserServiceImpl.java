package lk.ijse.movieticketbooking.service.impl;

import lk.ijse.movieticketbooking.dto.UserDTO;
import lk.ijse.movieticketbooking.entity.Role;
import lk.ijse.movieticketbooking.entity.User;
import lk.ijse.movieticketbooking.exception.BadRequestException;
import lk.ijse.movieticketbooking.exception.ResourceNotFoundException;
import lk.ijse.movieticketbooking.repository.UserRepository;
import lk.ijse.movieticketbooking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> result = new ArrayList<>();

        for (User user : users) {
            result.add(toDTO(user));
        }

        return result;
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return toDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Update user request received for user id {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (userDTO.getFullName() != null) {
            user.setFullName(userDTO.getFullName());
        }

        if (userDTO.getEmail() != null) {
            Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                log.warn("User update failed: email is already used by another user (user id {})", id);
                throw new BadRequestException("Email is already used by another user");
            }
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getRole() != null) {
            try {
                user.setRole(Role.valueOf(userDTO.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("User update failed: invalid role {} for user id {}", userDTO.getRole(), id);
                throw new BadRequestException("Invalid role. Use ADMIN or CUSTOMER");
            }
        }

        User savedUser = userRepository.save(user);
        log.info("User updated with id {}", id);

        return toDTO(savedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Delete user request received for user id {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getBookings() != null && !user.getBookings().isEmpty()) {
            log.warn("User delete failed: user id {} has bookings", id);
            throw new BadRequestException("User has bookings, cannot delete this user");
        }

        userRepository.delete(user);
        log.info("User deleted with id {}", id);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        return dto;
    }
}
package lk.ijse.movieticketbooking.service;

import lk.ijse.movieticketbooking.dto.UserDTO;
import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);
}
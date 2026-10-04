package lk.ijse.movieticketbooking.service;

import lk.ijse.movieticketbooking.dto.AuthResponse;
import lk.ijse.movieticketbooking.dto.SigninRequest;
import lk.ijse.movieticketbooking.dto.SignupRequest;

public interface AuthService {

    AuthResponse signup(SignupRequest request);

    AuthResponse signin(SigninRequest request);
}
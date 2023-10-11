package alpha.payeasebe.services.user;

import org.springframework.http.ResponseEntity;

import alpha.payeasebe.payloads.req.CreatePINRequest;
import alpha.payeasebe.payloads.req.FindUserEmail;
import alpha.payeasebe.payloads.req.LoginRequest;
import alpha.payeasebe.payloads.req.RegisterRequest;
import alpha.payeasebe.payloads.req.ResetPasswordRequest;

public interface UserServices {
    ResponseEntity<?> registerService(RegisterRequest request);
    ResponseEntity<?> loginService(LoginRequest request);
    ResponseEntity<?> getUsersService();
    ResponseEntity<?> createUserPINService(CreatePINRequest request);
    // ResponseEntity<?> resetPasswordService(ResetPasswordRequest request);
    ResponseEntity<?> getUserByIdService(String id);
    ResponseEntity<?> findUserByEmail(FindUserEmail request);
    ResponseEntity<?> resetPasswordService(String token, ResetPasswordRequest request);
}

package alpha.payeasebe.services.user;

import org.springframework.http.ResponseEntity;

import alpha.payeasebe.payloads.req.ChangePINRequest;
import alpha.payeasebe.payloads.req.ChangePasswordRequest;
import alpha.payeasebe.payloads.req.CreatePINRequest;
import alpha.payeasebe.payloads.req.LoginRequest;
import alpha.payeasebe.payloads.req.RegisterRequest;
import alpha.payeasebe.payloads.req.ResetPasswordRequest;

public interface UserServices {
    ResponseEntity<?> registerService(RegisterRequest request);
    ResponseEntity<?> loginService(LoginRequest request);
    ResponseEntity<?> getUsersService();
    ResponseEntity<?> createUserPINService(CreatePINRequest request);
    ResponseEntity<?> changeUserPINService(ChangePINRequest request);
    ResponseEntity<?> changeUserPasswordService(ChangePasswordRequest request);
    ResponseEntity<?> resetPasswordService(ResetPasswordRequest request);
    ResponseEntity<?> getUserByIdService(String id);
}

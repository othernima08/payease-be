package alpha.payeasebe.services.user;

import org.springframework.http.ResponseEntity;

import alpha.payeasebe.payloads.req.User.ChangePINRequest;
import alpha.payeasebe.payloads.req.User.ChangePasswordRequest;
import alpha.payeasebe.payloads.req.User.CreatePINRequest;
import alpha.payeasebe.payloads.req.User.LoginRequest;
import alpha.payeasebe.payloads.req.User.RegisterRequest;
import alpha.payeasebe.payloads.req.User.ResetPasswordRequest;

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

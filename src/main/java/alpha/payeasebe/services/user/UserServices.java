package alpha.payeasebe.services.user;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import alpha.payeasebe.payloads.req.FindUserEmail;
import alpha.payeasebe.payloads.req.User.ChangePINRequest;
import alpha.payeasebe.payloads.req.User.ChangePasswordRequest;
import alpha.payeasebe.payloads.req.User.CreatePINRequest;
import alpha.payeasebe.payloads.req.User.CreatePhoneNumberRequest;
import alpha.payeasebe.payloads.req.User.LoginRequest;
import alpha.payeasebe.payloads.req.User.RegisterRequest;
import alpha.payeasebe.payloads.req.User.ResetPasswordRequest;

public interface UserServices {
    ResponseEntity<?> registerService(RegisterRequest request);
    ResponseEntity<?> loginService(LoginRequest request);
    ResponseEntity<?> getUsersService();
    ResponseEntity<?> createUserPINService(CreatePINRequest request);
    ResponseEntity<?> addPhoneNumberService(CreatePhoneNumberRequest request);
    ResponseEntity<?> deletePhoneNumberService(String userId);
    ResponseEntity<?> changeUserPINService(ChangePINRequest request);
    ResponseEntity<?> changeUserPasswordService(ChangePasswordRequest request);
    ResponseEntity<?> getUserByIdService(String id);
    ResponseEntity<?> findUserByEmail(FindUserEmail request);
    ResponseEntity<?> storeImage(MultipartFile file, String newsId) throws IOException;
    ResponseEntity<?> checkTokenService(String token);
    ResponseEntity<?> changePasswordService(String token, ResetPasswordRequest request);
}


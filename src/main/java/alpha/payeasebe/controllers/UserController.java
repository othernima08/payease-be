package alpha.payeasebe.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import alpha.payeasebe.payloads.req.User.ChangePINRequest;
import alpha.payeasebe.payloads.req.User.ChangePasswordRequest;
import alpha.payeasebe.payloads.req.User.CreatePINRequest;
import alpha.payeasebe.payloads.req.User.CreatePhoneNumberRequest;
import alpha.payeasebe.payloads.req.User.LoginRequest;
import alpha.payeasebe.payloads.req.User.RegisterRequest;
import alpha.payeasebe.payloads.req.User.ResetPasswordRequest;
import alpha.payeasebe.payloads.req.User.VerifyPINRequest;
import alpha.payeasebe.payloads.req.FindUserEmail;
import alpha.payeasebe.services.user.UserServices;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserServices userServices;

    @PostMapping("/register")
    public ResponseEntity<?> registerService(@RequestBody @Valid RegisterRequest request) {
        return userServices.registerService(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginService(@RequestBody @Valid LoginRequest request) {
        return userServices.loginService(request);
    }

    @GetMapping()
    public ResponseEntity<?> getUsersService() {
        return userServices.getUsersService();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserByIdService(@PathVariable String id) {
        return userServices.getUserByIdService(id);
    }

    @PutMapping("/create-pin")
    public ResponseEntity<?> createPINService(@RequestBody @Valid CreatePINRequest request) {
        return userServices.createUserPINService(request);
    }

    @PutMapping("/find-email-reset")
    public ResponseEntity<?> findUserEMail(@RequestBody FindUserEmail request) {
        return userServices.findUserByEmail(request);
    }

     @GetMapping("/reset-password")
    public ResponseEntity<?> findUserEMail( @RequestParam(value = "token") String token) {
        return userServices.checkTokenService(token);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword( @RequestParam(value = "token") String token, @RequestBody ResetPasswordRequest request) {
        return userServices.changePasswordService(token, request);
    }

    @PutMapping("/change-pin")
    public ResponseEntity<?> changePINService(@RequestBody @Valid ChangePINRequest request) {
        return userServices.changeUserPINService(request);
    }

    @PutMapping("/change-password-after-login")
    public ResponseEntity<?> changeUserPassword(@RequestBody @Valid ChangePasswordRequest request) {
        return userServices.changeUserPasswordService(request);
    }

    @PutMapping("/verify-pin")
    public ResponseEntity<?> verifyPINService(@RequestBody @Valid VerifyPINRequest request) {
        return userServices.verifyUserPINService(request);
    }

    @PutMapping("/update-image")
    public ResponseEntity<?> updateImageService(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "userId") String userId) throws IOException{
        return userServices.storeImage(file, userId);
    }

    @PutMapping("/add-phone-number")
    public ResponseEntity<?> addPhoneNumberService(@RequestBody @Valid CreatePhoneNumberRequest request) {
        return userServices.addPhoneNumberService(request);
    }

    @DeleteMapping("/delete-phone-number/{userId}")
    public ResponseEntity<?> deleteUserPhoneNumber(@PathVariable String userId) {
        return userServices.deletePhoneNumberService(userId);
    }
}


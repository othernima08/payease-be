package alpha.payeasebe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import alpha.payeasebe.payloads.req.CreatePINRequest;
import alpha.payeasebe.payloads.req.LoginRequest;
import alpha.payeasebe.payloads.req.RegisterRequest;
import alpha.payeasebe.payloads.req.ResetPasswordRequest;
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

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPasswordService(@RequestBody @Valid ResetPasswordRequest request) {
        return userServices.resetPasswordService(request);
    }
}

package alpha.payeasebe.services.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alpha.payeasebe.configs.JwtUtil;
import alpha.payeasebe.exceptions.custom.EntityFoundException;
import alpha.payeasebe.models.User;
import alpha.payeasebe.payloads.req.User.ChangePINRequest;
import alpha.payeasebe.payloads.req.User.ChangePasswordRequest;
import alpha.payeasebe.payloads.req.User.CreatePINRequest;
import alpha.payeasebe.payloads.req.User.LoginRequest;
import alpha.payeasebe.payloads.req.User.RegisterRequest;
import alpha.payeasebe.payloads.req.User.ResetPasswordRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.repositories.UserRepository;
import alpha.payeasebe.validators.UserValidation;

@Service
public class UserServicesImpl implements UserServices {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidation userValidation;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired 
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<?> registerService(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EntityFoundException("Email is already used!");
        }

        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        
        return ResponseHandler.responseMessage(200, "User created successfully", true);
    }

    @Override
    public ResponseEntity<?> loginService(LoginRequest request) {
        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new NoSuchElementException("User is not found!");
        }

        User user = userRepository.findByEmail(request.getEmail());
        userValidation.validateUser(user);

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

        // validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new NoSuchElementException("Bad credentials: password doesn't match!");
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtil.createToken(request.getEmail());

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("token", token);
        
        return ResponseHandler.responseData(200, "Success login!", data);
    }

    @Override
    public ResponseEntity<?> getUsersService() {
        List<User> users = userRepository.findAll();

        return ResponseHandler.responseData(200, "Success", users);
    }

    @Override
    public ResponseEntity<?> createUserPINService(CreatePINRequest request) {
        if (!userRepository.existsByEmail(request.getEmailUser())) {
            throw new NoSuchElementException("User is not found!");
        }

        User user = userRepository.findByEmail(request.getEmailUser());

        userValidation.validateUser(user);

        user.setPin(passwordEncoder.encode(request.getPin()));

        userRepository.save(user);
        
        return ResponseHandler.responseMessage(200, "Create PIN success!", true);
    }

    @Override
    public ResponseEntity<?> resetPasswordService(ResetPasswordRequest request) {
        if (!userRepository.existsByEmail(request.getEmailUser())) {
            throw new NoSuchElementException("User is not found!");
        }

        User user = userRepository.findByEmail(request.getEmailUser());

        userValidation.validateUser(user);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        return ResponseHandler.responseMessage(200, "Reset password success", true);
    }

    @Override
    public ResponseEntity<?> getUserByIdService(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("User not found");
        });

        return ResponseHandler.responseData(200, "Success", user); 
    }

    @Override
    public ResponseEntity<?> changeUserPINService(ChangePINRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
            throw new NoSuchElementException("User not found");
        });

        if (!(passwordEncoder.matches(request.getCurrentPin(), user.getPin()))) {
            throw new NoSuchElementException("Bad Credentials: PIN doesn't match!");
        }

        user.setPin(passwordEncoder.encode(request.getNewPin()));
        userRepository.save(user);

        return ResponseHandler.responseMessage(200, "Change PIN Success", true); 
    }

    @Override
    public ResponseEntity<?> changeUserPasswordService(ChangePasswordRequest request) {
       User user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
            throw new NoSuchElementException("User not found");
        });

        if (!(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))) {
            throw new NoSuchElementException("Bad Credentials: PIN doesn't match!");
        }

        user.setPin(passwordEncoder.encode(request.getCurrentPassword()));
        userRepository.save(user);

        return ResponseHandler.responseMessage(200, "Change Password Success", true); 
    }
}

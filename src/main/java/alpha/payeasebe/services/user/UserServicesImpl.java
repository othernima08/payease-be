package alpha.payeasebe.services.user;

import java.time.LocalDateTime;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import alpha.payeasebe.configs.JwtUtil;
import alpha.payeasebe.exceptions.custom.EntityFoundException;
import alpha.payeasebe.models.ResetToken;
import alpha.payeasebe.models.User;
import alpha.payeasebe.payloads.req.CreatePINRequest;
import alpha.payeasebe.payloads.req.FindUserEmail;
import alpha.payeasebe.payloads.req.LoginRequest;
import alpha.payeasebe.payloads.req.MailRequest;
import alpha.payeasebe.payloads.req.RegisterRequest;
import alpha.payeasebe.payloads.req.ResetPasswordRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.repositories.ResetPasswordRepository;
import alpha.payeasebe.repositories.UserRepository;
import alpha.payeasebe.services.mail.MailService;
import alpha.payeasebe.validators.UserValidation;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    ResetPasswordRepository resetPasswordRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidation userValidation;

    @Autowired
    MailService mailService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<?> registerService(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EntityFoundException("Email is already used!");
        }

        User user = new User(request.getFirstName(), request.getLastName(), request.getEmail(),
                passwordEncoder.encode(request.getPassword()));
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

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());

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

    // @Override
    // public ResponseEntity<?> resetPasswordService(ResetPasswordRequest request) {
    // if (!userRepository.existsByEmail(request.getEmailUser())) {
    // throw new NoSuchElementException("User is not found!");
    // }

    // User user = userRepository.findByEmail(request.getEmailUser());

    // userValidation.validateUser(user);

    // user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    // userRepository.save(user);

    // return ResponseHandler.responseMessage(200, "Reset password success", true);
    // }

    @Override
    public ResponseEntity<?> getUserByIdService(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("User not found");
        });

        return ResponseHandler.responseData(200, "Success", user);
    }

    @Override
    public ResponseEntity<?> findUserByEmail(FindUserEmail request) {

        if (!userRepository.existsByEmail(request.getEmail())) {
            throw new NoSuchElementException("Email not found");
        }

        User user = userRepository.findByEmail(request.getEmail());

        ResetToken resetToken = new ResetToken(user);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDateTime = now.plusMinutes(5);
        resetToken.setExpiryDateTime(expireDateTime);

        resetPasswordRepository.save(resetToken);

        StringBuilder buildMessageMail = new StringBuilder();
        buildMessageMail.append("To reset your password, please check your email by click here: ");
        buildMessageMail.append(
                "http://localhost:5173/create-password/" + resetToken.getToken());
        buildMessageMail.append(
                "    /n This link is expired in 5 minutes.");
        mailService.sendMail(new MailRequest(user.getEmail(), "Reset your password!", buildMessageMail.toString()));

        return ResponseHandler.responseData(201, "Password Reset Succesfull!", user);
    }

    @Override
    public ResponseEntity<?> checkTokenService(String token) {
        ResetToken resetToken = resetPasswordRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token is Invalid!"));

        // tokenvalidation

        LocalDateTime currentTime = LocalDateTime.now();
        if (resetToken.getExpiryDateTime() != null && currentTime.isAfter(resetToken.getExpiryDateTime())) {
            throw new RuntimeException("Token has expired!");
        }

        // used or not
        if (!resetToken.getIsActive().equals(true)) {
            throw new RuntimeException("This token already used, please go to forgot-password page.");
        }

        User user = resetToken.getUser();

        return ResponseHandler.responseData(200, "Token Is Valid!", user);
    }

    @Override
    public ResponseEntity<?> changePasswordService(String token, ResetPasswordRequest request) {
        ResetToken resetToken = resetPasswordRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token is Invalid!"));

        User user = resetToken.getUser();

        String passwordFix;
        userValidation.validateUser(user);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirmation doesnt match!");
        }

        else {
            passwordFix = request.getConfirmPassword();
            user.setPassword(passwordEncoder.encode(passwordFix));
            resetToken.setIsActive(false);
            userRepository.save(user);
        }

        return ResponseHandler.responseData(200, "Password is changed!", user);

    }
}

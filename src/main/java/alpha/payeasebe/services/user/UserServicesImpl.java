package alpha.payeasebe.services.user;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import alpha.payeasebe.configs.JwtUtil;
import alpha.payeasebe.exceptions.custom.EntityFoundException;
import alpha.payeasebe.models.ResetToken;
import alpha.payeasebe.models.User;
import alpha.payeasebe.payloads.req.User.ChangePINRequest;
import alpha.payeasebe.payloads.req.User.ChangePasswordRequest;
import alpha.payeasebe.payloads.req.User.CreatePINRequest;
import alpha.payeasebe.payloads.req.User.CreatePhoneNumberRequest;
import alpha.payeasebe.payloads.req.User.LoginRequest;
import alpha.payeasebe.payloads.req.User.RegisterRequest;
import alpha.payeasebe.payloads.req.User.ResetPasswordRequest;
import alpha.payeasebe.payloads.req.User.VerifyPINRequest;
import alpha.payeasebe.payloads.req.FindUserEmail;
import alpha.payeasebe.payloads.req.MailRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.payloads.res.ResponseShowUsersNotNullAndNotUser;
import alpha.payeasebe.repositories.ResetPasswordRepository;
import alpha.payeasebe.repositories.UserRepository;
import alpha.payeasebe.services.mail.MailService;
import alpha.payeasebe.services.virtualAccounts.UserVirtualAccountService;
import alpha.payeasebe.validators.UserValidation;
import jakarta.validation.ValidationException;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    ResetPasswordRepository resetPasswordRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidation userValidation;

    @Autowired
    UserVirtualAccountService userVirtualAccountService;

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

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

        user.setPin(passwordEncoder.encode(request.getPin()));

        userRepository.save(user);

        return ResponseHandler.responseMessage(200, "Create PIN success!", true);
    }

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

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

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

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }
        return ResponseHandler.responseData(200, "Token Is Valid!", user);
    }

    @Override
    public ResponseEntity<?> changePasswordService(String token, ResetPasswordRequest request) {
        ResetToken resetToken = resetPasswordRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token is Invalid!"));

        User user = resetToken.getUser();
        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

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

    @Override
    public ResponseEntity<?> changeUserPINService(ChangePINRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
            throw new NoSuchElementException("User not found");
        });

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

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

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

        if (!(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))) {
            throw new NoSuchElementException("Bad Credentials: Password doesn't match!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseHandler.responseMessage(200, "Password edited successfully", true);
    }

    @Override
    public ResponseEntity<?> storeImage(MultipartFile file, String userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("news tidak ditemukan"));

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

        user.setPictureProfile(file.getBytes());
        userRepository.save(user);

        String sharedUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath() // localhost
                .path("/users/update-image/") // Ubah ini sesuai dengan path Anda
                .path(user.getId()) // ID pengguna
                .toUriString();

        // Atur sharedUrl ke objek User
        user.setSharedUrl(sharedUrl);
        userRepository.save(user);
        return ResponseHandler.responseData(201, "success", user);
    }

    // @Override
    // public ResponseEntity<?> storeImage(MultipartFile file, String newsId) throws
    // IOException {
    // // ambil nama gambar
    // String imgName = StringUtils.cleanPath(file.getOriginalFilename());
    // // cari entitas news
    // News news = newsRepository.findById(newsId).orElseThrow(()
    // -> new NoSuchElementException("news tidak ditemukan"));

    // // buatkan entitas image news
    // StoreImage image = new StoreImage(imgName, file.getBytes(), news);
    // imageRepository.save(image); // menyimpan id

    // // buatkan sharedUrl
    // /*
    // * endpoint untuk upload: /admin/files/news -> POST
    // * endpoint untuk load: /files/news/{uuidGambar} ->GET
    // */
    // String sharedUrl = ServletUriComponentsBuilder
    // .fromCurrentContextPath() // localhost:9098
    // .path("/files/news/")
    // .path(image.getId()) // id gambar
    // .toUriString();

    // // set sharedurl ke obj image news
    // image.setSharedUrl(sharedUrl);
    // imageRepository.save(image);
    // return ResponseHandler.responseData(201, "success", image);
    // }

    public ResponseEntity<?> addPhoneNumberService(CreatePhoneNumberRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
            throw new NoSuchElementException("User not found");
        });

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

        // Cek no hp user
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            throw new EntityFoundException("Phone number is already added!");
        }

        String phoneNumber = request.getPhoneNumber();

        // Validasi nomor HP
        if (!phoneNumber.matches("^[0-9]*$") || phoneNumber.length() < 12 || phoneNumber.length() > 13) {
            throw new ValidationException("Invalid phone number format or length");
        }

        // Cek apakah nomor HP sudah ada dalam database
        User existingUserWithPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);
        if (existingUserWithPhoneNumber != null) {
            throw new EntityFoundException("Phone number is already in use by another user!");
        }

        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);

        userVirtualAccountService.generateUserVirtualAccountsService(phoneNumber);

        return ResponseHandler.responseMessage(200, "Phone number added successfully", true);
    }

    @Override
    public ResponseEntity<?> deletePhoneNumberService(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NoSuchElementException("User not found");
        });

        userValidation.validateUser(user);

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

        // Cek jika nomor HP sudah ada atau tidak
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            throw new NoSuchElementException("Phone number is not found!");
        }

        // Hapus nomor HP
        user.setPhoneNumber(null);
        userRepository.save(user);

        userVirtualAccountService.deleteUserVirtualAccountsService(userId);

        return ResponseHandler.responseMessage(200, "Phone number deleted successfully", true);
    }

    @Override
    public ResponseEntity<?> verifyUserPINService(VerifyPINRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
            throw new NoSuchElementException("User not found");
        });

        if (user.getIsDeleted()) {
            throw new NoSuchElementException("User is not active or already deleted");
        }

        if (!(passwordEncoder.matches(request.getCurrentPin(), user.getPin()))) {
            throw new NoSuchElementException("Bad Credentials: PIN doesn't match!");
        }

        return ResponseHandler.responseMessage(200, "PIN match", true);
    }

    @Override
    public ResponseEntity<?> getUserPhoneNotNullAndNotSender(String id) {
        List<ResponseShowUsersNotNullAndNotUser> userListNotSender = new ArrayList<>();
        userListNotSender.addAll(userRepository.getUserNotNull(id));
        return ResponseHandler.responseData(200, "User yang not null nomornya", userListNotSender);
    }

}

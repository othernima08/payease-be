package alpha.payeasebe.services.otp;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import alpha.payeasebe.models.OTP;
import alpha.payeasebe.models.User;
import alpha.payeasebe.payloads.req.OTP.CreateOTPRequest;
import alpha.payeasebe.payloads.req.OTP.VerifyOTPRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.repositories.OTPRepository;
import alpha.payeasebe.repositories.UserRepository;
import alpha.payeasebe.validators.OTPValidation;
import alpha.payeasebe.validators.UserValidation;

@Service
public class OTPServicesImpl implements OTPServices {
    @Autowired
    OTPRepository otpRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidation userValidation;

    @Autowired
    OTPValidation otpValidation;

    @Override
    public ResponseEntity<?> generateOTPCodeService(CreateOTPRequest request) {
        User user = userRepository.findByEmail(request.getEmailUser());

        userValidation.validateUser(user);
        
        String otpCode = null;

        do {
            otpCode = generateOTP();
        } while (otpRepository.existsByOtpCode(generateOTP())); 

        OTP otp = new OTP(user, otpCode, LocalDateTime.now().plusMinutes(1));
        otpRepository.save(otp);

        return ResponseHandler.responseMessage(200, "Generate OTP success!", true);
    }

    @Override
    public ResponseEntity<?> verifyOTPCodeService(VerifyOTPRequest request) {
        OTP otp = otpRepository.findByOtpCode(request.getOtpCode());

        otpValidation.validateOTP(otp);

        if (LocalDateTime.now().isAfter(otp.getExpiredAt()) ) {
            throw new IllegalArgumentException("OTP Code is expired");
        }

        return ResponseHandler.responseMessage(200, "OTP correct", true);
    }

    @Override
    public String generateOTP() {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Integer OTP_LENGTH = 6;

        StringBuilder otp = new StringBuilder();

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < OTP_LENGTH; i++) {
            int randomIndex = random.nextInt(ALPHA_NUMERIC_STRING.length());
            char randomChar = ALPHA_NUMERIC_STRING.charAt(randomIndex);
            otp.append(randomChar);
        }

        return otp.toString();
    }
}

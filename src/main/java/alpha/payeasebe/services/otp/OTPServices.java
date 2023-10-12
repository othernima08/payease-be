package alpha.payeasebe.services.otp;

import org.springframework.http.ResponseEntity;

import alpha.payeasebe.payloads.req.OTP.CreateOTPRequest;
import alpha.payeasebe.payloads.req.OTP.VerifyOTPRequest;

public interface OTPServices {
    ResponseEntity<?> generateOTPCodeService(CreateOTPRequest request);
    ResponseEntity<?> verifyOTPCodeService(VerifyOTPRequest request);
    String generateOTP();
}

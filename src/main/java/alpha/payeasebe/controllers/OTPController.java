package alpha.payeasebe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import alpha.payeasebe.payloads.req.OTP.CreateOTPRequest;
import alpha.payeasebe.payloads.req.OTP.VerifyOTPRequest;
import alpha.payeasebe.services.otp.OTPServices;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/otp")
public class OTPController {
    @Autowired
    OTPServices otpServices;

    @PostMapping("/generate")
    public ResponseEntity<?> generateOTP(@RequestBody @Valid CreateOTPRequest request) {
        return otpServices.generateOTPCodeService(request);
    }

    @GetMapping()
    public ResponseEntity<?> verifyOTP(@RequestBody @Valid VerifyOTPRequest request) {
        return otpServices.verifyOTPCodeService(request);
    }
}

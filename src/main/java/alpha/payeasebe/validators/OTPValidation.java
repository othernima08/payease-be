package alpha.payeasebe.validators;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.stereotype.Service;

import alpha.payeasebe.models.OTP;

@Service
public class OTPValidation {
    public void validateOTP(OTP otp) {
        if(otp == null || Objects.isNull(otp)){
            throw new NoSuchElementException("OTP code is not found");
        }
    }
}

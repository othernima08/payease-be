package alpha.payeasebe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.OTP;

public interface OTPRepository extends JpaRepository<OTP,String>{
    OTP findByOtpCode(String otpCode);
    Boolean existsByOtpCode(String otpCode);
}

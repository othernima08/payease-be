package alpha.payeasebe.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(name = "otp_requests")
public class OTP {
    @Id
    @UuidGenerator
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 6, unique = true)
    private String otpCode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    public OTP(User user, String otpCode, LocalDateTime expiredAt) {
        this.user = user;
        this.otpCode = otpCode;
        this.expiredAt = expiredAt;
    }
}

package alpha.payeasebe.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "reset_token")
public class ResetToken {
    @Id
    @UuidGenerator
    private String id;
    private String token;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expiryDateTime; 

    private Boolean isActive = true;

    @JsonIgnore
    @ManyToOne
    private User user;


    public ResetToken(User user) {
        this.user = user;
        token = UUID.randomUUID().toString();
    }


    
}

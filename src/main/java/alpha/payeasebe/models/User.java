package alpha.payeasebe.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator
    private String id;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;
    
    @Column(length = 50, unique = true)
    private String phoneNumber = null;

    @Column(length = 255, unique = true)
    private String email;

    @JsonIgnore
    @Column(length = 255)
    private String password;

    @JsonIgnore
    @Column(length = 255)
    private String pin = null;

    private Double balance = 0.; 

    private String profilePictureUrl = null;

    @JsonIgnore
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    private Boolean isDeleted = false;


    @OneToMany(mappedBy = "user")
    private List<ResetToken> resetTokens = new ArrayList<>();

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}

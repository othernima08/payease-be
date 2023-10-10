package alpha.payeasebe.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(name = "providers")
public class Providers {
    @Id
    @UuidGenerator
    private String id;

    @Column(length = 255, unique = true)
    private String name;

    private String profilePictureUrl = null;

    @JsonIgnore
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    private Boolean isDeleted = false;

    public Providers(String name, String profilePictureUrl) {
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }   
}

package alpha.payeasebe.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "user_virtual_accounts")
public class UserVirtualAccount {
    @Id
    @UuidGenerator
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tc_id")
    private TransactionCategories transactionCategories;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Providers providers;

    @Column(length = 50)
    private String number;

    @JsonIgnore
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    private Boolean isDeleted = false;

    public UserVirtualAccount(User user, TransactionCategories transactionCategories, Providers providers, String number) {
        this.user = user;
        this.transactionCategories = transactionCategories;
        this.providers = providers;
        this.number = number;
    }
}

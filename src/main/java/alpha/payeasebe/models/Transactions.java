package alpha.payeasebe.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "transactions")
public class Transactions {
    @Id
    @UuidGenerator
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "transaction_categories_id")
    private TransactionCategories transactionCategories;

    private Double amount;

    @CreationTimestamp
    private LocalDateTime transactionTime;

    private Boolean alreadyDone = false;

    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    private Boolean isDeleted = false;

    public Transactions(User user, TransactionCategories transactionCategories, Double amount) {
        this.user = user;
        this.transactionCategories = transactionCategories;
        this.amount = amount;
    }
}

package alpha.payeasebe.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "transfers")
public class Transfers {
    @Id
    @UuidGenerator
    private String id;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transactions transactions;

    private String notes = "";

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime transactionTime;

    private Boolean isDeleted = false;

    public Transfers(User user, Transactions transactions, String notes, LocalDateTime transactionTime) {
        this.user = user;
        this.transactions = transactions;
        this.notes = notes;
        this.transactionTime = transactionTime;
    }

    
}

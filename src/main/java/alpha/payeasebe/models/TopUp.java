package alpha.payeasebe.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
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
@Table(name = "top_ups")
public class TopUp {
    @Id
    @UuidGenerator
    private String id;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transactions transactions;

    @ManyToOne
    @JoinColumn(name = "method_id")
    private UserVirtualAccount userVirtualAccount;

    @Column(unique = true)
    private String paymentCode;

    private Boolean isExpired = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Boolean isDeleted = false;

    public TopUp(Transactions transactions, UserVirtualAccount userVirtualAccount, String paymentCode) {
        this.transactions = transactions;
        this.userVirtualAccount = userVirtualAccount;
        this.paymentCode = paymentCode;
    }
}

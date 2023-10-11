package alpha.payeasebe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.Transactions;

public interface TransactionsRepository extends JpaRepository<Transactions, String> {
        
}

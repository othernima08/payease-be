package alpha.payeasebe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.Transfers;

public interface TransferRepository extends JpaRepository<Transfers, String> {
    
}

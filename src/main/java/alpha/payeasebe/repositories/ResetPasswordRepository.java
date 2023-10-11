package alpha.payeasebe.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.ResetToken;

public interface ResetPasswordRepository extends JpaRepository<ResetToken, String> {
    Optional<ResetToken> findByToken(String token);
}

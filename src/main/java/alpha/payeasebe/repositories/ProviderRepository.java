package alpha.payeasebe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.Providers;

public interface ProviderRepository extends JpaRepository<Providers, String> {
    Boolean existsByName(String name);
    Providers findByName(String name);
}

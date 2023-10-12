package alpha.payeasebe.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import alpha.payeasebe.models.TransactionCategories;

public interface TransactionCategoryRepository extends JpaRepository<TransactionCategories,String>{
    Boolean existsByType(String type);
    TransactionCategories findByType(String type);
}

package alpha.payeasebe.repositories;

import java.util.List;

import org.antlr.v4.runtime.atn.SemanticContext.AND;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import alpha.payeasebe.models.Transactions;
import alpha.payeasebe.payloads.res.ResponseHistoryTransaction;
import alpha.payeasebe.payloads.res.ResponseShowTopUpHistory;

public interface TransactionsRepository extends JpaRepository<Transactions, String> {
    @Query(value = "SELECT t.id, t.amount, t.transaction_time, CASE WHEN t.already_done = true THEN 'Success' ELSE 'Pending' END AS status FROM transactions t JOIN transaction_categories tc ON t.transaction_categories_id = tc.id JOIN users u ON t.user_id = u.id WHERE tc.type = 'Top Up' AND t.is_deleted = false AND t.user_id = ? GROUP BY t.id", nativeQuery = true)
    List<ResponseShowTopUpHistory> getTopUpHistoryByUserId(String userId);

    @Query(value = "SELECT u.first_name, u.last_name, t.amount, tc.type FROM Users u, Transactions t, Transaction_categories tc WHERE t.user_id = u.id AND t.transaction_categories_id = tc.id AND t.already_done = TRUE AND t.id = (SELECT id FROM Transactions WHERE user_id = t.user_id ORDER BY transaction_time DESC LIMIT 1)", nativeQuery = true)
    List<ResponseHistoryTransaction> getHistory(String UserId);
}

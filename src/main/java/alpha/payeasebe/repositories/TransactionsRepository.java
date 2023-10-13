package alpha.payeasebe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import alpha.payeasebe.models.Transactions;
import alpha.payeasebe.payloads.res.ResponseShowTopUpHistory;

public interface TransactionsRepository extends JpaRepository<Transactions, String> {
    @Query(value = "SELECT tu.id, tc.type, p.name, p.profile_picture_url, CASE WHEN t.already_done = true THEN 'Success' ELSE 'Pending' END AS status, t.transaction_time, t.amount \r\n" + //
            "FROM top_ups tu\r\n" + //
            "JOIN transactions t ON tu.transaction_id = t.id\r\n" + //
            "JOIN transaction_categories tc ON tc.id = t.transaction_categories_id\r\n" + //
            "JOIN user_virtual_accounts uac ON tu.method_id = uac.id\r\n" + //
            "JOIN providers p ON p.id = uac.provider_id\r\n" + //
            "WHERE t.user_id = ? AND t.is_deleted = false\r\n" + //
            "GROUP BY tu.id", nativeQuery = true)
    List<ResponseShowTopUpHistory> getTopUpHistoryByUserId(String userId);
}

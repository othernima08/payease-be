package alpha.payeasebe.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import alpha.payeasebe.models.Transactions;
import alpha.payeasebe.payloads.res.ResponseShowTopUpHistory;
import alpha.payeasebe.payloads.res.ResponseShowTransactionHistory;

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

    @Query(value = "SELECT tu.id, tc.type, p.name, p.profile_picture_url, CASE WHEN t.already_done = true THEN 'Success' ELSE 'Pending' END AS status, t.transaction_time, t.amount \r\n" + //
            "FROM top_ups tu\r\n" + //
            "JOIN transactions t ON tu.transaction_id = t.id\r\n" + //
            "JOIN transaction_categories tc ON tc.id = t.transaction_categories_id\r\n" + //
            "JOIN user_virtual_accounts uac ON tu.method_id = uac.id\r\n" + //
            "JOIN providers p ON p.id = uac.provider_id\r\n" + //
            "WHERE t.user_id = ?1 AND t.is_deleted = false AND t.already_done = ?2\r\n" + //
            "GROUP BY tu.id", nativeQuery = true)
    List<ResponseShowTopUpHistory> getTopUpHistoryByUserIdAndStatus(String userId, Boolean isDeleted);

     @Query(value = "SELECT tf.id, CONCAT(u.first_name, ' ', u.last_name) AS name, u.shared_url AS profile_picture_url, CONCAT(tc.type, ' to') AS type, t.transaction_time, t.amount\r\n" + //
             "FROM transfers tf\r\n" + //
             "JOIN users u ON tf.recipient_id = u.id\r\n" + //
             "JOIN transactions t ON t.id = tf.transaction_id\r\n" + //
             "JOIN transaction_categories tc ON tc.id = t.transaction_categories_id\r\n" + //
             "WHERE t.already_done = true AND t.user_id = ?\r\n" + //
             "GROUP BY tf.id", nativeQuery = true)
    List<ResponseShowTransactionHistory> getTransferToHistoryByUserId(String userId);

    @Query(value = "SELECT tf.id, CONCAT(u.first_name, ' ', u.last_name) AS name, u.shared_url AS profile_picture_url, CONCAT(tc.type, ' from') AS type, t.transaction_time, t.amount\r\n" + //
            "FROM transfers tf\r\n" + //
            "JOIN transactions t ON t.id = tf.transaction_id\r\n" + //
            "JOIN users u ON t.user_id= u.id\r\n" + //
            "JOIN transaction_categories tc ON tc.id = t.transaction_categories_id\r\n" + //
            "WHERE t.already_done = true AND tf.recipient_id =?\r\n" + //
            "GROUP BY tf.id", nativeQuery = true)
    List<ResponseShowTransactionHistory> getTransferFromHistoryByUserId(String userId);

    @Query(value = "SELECT tu.id, p.name, tc.type, p.profile_picture_url, t.transaction_time, t.amount\r\n" + //
            "FROM top_ups tu\r\n" + //
            "JOIN transactions t ON tu.transaction_id = t.id\r\n" + //
            "JOIN transaction_categories tc ON tc.id = t.transaction_categories_id\r\n" + //
            "JOIN user_virtual_accounts uac ON tu.method_id = uac.id\r\n" + //
            "JOIN providers p ON p.id = uac.provider_id\r\n" + //
            "WHERE  t.is_deleted = false AND t.already_done = true AND t.user_id = ? \r\n" + //
            "GROUP BY tu.id", nativeQuery = true)
    List<ResponseShowTransactionHistory> getTopUpByUserId(String userId);
}

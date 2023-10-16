package alpha.payeasebe.services.transactions;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alpha.payeasebe.models.TopUp;
import alpha.payeasebe.models.TransactionCategories;
import alpha.payeasebe.models.Transactions;
import alpha.payeasebe.models.Transfers;
import alpha.payeasebe.models.User;
import alpha.payeasebe.models.UserVirtualAccount;
import alpha.payeasebe.payloads.req.Transactions.TopUpRequest;
import alpha.payeasebe.payloads.req.Transactions.TransferRequest;
import alpha.payeasebe.payloads.res.ResponseHandler;
import alpha.payeasebe.payloads.res.ResponseShowTopUpHistory;
import alpha.payeasebe.payloads.res.ResponseShowTransactionHistory;
import alpha.payeasebe.repositories.TopUpRepository;
import alpha.payeasebe.repositories.TransactionCategoryRepository;
import alpha.payeasebe.repositories.TransactionsRepository;
import alpha.payeasebe.repositories.TransferRepository;
import alpha.payeasebe.repositories.UserRepository;
import alpha.payeasebe.repositories.UserVirtualAccountRepository;
import alpha.payeasebe.validators.TopUpValidation;
import alpha.payeasebe.validators.TransactionCategoriesValidation;
import alpha.payeasebe.validators.UserValidation;

@Service
public class TransactionServiceImpl implements TransactionsService {
    @Autowired
    TransactionsRepository transactionsRepository;

    @Autowired
    TransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserValidation userValidation;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserVirtualAccountRepository userVirtualAccountRepository;

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    TopUpRepository topUpRepository;

    @Autowired
    TransactionCategoriesValidation transactionCategoriesValidation;

    @Autowired
    TopUpValidation topUpValidation;

    @Override
    public Transactions createTransactionService(String userId, String transactionCategoryName, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        TransactionCategories transactionCategories = transactionCategoryRepository.findByType(transactionCategoryName);
        transactionCategoriesValidation.validateTransactionCategory(transactionCategories);

        Transactions transactions = new Transactions(user, transactionCategories, amount);
        transactionsRepository.save(transactions);

        return transactions;
    }

    @Override
    public ResponseEntity<?> transferService(TransferRequest request) {
        Transactions transactions = createTransactionService(request.getUserId(), "Transfer", request.getAmount());

        User user = transactions.getUser();

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        User recipient = userRepository.findByPhoneNumber(request.getRecipientPhoneNumber());
        userValidation.validateUser(recipient);

        if (recipient.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        if (user.getBalance() < request.getAmount()) {
            transactions.setIsDeleted(true);
            transactionsRepository.save(transactions);

            throw new IllegalArgumentException("Insufficient balance");
        }

        Transfers transfers = new Transfers(recipient, transactions, request.getNotes(), request.getTransactionTime());
        transferRepository.save(transfers);

        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            transactions.setIsDeleted(true);
            transactionsRepository.save(transactions);

            throw new NoSuchElementException("Bad credentials: pin doesn't match!");
        }

        transactions.setAlreadyDone(true);
        transactionsRepository.save(transactions);

        user.setBalance(user.getBalance() - request.getAmount());
        userRepository.save(user);

        recipient.setBalance(recipient.getBalance() + request.getAmount());
        userRepository.save(recipient);

        return ResponseHandler.responseData(200,
                "Transfer from " + user.getFirstName() + " " + user.getLastName() + " success", transfers.getId());
    }

    @Override
    public ResponseEntity<?> transferDetail(String id) {
        Transfers transfers = transferRepository.findById(id).orElseThrow(() -> {
            throw new NoSuchElementException("transfer not found");
        });

        return ResponseHandler.responseData(200, "Get top up history data success", transfers);
    }

    public ResponseEntity<?> topUpGenerateCodeService(TopUpRequest request) {
        double amount = request.getAmount();
        if (amount < 10000 || amount > 10000000 || amount <= 0) {
            throw new IllegalArgumentException("Top up amount must be between 10,000 and 10,000,000 and cannot be 0 or negative");
        }

        Transactions transaction = createTransactionService(request.getUserId(), "Top Up", request.getAmount());

        String paymentCode = generatePaymentCode();

        UserVirtualAccount userVirtualAccount = userVirtualAccountRepository.findById(request.getVirtualAccountId())
                .orElseThrow(() -> new NoSuchElementException("Virtual account is not found"));

        TopUp topUp = new TopUp(transaction, userVirtualAccount, paymentCode);
        topUpRepository.save(topUp);

        Map<String, Object> data = new HashMap<>();
        data.put("virtualAccountId", topUp.getUserVirtualAccount().getId());
        data.put("paymentCode", topUp.getPaymentCode());

        return ResponseHandler.responseData(200, "Success", data);
    }

    @Override
    public ResponseEntity<?> topUpPaymentCodeService(String paymentCode) {
        TopUp topUp = topUpRepository.findByPaymentCode(paymentCode);

        topUpValidation.validateTopUp(topUp);

        if (topUp.getIsExpired()) {
            throw new IllegalArgumentException("Payment code already expired");
        }

        Transactions transaction = transactionsRepository.findById(topUp.getTransactions().getId())
                .orElseThrow(() -> new NoSuchElementException("Transaction is not found"));

        User user = transaction.getUser();

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        if (transaction.getAmount() < 10000) {
            throw new IllegalArgumentException("Minimum top upzzz amount is Rp10000");
        } else if (transaction.getAmount() > 10000000) {
            throw new IllegalArgumentException("Maximum top up amount is Rp10000000");
        }

        transaction.setAlreadyDone(true);
        transactionsRepository.save(transaction);

        user.setBalance(user.getBalance() + transaction.getAmount());
        userRepository.save(user);

        topUp.setIsExpired(true);
        topUpRepository.save(topUp);

        return ResponseHandler.responseMessage(200,
                "Top up for " + user.getFirstName() + " " + user.getLastName() + " success", true);
    }

    public String generatePaymentCode() {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        Integer CODE_LENGTH = 12;

        StringBuilder code = new StringBuilder();

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(ALPHA_NUMERIC_STRING.length());
            char randomChar = ALPHA_NUMERIC_STRING.charAt(randomIndex);
            code.append(randomChar);
        }

        return code.toString();
    }

    @Override
    public ResponseEntity<?> getTopUpHistoryByUserIdService(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTopUpHistory> topUpHistories = transactionsRepository.getTopUpHistoryByUserId(userId);

        Map<String, List<ResponseShowTopUpHistory>> categorizedTransactions = categorizeTopUpHistory(topUpHistories);

        return ResponseHandler.responseData(200,
                "Get " + user.getFirstName() + " " + user.getLastName() + " top up history success",
                categorizedTransactions);
    }

    @Override
    public ResponseEntity<?> getTopUpHistoryByUserIdAndStatusService(String userId, Boolean isDeleted) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTopUpHistory> topUpHistories = transactionsRepository.getTopUpHistoryByUserIdAndStatus(userId,
                isDeleted);

        Map<String, List<ResponseShowTopUpHistory>> categorizedTransactions = categorizeTopUpHistory(topUpHistories);

        return ResponseHandler.responseData(200,
                "Get " + user.getFirstName() + " " + user.getLastName() + " top up history success",
                categorizedTransactions);
    }

    @Override
    public ResponseEntity<?> getTransactionHistoryByUserIdService(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();
        transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferToHistoryByUserId(userId));

        Collections.sort(transactionHistories,
                (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));

        Map<String, List<ResponseShowTransactionHistory>> categorizedTransactions = categorizeTransactionHistory(
                transactionHistories);

        return ResponseHandler.responseData(200,
                "Get " + user.getFirstName() + " " + user.getLastName() + " transaction history success",
                categorizedTransactions);
    }

    @Override
    public ResponseEntity<?> getTransactionHistoryByUserIdAndTypeService(String userId, Boolean isIncome) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();

        if (isIncome) {
            transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
            transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));

            Collections.sort(transactionHistories,
                    (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));
        } else {
            transactionHistories.addAll(transactionsRepository.getTransferToHistoryByUserId(userId));
        }

        Map<String, List<ResponseShowTransactionHistory>> categorizedTransactions = categorizeTransactionHistory(
                transactionHistories);

        return ResponseHandler.responseData(200,
                "Get " + user.getFirstName() + " " + user.getLastName() + " transaction history success",
                categorizedTransactions);
    }

    public Map<String, List<ResponseShowTransactionHistory>> categorizeTransactionHistory(
            List<ResponseShowTransactionHistory> transactionHistories) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate startOfMonth = today.withDayOfMonth(1);

        List<ResponseShowTransactionHistory> thisWeekTransactions = new ArrayList<>();
        List<ResponseShowTransactionHistory> thisMonthTransactions = new ArrayList<>();
        List<ResponseShowTransactionHistory> olderTransactions = new ArrayList<>();

        for (ResponseShowTransactionHistory history : transactionHistories) {
            Date transactionDate = Date.from(history.getTransaction_time().atZone(ZoneId.systemDefault()).toInstant());
            LocalDate localTransactionDate = transactionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (localTransactionDate.isEqual(today)) {
                thisWeekTransactions.add(history);
            } else if (localTransactionDate.isAfter(startOfWeek)) {
                thisWeekTransactions.add(history);
            } else if (localTransactionDate.isAfter(startOfMonth)) {
                thisMonthTransactions.add(history);
            } else {
                olderTransactions.add(history);
            }
        }

        Map<String, List<ResponseShowTransactionHistory>> categorizedTransactions = new HashMap<>();
        categorizedTransactions.put("thisWeek", thisWeekTransactions);
        categorizedTransactions.put("thisMonth", thisMonthTransactions);
        categorizedTransactions.put("older", olderTransactions);

        return categorizedTransactions;
    }

    public Map<String, List<ResponseShowTopUpHistory>> categorizeTopUpHistory(
            List<ResponseShowTopUpHistory> topUpHistories) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate startOfMonth = today.withDayOfMonth(1);

        List<ResponseShowTopUpHistory> thisWeekTransactions = new ArrayList<>();
        List<ResponseShowTopUpHistory> thisMonthTransactions = new ArrayList<>();
        List<ResponseShowTopUpHistory> olderTransactions = new ArrayList<>();

        for (ResponseShowTopUpHistory history : topUpHistories) {
            Date transactionDate = Date.from(history.getTransaction_time().atZone(ZoneId.systemDefault()).toInstant());
            LocalDate localTransactionDate = transactionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (localTransactionDate.isEqual(today)) {
                thisWeekTransactions.add(history);
            } else if (localTransactionDate.isAfter(startOfWeek)) {
                thisWeekTransactions.add(history);
            } else if (localTransactionDate.isAfter(startOfMonth)) {
                thisMonthTransactions.add(history);
            } else {
                olderTransactions.add(history);
            }
        }

        Map<String, List<ResponseShowTopUpHistory>> categorizedTransactions = new HashMap<>();
        categorizedTransactions.put("thisWeek", thisWeekTransactions);
        categorizedTransactions.put("thisMonth", thisMonthTransactions);
        categorizedTransactions.put("older", olderTransactions);

        return categorizedTransactions;
    }

    @Override
    public ResponseEntity<?> getTopFiveTransactionByUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();
        transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferToHistoryByUserId(userId));

        Collections.sort(transactionHistories,
                (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));

        Integer limit = Math.min(5, transactionHistories.size());
        List<ResponseShowTransactionHistory> top5TransactionHistories = transactionHistories.subList(0, limit);

        return ResponseHandler.responseData(200,
                "Get top five" + user.getFirstName() + " " + user.getLastName() + " transaction history success",
                top5TransactionHistories);
    }

    @Override
    public ResponseEntity<?> getTransactionHistoryByUserIdAndDateTimeService(String userId, String startDate,
            String endDate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMM yyyy", Locale.ENGLISH);

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be greater than start date");
        }

        List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();
        transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferToHistoryByUserId(userId));

        List<ResponseShowTransactionHistory> filteredHistories = transactionHistories.stream()
                .filter(history -> {
                    LocalDate transactionDate = history.getTransaction_time().toLocalDate();
                    return !transactionDate.isBefore(start) && !transactionDate.isAfter(end);
                })
                .collect(Collectors.toList());

        Collections.sort(filteredHistories,
                (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));

        return ResponseHandler.responseData(200,
                "Get " + user.getFirstName() + " " + user.getLastName()
                        + " transaction history by selected date range success",
                filteredHistories);
    }

    @Override
    public ResponseEntity<?> getIncomesAndExpensesAmountByUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();
        transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferToHistoryByUserId(userId));

        Collections.sort(transactionHistories,
                (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));

        Double incomeAmount = 0.;
        Double expenseAmount = 0.;

        for (ResponseShowTransactionHistory transactionHistory : transactionHistories) {
            if (transactionHistory.getType() == "Transfer to" || transactionHistory.getType().equals("Transfer to")) {
                expenseAmount += transactionHistory.getAmount();
            } else {
                incomeAmount += transactionHistory.getAmount();
            }
        }

        Map<String, Double> userIncomesExpensesAmount = new HashMap<>();
        userIncomesExpensesAmount.put("incomes", incomeAmount);
        userIncomesExpensesAmount.put("expenses", expenseAmount);

        return ResponseHandler.responseData(200,
                "Get " + user.getFirstName() + " " + user.getLastName()
                        + " user incomes and expenses amount success",
                userIncomesExpensesAmount);
    }

    @Override
    public ResponseEntity<?> getTransactionHistoryByUserIdAndDays(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User is not found"));

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User is not active or already deleted");
        }

        List<ResponseShowTransactionHistory> transactionHistories = new ArrayList<>();
        transactionHistories.addAll(transactionsRepository.getTopUpByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferFromHistoryByUserId(userId));
        transactionHistories.addAll(transactionsRepository.getTransferToHistoryByUserId(userId));

        Collections.sort(transactionHistories,
                (history1, history2) -> history2.getTransaction_time().compareTo(history1.getTransaction_time()));

        // map perhari
        Map<String, Map<String, Double>> dailyData = new LinkedHashMap<>();
        String[] daysOfWeek = { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY" };
        for (String day : daysOfWeek) {
            Map<String, Double> dayData = new HashMap<>();
            dayData.put("income", 0.0);
            dayData.put("expense", 0.0);
            dailyData.put(day, dayData);
        }

        // data perhari dari riwayat transaksi
        for (ResponseShowTransactionHistory transaction : transactionHistories) {
            String dayOfWeek = transaction.getTransaction_time().getDayOfWeek().toString();
            Map<String, Double> dayData = dailyData.get(dayOfWeek);
            if (transaction.getType().equals("Transfer to")) {
                dayData.put("expense", dayData.get("expense") + transaction.getAmount());
            } else {
                dayData.put("income", dayData.get("income") + transaction.getAmount());
            }
        }

        // map response nya sesuai format hari
        Map<String, Object> responseData = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> entry : dailyData.entrySet()) {
            String day = entry.getKey();
            Map<String, Double> dayData = entry.getValue();
            Map<String, Double> formattedData = new HashMap<>();
            formattedData.put("income", dayData.get("income"));
            formattedData.put("expense", dayData.get("expense"));
            responseData.put(day, formattedData);
        }
        return ResponseHandler.responseData(200, "Get daily income and expense for user success", responseData);
    }
}

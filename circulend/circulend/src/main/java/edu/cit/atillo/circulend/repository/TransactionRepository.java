package edu.cit.atillo.circulend.repository;

import edu.cit.atillo.circulend.entity.Transaction;
import edu.cit.atillo.circulend.entity.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserUserIdOrderByBorrowDateDesc(Long userId);
    Optional<Transaction> findByItemItemIdAndStatus(Long itemId, TransactionStatus status);
}

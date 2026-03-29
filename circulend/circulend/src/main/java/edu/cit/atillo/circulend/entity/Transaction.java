package edu.cit.atillo.circulend.entity;

import edu.cit.atillo.circulend.entity.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionStatus status;

    @PrePersist
    protected void onCreate() {
        if (borrowDate == null) borrowDate = LocalDateTime.now();
        if (status == null) status = TransactionStatus.ACTIVE;
    }
}
package edu.cit.atillo.circulend.service;

import edu.cit.atillo.circulend.dto.transaction.*;
import edu.cit.atillo.circulend.entity.AuditLog;
import edu.cit.atillo.circulend.entity.Item;
import edu.cit.atillo.circulend.entity.Transaction;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.AuditActionType;
import edu.cit.atillo.circulend.entity.enums.ItemStatus;
import edu.cit.atillo.circulend.entity.enums.TransactionStatus;
import edu.cit.atillo.circulend.exception.AuthException;
import edu.cit.atillo.circulend.repository.AuditLogRepository;
import edu.cit.atillo.circulend.repository.ItemRepository;
import edu.cit.atillo.circulend.repository.TransactionRepository;
import edu.cit.atillo.circulend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final EmailService emailService;

    @Transactional
    public BorrowResponseDTO borrow(BorrowRequestDTO req, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("AUTH-002", "Invalid or expired token", HttpStatus.UNAUTHORIZED));

        Item item = itemRepository.findById(req.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DB-001: Item not found"));

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "BUSINESS-001: Item is unavailable");
        }

        if (!item.getAssetTag().equalsIgnoreCase(req.getAssetTag().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALID-001: Asset tag mismatch");
        }

        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setItem(item);
        tx.setBorrowDate(LocalDateTime.now());
        tx.setStatus(TransactionStatus.ACTIVE);

        item.setStatus(ItemStatus.BORROWED);

        Transaction saved = transactionRepository.save(tx);
        itemRepository.save(item);

        boolean sent = emailService.sendBorrowReceipt(
                user.getEmail(),
                item.getName(),
                saved.getBorrowDate().toString()
        );
        writeSmtpAudit(user, sent,
                sent ? "Borrow receipt sent for tx " + saved.getTransactionId() +" ("+ item.getAssetTag()+")"
                        : "Borrow receipt failed for tx " + saved.getTransactionId() + " ("+ item.getAssetTag()+")");

        return new BorrowResponseDTO(
                saved.getTransactionId(),
                item.getItemId(),
                item.getName(),
                saved.getBorrowDate(),
                saved.getStatus().name()
        );
    }

    @Transactional
    public ReturnResponseDTO returnItem(Long transactionId, ReturnRequestDTO req, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("AUTH-002", "Invalid or expired token", HttpStatus.UNAUTHORIZED));

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DB-001: Transaction not found"));

        if (!tx.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "BUSINESS-002: Cannot return item not borrowed by you");
        }

        if (tx.getStatus() != TransactionStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "BUSINESS-001: Transaction is not active");
        }

        Item item = tx.getItem();
        if (!item.getAssetTag().equalsIgnoreCase(req.getAssetTag().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALID-001: Asset tag mismatch");
        }

        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setReturnDate(LocalDateTime.now());
        item.setStatus(ItemStatus.AVAILABLE);

        transactionRepository.save(tx);
        itemRepository.save(item);

        boolean sent = emailService.sendReturnReceipt(
                user.getEmail(),
                item.getName(),
                tx.getReturnDate().toString()
        );
        writeSmtpAudit(user, sent,
                sent ? "Return receipt sent for tx " + tx.getTransactionId() +" ("+ item.getAssetTag() +")"
                        : "Return receipt failed for tx " + tx.getTransactionId() +" ("+ item.getAssetTag() +")");

        return new ReturnResponseDTO(
                tx.getTransactionId(),
                tx.getReturnDate(),
                tx.getStatus().name()
        );
    }

    public List<UserTransactionDTO> getUserTransactions(Long userId) {
        return transactionRepository.findByUserUserIdOrderByBorrowDateDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<UserTransactionDTO> getAllTransactions() {
        return transactionRepository.findAllByOrderByBorrowDateDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private UserTransactionDTO toDto(Transaction tx) {
        return new UserTransactionDTO(
                tx.getTransactionId(),
                tx.getUser().getUserId(),
                tx.getItem().getItemId(),
                tx.getItem().getName(),
                tx.getItem().getAssetTag(),
                tx.getBorrowDate(),
                tx.getReturnDate(),
                tx.getStatus().name()
        );
    }

    private void writeSmtpAudit(User user, boolean sent, String description) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setActionType(sent ? AuditActionType.SMTP_SENT : AuditActionType.SMTP_FAILED);
        log.setDescription(description);
        auditLogRepository.save(log);
    }
}
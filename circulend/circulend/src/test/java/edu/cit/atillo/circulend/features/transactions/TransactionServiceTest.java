package edu.cit.atillo.circulend.features.transactions;

import edu.cit.atillo.circulend.entity.Item;
import edu.cit.atillo.circulend.entity.Transaction;
import edu.cit.atillo.circulend.entity.User;
import edu.cit.atillo.circulend.entity.enums.ItemStatus;
import edu.cit.atillo.circulend.entity.enums.Role;
import edu.cit.atillo.circulend.entity.enums.TransactionStatus;
import edu.cit.atillo.circulend.features.shared.service.EmailService;
import edu.cit.atillo.circulend.features.transactions.dto.BorrowRequestDTO;
import edu.cit.atillo.circulend.features.transactions.dto.ReturnRequestDTO;
import edu.cit.atillo.circulend.repository.AuditLogRepository;
import edu.cit.atillo.circulend.repository.ItemRepository;
import edu.cit.atillo.circulend.repository.TransactionRepository;
import edu.cit.atillo.circulend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void borrowThrowsWhenItemUnavailable() {
        User user = new User();
        user.setUserId(1L);
        user.setRole(Role.BORROWER);
        user.setEmail("u@x.com");

        Item item = new Item();
        item.setItemId(100L);
        item.setAssetTag("TAG-1");
        item.setStatus(ItemStatus.BORROWED);

        BorrowRequestDTO req = new BorrowRequestDTO();
        req.setItemId(100L);
        req.setAssetTag("TAG-1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> transactionService.borrow(req, 1L));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void borrowSuccessReturnsActiveStatus() {
        User user = new User();
        user.setUserId(1L);
        user.setRole(Role.BORROWER);
        user.setEmail("u@x.com");

        Item item = new Item();
        item.setItemId(100L);
        item.setName("Laptop");
        item.setAssetTag("TAG-1");
        item.setStatus(ItemStatus.AVAILABLE);

        BorrowRequestDTO req = new BorrowRequestDTO();
        req.setItemId(100L);
        req.setAssetTag("TAG-1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction tx = i.getArgument(0);
            tx.setTransactionId(500L);
            tx.setBorrowDate(LocalDateTime.now());
            return tx;
        });
        when(emailService.sendBorrowReceipt(anyString(), anyString(), anyString())).thenReturn(true);

        var result = transactionService.borrow(req, 1L);
        assertEquals(500L, result.getTransactionId());
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void returnItemThrowsWhenBorrowerMismatch() {
        User borrower = new User();
        borrower.setUserId(10L);
        borrower.setEmail("owner@x.com");

        User current = new User();
        current.setUserId(20L);

        Item item = new Item();
        item.setAssetTag("TAG-1");

        Transaction tx = new Transaction();
        tx.setTransactionId(99L);
        tx.setUser(borrower);
        tx.setItem(item);
        tx.setStatus(TransactionStatus.ACTIVE);

        ReturnRequestDTO req = new ReturnRequestDTO();
        req.setAssetTag("TAG-1");

        when(userRepository.findById(20L)).thenReturn(Optional.of(current));
        when(transactionRepository.findById(99L)).thenReturn(Optional.of(tx));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.returnItem(99L, req, 20L)
        );
        assertEquals(403, ex.getStatusCode().value());
    }
}

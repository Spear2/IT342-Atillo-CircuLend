package edu.cit.atillo.circulend.entity;

import edu.cit.atillo.circulend.entity.enums.*;
import edu.cit.atillo.circulend.features.auditlogs.dto.AuditLogResponseDTO;
import edu.cit.atillo.circulend.features.auth.AuthException;
import edu.cit.atillo.circulend.features.auth.dto.RegisterDTO;
import edu.cit.atillo.circulend.features.inventory.dto.CreateItemRequest;
import edu.cit.atillo.circulend.features.inventory.dto.ItemResponseDTO;
import edu.cit.atillo.circulend.features.transactions.dto.BorrowRequestDTO;
import edu.cit.atillo.circulend.features.transactions.dto.ReturnRequestDTO;
import edu.cit.atillo.circulend.features.users.dto.UserResponseDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityAndDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void registerDtoValidationTriggersRequiredErrors() {
        RegisterDTO dto = new RegisterDTO();
        dto.setEmail("invalid-email");
        dto.setPassword("123");

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void itemAndTransactionRequestDtoValidationTriggersErrors() {
        CreateItemRequest create = new CreateItemRequest();
        BorrowRequestDTO borrow = new BorrowRequestDTO();
        ReturnRequestDTO ret = new ReturnRequestDTO();

        assertFalse(validator.validate(create).isEmpty());
        assertFalse(validator.validate(borrow).isEmpty());
        assertFalse(validator.validate(ret).isEmpty());
    }

    @Test
    void dtoMappersMapFields() {
        User user = new User();
        user.setUserId(1L);
        user.setFirstName("Ana");
        user.setLastName("Atillo");
        user.setEmail("ana@example.com");
        user.setRole(Role.BORROWER);
        user.setCreatedAt(LocalDateTime.now());

        UserResponseDTO userDto = UserResponseDTO.fromUser(user);
        assertEquals("ana@example.com", userDto.getEmail());

        Category category = new Category();
        category.setCategoryId(10L);
        category.setName("Electronics");
        Item item = new Item();
        item.setItemId(5L);
        item.setName("Laptop");
        item.setAssetTag("TAG-1");
        item.setStatus(ItemStatus.AVAILABLE);
        item.setCategory(category);
        ItemResponseDTO itemDto = ItemResponseDTO.from(item);
        assertEquals("AVAILABLE", itemDto.getStatus());
        assertEquals(10L, itemDto.getCategoryId());

        AuditLog log = new AuditLog();
        log.setAuditId(7L);
        log.setUser(user);
        log.setActionType(AuditActionType.SMTP_SENT);
        log.setDescription("sent");
        log.setTimestamp(LocalDateTime.now());
        AuditLogResponseDTO logDto = AuditLogResponseDTO.from(log);
        assertEquals("SMTP_SENT", logDto.getActionType());
    }

    @Test
    void entityLifecycleMethodsSetDefaults() throws Exception {
        User user = new User();
        invokeOnCreate(user);
        assertNotNull(user.getCreatedAt());

        Item item = new Item();
        invokeOnCreate(item);
        assertNotNull(item.getCreatedAt());

        Transaction tx = new Transaction();
        invokeOnCreate(tx);
        assertNotNull(tx.getBorrowDate());
        assertEquals(TransactionStatus.ACTIVE, tx.getStatus());

        AuditLog log = new AuditLog();
        invokeOnCreate(log);
        assertNotNull(log.getTimestamp());
    }

    @Test
    void enumsAndAuthExceptionAreUsable() {
        assertNotNull(Role.valueOf("ADMIN"));
        assertNotNull(AuthProvider.valueOf("LOCAL"));
        assertNotNull(ItemStatus.valueOf("AVAILABLE"));
        assertNotNull(TransactionStatus.valueOf("ACTIVE"));
        assertNotNull(AuditActionType.valueOf("SMTP_SENT"));

        AuthException ex = new AuthException("AUTH-001", "Invalid", HttpStatus.UNAUTHORIZED);
        assertEquals("AUTH-001", ex.getCode());
    }

    private void invokeOnCreate(Object target) throws Exception {
        Method m = target.getClass().getDeclaredMethod("onCreate");
        m.setAccessible(true);
        m.invoke(target);
    }
}

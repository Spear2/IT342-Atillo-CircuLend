package edu.cit.atillo.circulend.features;

import edu.cit.atillo.circulend.features.auditlogs.AdminAuditLogController;
import edu.cit.atillo.circulend.features.auditlogs.AuditLogService;
import edu.cit.atillo.circulend.features.auth.AuthController;
import edu.cit.atillo.circulend.features.auth.AuthService;
import edu.cit.atillo.circulend.features.auth.dto.LoginRequestDTO;
import edu.cit.atillo.circulend.features.auth.dto.RegisterDTO;
import edu.cit.atillo.circulend.features.auth.dto.ResendVerificationRequestDTO;
import edu.cit.atillo.circulend.features.inventory.AdminItemController;
import edu.cit.atillo.circulend.features.inventory.ItemController;
import edu.cit.atillo.circulend.features.inventory.ItemService;
import edu.cit.atillo.circulend.features.inventory.dto.CreateItemRequest;
import edu.cit.atillo.circulend.features.inventory.dto.UpdateItemRequest;
import edu.cit.atillo.circulend.features.shared.dto.PagedResponseDTO;
import edu.cit.atillo.circulend.features.transactions.AdminTransactionController;
import edu.cit.atillo.circulend.features.transactions.TransactionController;
import edu.cit.atillo.circulend.features.transactions.TransactionService;
import edu.cit.atillo.circulend.features.transactions.dto.BorrowRequestDTO;
import edu.cit.atillo.circulend.features.transactions.dto.ReturnRequestDTO;
import edu.cit.atillo.circulend.features.users.AdminUserController;
import edu.cit.atillo.circulend.features.users.UserController;
import edu.cit.atillo.circulend.features.users.UserService;
import edu.cit.atillo.circulend.security.CurrentUserService;
import edu.cit.atillo.circulend.security.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ControllerUnitTest {

    @Test
    void authControllerDelegatesCalls() {
        AuthService authService = mock(AuthService.class);
        TokenProvider tokenProvider = mock(TokenProvider.class);
        AuthController controller = new AuthController(authService, tokenProvider);

        when(authService.registration(any(RegisterDTO.class))).thenReturn(null);
        when(authService.login(any(LoginRequestDTO.class))).thenReturn(null);
        when(authService.verifyEmail("abc")).thenReturn(null);
        when(authService.resendVerification(any(ResendVerificationRequestDTO.class))).thenReturn(null);

        assertEquals(200, controller.register(new RegisterDTO()).getStatusCode().value());
        assertEquals(200, controller.login(new LoginRequestDTO()).getStatusCode().value());
        assertEquals(200, controller.verifyEmail("abc").getStatusCode().value());
        assertEquals(200, controller.resendVerification(new ResendVerificationRequestDTO()).getStatusCode().value());
    }

    @Test
    void userControllersDelegateCalls() {
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);
        AdminUserController adminUserController = new AdminUserController(userService);

        when(userService.getCurrentUser()).thenReturn(null);
        when(userService.listUsers(anyInt(), anyInt())).thenReturn(List.of());

        assertEquals(200, userController.me().getStatusCode().value());
        assertEquals(200, adminUserController.ping().getStatusCode().value());
        assertEquals(200, adminUserController.listUsers(0, 20).getStatusCode().value());
    }

    @Test
    void itemControllersDelegateCalls() {
        ItemService itemService = mock(ItemService.class);
        ItemController itemController = new ItemController(itemService);
        AdminItemController adminItemController = new AdminItemController(itemService);

        when(itemService.searchItems(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new PagedResponseDTO<>(List.of(), 0, 10, 0L, 0, true));
        when(itemService.getItemById(1L)).thenReturn(null);
        when(itemService.createItem(any(CreateItemRequest.class), any())).thenReturn(null);
        when(itemService.updateItem(eq(1L), any(UpdateItemRequest.class), any())).thenReturn(null);

        MockMultipartFile file = new MockMultipartFile("imageFile", "x.png", "image/png", new byte[]{1});

        assertEquals(200, itemController.getItems(null, null, null, 0, 12).getStatusCode().value());
        assertEquals(200, itemController.getItemById(1L).getStatusCode().value());
        assertEquals(200, adminItemController.createItem(new CreateItemRequest(), file).getStatusCode().value());
        assertEquals(200, adminItemController.updateItem(1L, new UpdateItemRequest(), file).getStatusCode().value());
        assertEquals(200, adminItemController.deleteItem(1L).getStatusCode().value());
    }

    @Test
    void transactionAndAuditControllersDelegateCalls() {
        TransactionService transactionService = mock(TransactionService.class);
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        TransactionController transactionController = new TransactionController(transactionService, currentUserService);
        AdminTransactionController adminTransactionController = new AdminTransactionController(transactionService);

        AuditLogService auditLogService = mock(AuditLogService.class);
        AdminAuditLogController adminAuditLogController = new AdminAuditLogController(auditLogService);

        when(currentUserService.getCurrentUserId()).thenReturn(5L);
        when(transactionService.borrow(any(BorrowRequestDTO.class), eq(5L))).thenReturn(null);
        when(transactionService.returnItem(eq(1L), any(ReturnRequestDTO.class), eq(5L))).thenReturn(null);
        when(transactionService.getUserTransactions(5L)).thenReturn(List.of());
        when(transactionService.getAllTransactions()).thenReturn(List.of());
        when(auditLogService.listLogs(anyInt(), anyInt())).thenReturn(List.of());

        assertEquals(200, transactionController.borrow(new BorrowRequestDTO()).getStatusCode().value());
        assertEquals(200, transactionController.returnItem(1L, new ReturnRequestDTO()).getStatusCode().value());
        assertEquals(200, transactionController.getUserTransactions().getStatusCode().value());
        assertEquals(200, adminTransactionController.getAllTransactions(0, 20).getStatusCode().value());
        assertEquals(200, adminAuditLogController.listLogs(0, 20).getStatusCode().value());
    }
}

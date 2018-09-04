package io.fourfinanceit.app.controller;

import io.fourfinanceit.app.exception.*;
import io.fourfinanceit.app.model.LoanInfo;
import io.fourfinanceit.app.model.domain.ExtendedLoan;
import io.fourfinanceit.app.model.domain.Loan;
import io.fourfinanceit.app.model.domain.User;
import io.fourfinanceit.app.model.forms.ExtendLoanForm;
import io.fourfinanceit.app.model.forms.NewLoanForm;
import io.fourfinanceit.app.model.forms.ReturnLoanAmountForm;
import io.fourfinanceit.app.service.LoanService;
import io.fourfinanceit.app.service.LoggedRemoteAddressOfRequestService;
import io.fourfinanceit.app.service.UserService;
import io.fourfinanceit.app.utils.MyAppConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class LoanControllerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    private UserService userService;

    @Mock
    private LoanService loanService;

    @Mock
    private LoggedRemoteAddressOfRequestService remoteAddressService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LoanController loanController;

    private Loan loan;
    private List<Loan> loans;
    private ExtendedLoan extendedLoan;
    private ReturnLoanAmountForm returnLoanAmountForm;
    private LoanInfo loanInfo;
    private NewLoanForm newLoanForm;
    private ExtendLoanForm extendLoanForm;
    private User user;


    private static final Long LOAN_ID = 1L;
    private static final Long EXTENDED_LOAN_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final String REMOTE_ADDRESS = "0.0.0.0.0";

    @Before
    public void setUp() {
        loan = new Loan();
        loan.setId(LOAN_ID);
        loan.setStatus(MyAppConstants.STATUS_APPROVED);

        extendedLoan = new ExtendedLoan();
        extendedLoan.setId(EXTENDED_LOAN_ID);
        extendedLoan.setStatus(MyAppConstants.STATUS_APPROVED);

        returnLoanAmountForm = new ReturnLoanAmountForm();
        loanInfo = new LoanInfo();
        newLoanForm = new NewLoanForm();
        extendLoanForm = new ExtendLoanForm();
        user = new User();
        loans = new ArrayList<>();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testApproveLoan() {
        when(loanService.findLoan(anyLong())).thenReturn(Optional.ofNullable(loan));
        loan.setStatus(MyAppConstants.STATUS_WAITING_FOR_APPROVAL);

        loanController.approveLoan(LOAN_ID);

        verify(loanService, times(1)).approveLoan(anyLong());
    }

    @Test
    public void testApproveLoanIfAlreadyApproved() {
        when(loanService.findLoan(anyLong())).thenReturn(Optional.ofNullable(loan));
        loan.setStatus(MyAppConstants.STATUS_APPROVED);

        exception.expect(LoanAlreadyApprovedException.class);
        exception.expectMessage("Loan is already approved");

        loanController.approveLoan(LOAN_ID);
    }

    @Test
    public void testApproveExtendedLoan() {
        when(loanService.findLoan(anyLong())).thenReturn(Optional.ofNullable(loan));
        extendedLoan.setStatus(MyAppConstants.STATUS_WAITING_FOR_APPROVAL);
        loan.setExtendedLoan(extendedLoan);

        loanController.approveExtendedLoan(EXTENDED_LOAN_ID);

        verify(loanService, times(1)).approveExtendedLoan(anyLong());
    }

    @Test
    public void testApproveExtendedLoanWhenLoanNotExtended() {
        when(loanService.findLoan(anyLong())).thenReturn(Optional.ofNullable(loan));

        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage("Extended Loan not found");

        loanController.approveExtendedLoan(EXTENDED_LOAN_ID);
    }

    @Test
    public void testApproveExtendedLoanWhenAlreadyApproved() {
        when(loanService.findLoan(anyLong())).thenReturn(Optional.ofNullable(loan));
        extendedLoan.setStatus(MyAppConstants.STATUS_APPROVED);
        loan.setExtendedLoan(extendedLoan);

        exception.expect(LoanAlreadyApprovedException.class);
        exception.expectMessage("Loan is already approved");

        loanController.approveExtendedLoan(EXTENDED_LOAN_ID);
    }

    @Test
    public void testReturnLoanAmount() {
        returnLoanAmountForm.setUserId(USER_ID);
        returnLoanAmountForm.setLoanId(LOAN_ID);
        returnLoanAmountForm.setAmount(500d);
        loanInfo.setStatus(MyAppConstants.STATUS_APPROVED);
        loanInfo.setAmountToReturn(500d);
        loanInfo.setAmountReturned(0d);

        when(loanService.getLoanInfo(anyLong())).thenReturn(Optional.ofNullable(loanInfo));

        loanController.returnLoanAmount(returnLoanAmountForm);

        verify(loanService, times(1)).returnLoanAmount(anyLong(),anyDouble(), anyBoolean());
    }

    @Test
    public void testReturnLoanAmountNotFound() {
        returnLoanAmountForm.setUserId(USER_ID);
        returnLoanAmountForm.setLoanId(LOAN_ID);
        returnLoanAmountForm.setAmount(500d);

        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage("Loan not found");

        loanController.returnLoanAmount(returnLoanAmountForm);
    }

    @Test
    public void testReturnLoanAmountWhenLoanNotApproved() {
        returnLoanAmountForm.setUserId(USER_ID);
        returnLoanAmountForm.setLoanId(LOAN_ID);
        returnLoanAmountForm.setAmount(500d);
        loanInfo.setStatus(MyAppConstants.STATUS_WAITING_FOR_APPROVAL);

        when(loanService.getLoanInfo(anyLong())).thenReturn(Optional.ofNullable(loanInfo));

        exception.expect(LoanNotApprovedException.class);
        exception.expectMessage("Refused, because loan is not approved");

        loanController.returnLoanAmount(returnLoanAmountForm);
    }

    @Test
    public void testReturnLoanAmountWhenReturningMoreThenNeeded() {
        returnLoanAmountForm.setUserId(USER_ID);
        returnLoanAmountForm.setLoanId(LOAN_ID);
        returnLoanAmountForm.setAmount(500d);
        loanInfo.setStatus(MyAppConstants.STATUS_APPROVED);
        loanInfo.setAmountToReturn(420d);
        loanInfo.setAmountReturned(0d);

        when(loanService.getLoanInfo(anyLong())).thenReturn(Optional.ofNullable(loanInfo));

        exception.expect(LoanAmountReturnExceeds.class);
        exception.expectMessage("Requested amount to return: '500.0' exceeds the required: '420.0'");

        loanController.returnLoanAmount(returnLoanAmountForm);
    }

    @Test
    public void testGetLoan() {
        when(loanService.findLoan(anyLong())).thenReturn(Optional.ofNullable(loan));

        loanController.getLoan(LOAN_ID);

        verify(loanService, times(1)).findLoan(anyLong());
    }

    @Test
    public void testGetLoanIfLoanNotFound() {
        when(loanService.findLoan(anyLong())).thenReturn(Optional.empty());

        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage("Loan not found");

        loanController.getLoan(LOAN_ID);
    }

    @Test
    public void testGetUserLoans() {
        loanService.getInformationAboutUserLoans(USER_ID);

        verify(loanService, times(1)).getInformationAboutUserLoans(anyLong());
    }

    @Test
    public void testNewLoan() {
        newLoanForm.setUserId(USER_ID);
        newLoanForm.setAmount(200d);
        newLoanForm.setTermInDays(30);
        loans.add(loan);

        when(request.getRemoteAddr()).thenReturn(REMOTE_ADDRESS);
        when(userService.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(loanService.getLoansByUserIdWithoutClosedLoans(anyLong())).thenReturn(loans);
        when(remoteAddressService.checkIfRequestLimitForRemoteAddressExceeded(
                anyString(),
                anyList(),
                anyInt(),
                anyInt()
        )).thenReturn(false);

        loanController.newLoan(request, newLoanForm);

        verify(loanService, times(1)).createLoan(any());
        verify(remoteAddressService, times(1)).logRequestRemoteAddress(anyString(), anyString());
    }

    @Test
    public void testNewLoanWhenUserNotFound() {
        newLoanForm.setUserId(USER_ID);
        newLoanForm.setAmount(200d);
        newLoanForm.setTermInDays(30);
        loans.add(loan);

        when(request.getRemoteAddr()).thenReturn(REMOTE_ADDRESS);
        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage("User not found");

        loanController.newLoan(request, newLoanForm);
    }

    @Test
    public void testNewLoanWhenLoansPerUserExceeded() {
        newLoanForm.setUserId(USER_ID);
        newLoanForm.setAmount(200d);
        newLoanForm.setTermInDays(30);
        loans.add(loan);
        loans.add(loan);
        loans.add(loan);
        loans.add(loan);
        loans.add(loan);
        loans.add(loan);

        when(request.getRemoteAddr()).thenReturn(REMOTE_ADDRESS);
        when(userService.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(loanService.getLoansByUserIdWithoutClosedLoans(anyLong())).thenReturn(loans);
        when(remoteAddressService.checkIfRequestLimitForRemoteAddressExceeded(
                anyString(),
                anyList(),
                anyInt(),
                anyInt()
        )).thenReturn(false);

        exception.expect(LoanLimitPerUserExceededException.class);
        exception.expectMessage("Loan limit per one user: '5' exceeded");

        loanController.newLoan(request, newLoanForm);
    }

    @Test
    public void testNewLoanWhenUserLoanRequestsPerDayExceeded() {
        newLoanForm.setUserId(USER_ID);
        newLoanForm.setAmount(200d);
        newLoanForm.setTermInDays(30);
        loans.add(loan);

        when(request.getRemoteAddr()).thenReturn(REMOTE_ADDRESS);
        when(userService.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(loanService.getLoansByUserIdWithoutClosedLoans(anyLong())).thenReturn(loans);
        when(remoteAddressService.checkIfRequestLimitForRemoteAddressExceeded(
                anyString(),
                anyList(),
                anyInt(),
                anyInt()
        )).thenReturn(true);

        exception.expect(RequestsForRemoteAddressExceededException.class);
        exception.expectMessage("Requests for remote address: '0.0.0.0.0' exceeded limit: '3'");

        loanController.newLoan(request, newLoanForm);
    }

    @Test
    public void testExtendLoan() {
        extendLoanForm.setUserId(USER_ID);
        extendLoanForm.setLoanToExtendId(LOAN_ID);
        extendLoanForm.setTermInDays(30);
        loan.setStatus(MyAppConstants.STATUS_APPROVED);

        when(userService.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(loanService.findLoan(anyLong())).thenReturn(Optional.ofNullable(loan));

        loanController.extendLoan(extendLoanForm);

        verify(loanService, times(1)).extendLoan(any(), any());
    }

    @Test
    public void testExtendLoanWhenUserNotFound() {
        extendLoanForm.setUserId(USER_ID);
        extendLoanForm.setLoanToExtendId(LOAN_ID);
        extendLoanForm.setTermInDays(30);
        loan.setStatus(MyAppConstants.STATUS_APPROVED);

        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage("User not found");

        loanController.extendLoan(extendLoanForm);
    }

    @Test
    public void testExtendLoanWhenLoanNotFound() {
        extendLoanForm.setUserId(USER_ID);
        extendLoanForm.setLoanToExtendId(LOAN_ID);
        extendLoanForm.setTermInDays(30);
        loan.setStatus(MyAppConstants.STATUS_WAITING_FOR_APPROVAL);

        when(userService.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(loanService.findLoan(anyLong())).thenReturn(Optional.empty());

        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage("Loan not found");

        loanController.extendLoan(extendLoanForm);
    }

    @Test
    public void testExtendLoanWhenLoanNotApproved() {
        extendLoanForm.setUserId(USER_ID);
        extendLoanForm.setLoanToExtendId(LOAN_ID);
        extendLoanForm.setTermInDays(30);
        loan.setStatus(MyAppConstants.STATUS_WAITING_FOR_APPROVAL);

        when(userService.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(loanService.findLoan(anyLong())).thenReturn(Optional.ofNullable(loan));

        exception.expect(LoanNotApprovedException.class);
        exception.expectMessage("Refused, because loan is not approved");

        loanController.extendLoan(extendLoanForm);
    }
}

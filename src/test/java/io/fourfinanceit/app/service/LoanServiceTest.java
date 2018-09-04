package io.fourfinanceit.app.service;

import io.fourfinanceit.app.model.forms.ExtendLoanForm;
import io.fourfinanceit.app.model.LoanInfo;
import io.fourfinanceit.app.model.forms.NewLoanForm;
import io.fourfinanceit.app.model.domain.ExtendedLoan;
import io.fourfinanceit.app.model.domain.Loan;
import io.fourfinanceit.app.repository.ExtendedLoanRepository;
import io.fourfinanceit.app.repository.LoanRepository;
import io.fourfinanceit.app.utils.MyAppConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ExtendedLoanRepository extendedLoanRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan loan;
    private List<Loan> loans;
    private Date currentDate;

    public static final Long LOAN_ID = 1L;

    @Before
    public void setUp() {
        loan = new Loan();
        loans = new ArrayList<>();
        loans.add(loan);
        loans.add(loan);
        loans.add(loan);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindLoanWhenLoanFound() {
        when(loanRepository.findById(anyLong())).thenReturn(Optional.ofNullable(loan));

        Optional<Loan> actual = loanService.findLoan(LOAN_ID);

        assertNotNull(actual);
        assertEquals(loan, actual.get());

        verify(loanRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testFindLoanWhenLoanNotFound() {
        when(loanRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Loan> actual = loanService.findLoan(LOAN_ID);

        assertNotNull(actual);
        assertFalse(actual.isPresent());

        verify(loanRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testApproveLoan() {
        loanService.approveLoan(LOAN_ID);

        verify(loanRepository, times(1)).changeLoanStatus(anyLong(), anyString());
    }

    @Test
    public void testApproveExtendedLoan() {
        loanService.approveExtendedLoan(LOAN_ID);

        verify(extendedLoanRepository, times(1)).changeExtendedLoanStatus(anyLong(), anyString());
    }

    @Test
    public void testGetLoanByIdWithoutClosedLoansWhenLoanFound() {
        when(loanRepository.findLoanByIdAndStatusNot(anyLong(), anyString())).thenReturn(Optional.ofNullable(loan));

        Optional<Loan> actual = loanService.getLoanByIdWithoutClosedLoans(LOAN_ID);

        assertNotNull(actual);
        assertEquals(loan, actual.get());

        verify(loanRepository, times(1)).findLoanByIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetLoanByIdWithoutClosedLoansWhenLoanNotFound() {
        when(loanRepository.findLoanByIdAndStatusNot(anyLong(), anyString())).thenReturn(Optional.empty());

        Optional<Loan> actual = loanService.getLoanByIdWithoutClosedLoans(LOAN_ID);

        assertNotNull(actual);
        assertFalse(actual.isPresent());

        verify(loanRepository, times(1)).findLoanByIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetLoansByUserIdWithoutClosedLoansWhenLoansFound() {
        when(loanRepository.findLoansByUserIdAndStatusNot(anyLong(), anyString())).thenReturn(loans);

        List<Loan> actual = loanService.getLoansByUserIdWithoutClosedLoans(LOAN_ID);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(loans, actual);

        verify(loanRepository, times(1)).findLoansByUserIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetLoansByUserIdWithoutClosedLoansWhenLoansNotFound() {
        when(loanRepository.findLoansByUserIdAndStatusNot(anyLong(), anyString())).thenReturn(Collections.emptyList());

        List<Loan> actual = loanService.getLoansByUserIdWithoutClosedLoans(LOAN_ID);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());

        verify(loanRepository, times(1)).findLoansByUserIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetLoanInfoWithCorrectInterestFactorNotFound() {
        when(loanRepository.findLoanByIdAndStatusNot(anyLong(), anyString())).thenReturn(Optional.empty());

        Optional<LoanInfo> actual = loanService.getLoanInfo(LOAN_ID);

        assertNotNull(actual);
        assertFalse(actual.isPresent());

        verify(loanRepository, times(1)).findLoanByIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetLoanInfoWithCorrectInterestFactor() {
        LocalDate loanStartDate = LocalDate.now().minusDays(18);
        LocalDate loanEndDate = LocalDate.now().plusDays(3);

        populateLoanData();
        loan.setLoanStartDate(localDateToDate(loanStartDate));
        loan.setLoanEndDate(localDateToDate(loanEndDate));
        loan.setCreatedAt(localDateToDate(loanStartDate));
        loan.setUpdatedAt(localDateToDate(loanStartDate));

        when(loanRepository.findLoanByIdAndStatusNot(anyLong(), anyString())).thenReturn(Optional.ofNullable(loan));

        Optional<LoanInfo> actual = loanService.getLoanInfo(LOAN_ID);

        assertNotNull(actual);
        Double expectedInterestAmount = 15d;
        assertEquals(expectedInterestAmount, actual.get().getInterestAmount());
        assertEquals(expectedInterestAmount, actual.get().getTotalInterestAmount());

        verify(loanRepository, times(1)).findLoanByIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetLoanInfoWhenFoundOverdueLoanWithCorrectInterestFactor() {
        LocalDate loanStartDate = LocalDate.now().minusDays(28);
        LocalDate loanEndDate = LocalDate.now().minusDays(14);

        populateLoanData();
        loan.setLoanStartDate(localDateToDate(loanStartDate));
        loan.setLoanEndDate(localDateToDate(loanEndDate));
        loan.setCreatedAt(localDateToDate(loanStartDate));
        loan.setUpdatedAt(localDateToDate(loanStartDate));

        when(loanRepository.findLoanByIdAndStatusNot(anyLong(), anyString())).thenReturn(Optional.ofNullable(loan));

        Optional<LoanInfo> actual = loanService.getLoanInfo(LOAN_ID);

        assertNotNull(actual);

        Double expectedInterestAmount = 10d;
        Double expectedOverdueInterestAmount = 30d;
        Double totalInterestAmount = expectedInterestAmount + expectedOverdueInterestAmount;

        assertEquals(expectedInterestAmount, actual.get().getInterestAmount());
        assertEquals(expectedOverdueInterestAmount, actual.get().getOverdueLoanInfo().getInterestAmount());
        assertEquals(totalInterestAmount, actual.get().getTotalInterestAmount());

        verify(loanRepository, times(1)).findLoanByIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetLoanInfoWhenLoanExtendedWithCorrectInterestFactor() {
    LocalDate loanStartDate = LocalDate.now().minusDays(21);
    LocalDate loanEndDate = LocalDate.now().plusDays(7);
    LocalDate extendedLoanEndDate = LocalDate.now().plusDays(14);

    populateLoanData();
        loan.setLoanStartDate(localDateToDate(loanStartDate));
        loan.setLoanEndDate(localDateToDate(loanEndDate));

    ExtendedLoan extendedLoan = new ExtendedLoan();
        extendedLoan.setId(1L);
        extendedLoan.setInterestFactor(MyAppConstants.EXTENDED_INTEREST_FACTOR);
        extendedLoan.setLoanEndDate(localDateToDate(extendedLoanEndDate));
        extendedLoan.setStatus(MyAppConstants.STATUS_APPROVED);

        loan.setExtendedLoan(extendedLoan);

    when(loanRepository.findLoanByIdAndStatusNot(anyLong(), anyString())).thenReturn(Optional.ofNullable(loan));

    Optional<LoanInfo> actual = loanService.getLoanInfo(LOAN_ID);

    assertNotNull(actual);

    Double expectedInterestAmount = 20d;
    Double expectedExtendedInterestAmount = 7.5;
    Double totalInterestAmount = expectedInterestAmount + expectedExtendedInterestAmount;

    assertEquals(expectedInterestAmount, actual.get().getInterestAmount());
    assertEquals(expectedExtendedInterestAmount, actual.get().getExtendedLoanInfo().getInterestAmount());
    assertEquals(totalInterestAmount, actual.get().getTotalInterestAmount());

    verify(loanRepository, times(1)).findLoanByIdAndStatusNot(anyLong(), anyString());
}

    @Test
    public void testGetLoanInfoWhenLoanExtendedButNotApprovedWithCorrectInterestFactor() {
        LocalDate loanStartDate = LocalDate.now().minusDays(21);
        LocalDate loanEndDate = LocalDate.now().plusDays(7);
        LocalDate extendedLoanEndDate = LocalDate.now().plusDays(14);

        populateLoanData();
        loan.setLoanStartDate(localDateToDate(loanStartDate));
        loan.setLoanEndDate(localDateToDate(loanEndDate));

        ExtendedLoan extendedLoan = new ExtendedLoan();
        extendedLoan.setId(1L);
        extendedLoan.setInterestFactor(MyAppConstants.EXTENDED_INTEREST_FACTOR);
        extendedLoan.setLoanEndDate(localDateToDate(extendedLoanEndDate));
        extendedLoan.setStatus(MyAppConstants.STATUS_WAITING_FOR_APPROVAL);

        loan.setExtendedLoan(extendedLoan);

        when(loanRepository.findLoanByIdAndStatusNot(anyLong(), anyString())).thenReturn(Optional.ofNullable(loan));

        Optional<LoanInfo> actual = loanService.getLoanInfo(LOAN_ID);

        assertNotNull(actual);

        Double expectedInterestAmount = 20d;
        Double expectedExtendedInterestAmount = 7.5;
        Double totalInterestAmount = expectedInterestAmount + expectedExtendedInterestAmount;

        assertEquals(expectedInterestAmount, actual.get().getInterestAmount());
        assertEquals(expectedExtendedInterestAmount, actual.get().getExtendedLoanInfo().getInterestAmount());
        assertNotEquals(totalInterestAmount, actual.get().getTotalInterestAmount());
        assertEquals(expectedInterestAmount, actual.get().getTotalInterestAmount());

        verify(loanRepository, times(1)).findLoanByIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetLoanInfoWhenLoanExtendedAndOverdueWithCorrectInterestFactor() {
        LocalDate loanStartDate = LocalDate.now().minusDays(28);
        LocalDate loanEndDate = LocalDate.now().minusDays(14);
        LocalDate extendedLoanEndDate = LocalDate.now().minusDays(7);

        populateLoanData();
        loan.setLoanStartDate(localDateToDate(loanStartDate));
        loan.setLoanEndDate(localDateToDate(loanEndDate));

        ExtendedLoan extendedLoan = new ExtendedLoan();
        extendedLoan.setId(1L);
        extendedLoan.setInterestFactor(MyAppConstants.EXTENDED_INTEREST_FACTOR);
        extendedLoan.setLoanEndDate(localDateToDate(extendedLoanEndDate));
        extendedLoan.setStatus(MyAppConstants.STATUS_APPROVED);

        loan.setExtendedLoan(extendedLoan);

        when(loanRepository.findLoanByIdAndStatusNot(anyLong(), anyString())).thenReturn(Optional.ofNullable(loan));

        Optional<LoanInfo> actual = loanService.getLoanInfo(LOAN_ID);

        assertNotNull(actual);

        Double expectedInterestAmount = 10d;
        Double expectedExtendedInterestAmount = 7.5;
        Double expectedOverdueInterestAmount = 15d;
        Double totalInterestAmount = expectedInterestAmount + expectedExtendedInterestAmount + expectedOverdueInterestAmount;

        assertEquals(expectedInterestAmount, actual.get().getInterestAmount());
        assertEquals(expectedExtendedInterestAmount, actual.get().getExtendedLoanInfo().getInterestAmount());
        assertEquals(expectedOverdueInterestAmount, actual.get().getOverdueLoanInfo().getInterestAmount());
        assertEquals(totalInterestAmount, actual.get().getTotalInterestAmount());

        verify(loanRepository, times(1)).findLoanByIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetInformationAboutUserLoans() {
        LocalDate loanStartDate = LocalDate.now().minusDays(18);
        LocalDate loanEndDate = LocalDate.now().plusDays(3);

        populateLoanData();
        loan.setLoanStartDate(localDateToDate(loanStartDate));
        loan.setLoanEndDate(localDateToDate(loanEndDate));
        loan.setCreatedAt(localDateToDate(loanStartDate));
        loan.setUpdatedAt(localDateToDate(loanStartDate));

        List<Loan> userLoans = new ArrayList<>();
        userLoans.add(loan);
        userLoans.add(loan);

        when(loanRepository.findLoansByUserIdAndStatusNot(anyLong(), anyString())).thenReturn(userLoans);

        List<LoanInfo> actual = loanService.getInformationAboutUserLoans(LOAN_ID);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        Double expectedInterestAmount = 15d;
        assertEquals(expectedInterestAmount, actual.get(0).getInterestAmount());
        assertEquals(expectedInterestAmount, actual.get(0).getTotalInterestAmount());
        assertEquals(expectedInterestAmount, actual.get(1).getInterestAmount());
        assertEquals(expectedInterestAmount, actual.get(1).getTotalInterestAmount());

        verify(loanRepository, times(1)).findLoansByUserIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testGetInformationAboutUserLoansNotFound() {
        when(loanRepository.findLoansByUserIdAndStatusNot(anyLong(), anyString())).thenReturn(Collections.EMPTY_LIST);

        List<LoanInfo> actual = loanService.getInformationAboutUserLoans(LOAN_ID);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());

        verify(loanRepository, times(1)).findLoansByUserIdAndStatusNot(anyLong(), anyString());
    }

    @Test
    public void testReturnDaysOverdueIfPresentIfLoanEndDateToday() {
        currentDate = new Date();

        Optional<Integer> actual = loanService.returnDaysOverdueIfPresent(currentDate);

        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }

    @Test
    public void testReturnDaysOverdueIfPresentIfLoanEndDateNextDay() {
        LocalDate nextDaysDate = LocalDate.now().plusDays(1);

        Optional<Integer> actual = loanService.returnDaysOverdueIfPresent(localDateToDate(nextDaysDate));

        assertNotNull(actual);
        assertFalse(actual.isPresent());
    }

    @Test
    public void testReturnDaysOverdueIfPresentIfLoanEndDateWasYesterday() {
        LocalDate yesterdaysDate = LocalDate.now().minusDays(1);

        Optional<Integer> actual = loanService.returnDaysOverdueIfPresent(localDateToDate(yesterdaysDate));

        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(new Integer(1), actual.get());
    }

    @Test
    public void testIsNumberBetweenWhenNumberBetween() {
        boolean actual = loanService.isNumberBetween(5, 1, 8);

        assertTrue(actual);
    }

    @Test
    public void testIsNumberBetweenWhenNumberNotBetween() {
        boolean actual = loanService.isNumberBetween(0, 1, 8);
        assertFalse(actual);
        actual = loanService.isNumberBetween(10, 1, 8);
        assertFalse(actual);
    }

    @Test
    public void testCreateLoan() {
        NewLoanForm newLoanForm = new NewLoanForm();
        newLoanForm.setTermInDays(20);
        newLoanForm.setAmount(500d);

        loanService.createLoan(newLoanForm);

        verify(loanRepository, times(1)).save(any());
    }

    @Test
    public void testReturnLoanAmount() {
        loanService.returnLoanAmount(LOAN_ID, 200d, false);

        verify(loanRepository, times(0)).returnLoanAmountAndUpdateStatus(anyLong(), anyDouble(), anyString());
        verify(loanRepository, times(1)).returnLoanAmount(anyLong(), anyDouble());
    }

    @Test
    public void testReturnLoanAmountFullAmount() {
        loanService.returnLoanAmount(LOAN_ID, 200d, true);

        verify(loanRepository, times(0)).returnLoanAmount(anyLong(), anyDouble());
        verify(loanRepository, times(1)).returnLoanAmountAndUpdateStatus(anyLong(), anyDouble(), anyString());
    }

    @Test
    public void testExtendLoan() {
        populateLoanData();

        Date currentDate = new Date();
        loan.setLoanEndDate(currentDate);

        ExtendLoanForm extendLoanForm = new ExtendLoanForm();
        extendLoanForm.setUserId(1L);
        extendLoanForm.setLoanToExtendId(LOAN_ID);
        extendLoanForm.setTermInDays(20);

        loanService.extendLoan(loan, extendLoanForm);

        verify(loanRepository, times(1)).save(any());
    }

    private void populateLoanData() {
        loan.setId(LOAN_ID);
        loan.setUserId(1L);
        loan.setUserId(1L);
        loan.setInterestFactor(MyAppConstants.STANDARD_INTEREST_FACTOR);
        loan.setLoanAmount(500d);
        loan.setAmountReturned(0d);
        loan.setStatus(MyAppConstants.STATUS_APPROVED);
    }

    private Date localDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}

package io.fourfinanceit.app.service;

import io.fourfinanceit.app.model.*;
import io.fourfinanceit.app.model.domain.ExtendedLoan;
import io.fourfinanceit.app.model.domain.Loan;
import io.fourfinanceit.app.model.forms.ExtendLoanForm;
import io.fourfinanceit.app.model.forms.NewLoanForm;
import io.fourfinanceit.app.repository.ExtendedLoanRepository;
import io.fourfinanceit.app.repository.LoanRepository;
import io.fourfinanceit.app.utils.MyAppConstants;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ExtendedLoanRepository extendedLoanRepository;

    public Optional<Loan> findLoan(Long id) {
        return loanRepository.findById(id);
    }

    public void approveLoan(Long loanId) {
        loanRepository.changeLoanStatus(loanId, MyAppConstants.STATUS_APPROVED);
    }

    public void returnLoanAmount(Long loanId, Double amount, boolean fullAmount) {
        if (fullAmount) {
            loanRepository.returnLoanAmountAndUpdateStatus(loanId, amount, MyAppConstants.STATUS_CLOSED);
        } else {
            loanRepository.returnLoanAmount(loanId, amount);
        }
    }

    public void approveExtendedLoan(Long loanId) {
        extendedLoanRepository.changeExtendedLoanStatus(loanId, MyAppConstants.STATUS_APPROVED);
    }

    public Optional<Loan> getLoanByIdWithoutClosedLoans(Long loanId) {
        return loanRepository.findLoanByIdAndStatusNot(loanId, MyAppConstants.STATUS_CLOSED);
    }

    public List<Loan> getLoansByUserIdWithoutClosedLoans(Long userId) {
        return loanRepository.findLoansByUserIdAndStatusNot(userId, MyAppConstants.STATUS_CLOSED);
    }

    public Optional<LoanInfo> getLoanInfo(Long loanId) {
        Optional<Loan> optionalLoan = getLoanByIdWithoutClosedLoans(loanId);

        return optionalLoan.map(this::getLoanInfo);
    }

    public List<LoanInfo> getInformationAboutUserLoans(Long userId) {
        List<Loan> loans = getLoansByUserIdWithoutClosedLoans(userId);

        return loans.stream().map(this::getLoanInfo).collect(Collectors.toList());
    }

    private LoanInfo getLoanInfo(Loan loan) {

        LoanInfo loanInfo = new LoanInfo();
        loanInfo.setLoanStartDate(loan.getLoanStartDate());
        loanInfo.setLoanEndDate(loan.getLoanEndDate());
        loanInfo.setLoanAmount(loan.getLoanAmount());
        loanInfo.setInterestAmount(
                calculateLoanInterestToReturn(
                        loan.getInterestFactor(),
                        loan.getLoanAmount(),
                        loan.getLoanStartDate(),
                        loan.getLoanEndDate()
                )
        );
        loanInfo.setTotalInterestAmount(
                calculateLoanInterestToReturn(
                        loan.getInterestFactor(),
                        loan.getLoanAmount(),
                        loan.getLoanStartDate(),
                        loan.getLoanEndDate()
                )
        );
        loanInfo.setAmountToReturn(loanInfo.getLoanAmount() + loanInfo.getTotalInterestAmount());
        loanInfo.setAmountReturned(loan.getAmountReturned());
        loanInfo.setStatus(loan.getStatus());

        Optional.ofNullable(loan.getExtendedLoan()).ifPresent(extendedLoan -> {
            ExtendedLoanInfo extendedLoanInfo = new ExtendedLoanInfo();
            extendedLoanInfo.setLoanEndDate(extendedLoan.getLoanEndDate());
            extendedLoanInfo.setInterestAmount(
                    calculateLoanInterestToReturn(
                            extendedLoan.getInterestFactor(),
                            loan.getLoanAmount(),
                            loan.getLoanEndDate(),
                            extendedLoan.getLoanEndDate()
                    )
            );
            extendedLoanInfo.setStatus(extendedLoan.getStatus());
            loanInfo.setExtendedLoanInfo(extendedLoanInfo);
            if (!loan.getExtendedLoan().getStatus().equals(MyAppConstants.STATUS_WAITING_FOR_APPROVAL)) {
                loanInfo.setAmountToReturn(loanInfo.getAmountToReturn() + extendedLoanInfo.getInterestAmount());
                loanInfo.setTotalInterestAmount(loanInfo.getTotalInterestAmount() + extendedLoanInfo.getInterestAmount());
            }
        });

        Optional<Integer> overdueDays;
        if (loanInfo.getExtendedLoanInfo() != null &&
                !loan.getExtendedLoan().getStatus().equals(MyAppConstants.STATUS_WAITING_FOR_APPROVAL)) {
            overdueDays = returnDaysOverdueIfPresent(loanInfo.getExtendedLoanInfo().getLoanEndDate());
        } else {
            overdueDays = returnDaysOverdueIfPresent(loanInfo.getLoanEndDate());
        }

        overdueDays.ifPresent(overdue -> {
            OverdueLoanInfo overdueLoanInfo = new OverdueLoanInfo();
            overdueLoanInfo.setInterestAmount(calculateLoanInterestToReturn(
                    MyAppConstants.OVERDUE_INTEREST_FACTOR,
                    loanInfo.getLoanAmount(),
                    Double.valueOf(overdueDays.get())));
            overdueLoanInfo.setOverdueDays(overdueDays.get());
            loanInfo.setAmountToReturn(
                    loanInfo.getAmountToReturn() +
                            overdueLoanInfo.getInterestAmount());
            loanInfo.setTotalInterestAmount(loanInfo.getTotalInterestAmount() + overdueLoanInfo.getInterestAmount());
            loanInfo.setOverdueLoanInfo(overdueLoanInfo);
        });

        if (loanInfo.getAmountToReturn() != null) {
            loanInfo.setAmountToReturn(Precision.round(loanInfo.getAmountToReturn(), 2));
        }
        loanInfo.setAmountReturned(Precision.round(loanInfo.getAmountReturned(), 2));

        return loanInfo;
    }

    public void extendLoan(Loan loanToExtend, ExtendLoanForm loanExtensions) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(loanToExtend.getLoanEndDate());
        calendar.add(Calendar.DAY_OF_MONTH, loanExtensions.getTermInDays());
        Date extendedLoanEndDate = calendar.getTime();

        ExtendedLoan extendedLoan = new ExtendedLoan(
                MyAppConstants.EXTENDED_INTEREST_FACTOR,
                extendedLoanEndDate,
                MyAppConstants.STATUS_WAITING_FOR_APPROVAL
        );

        loanToExtend.setExtendedLoan(extendedLoan);
        extendedLoan.setLoan(loanToExtend);

        loanRepository.save(loanToExtend);
    }

    public void createLoan(NewLoanForm newLoanForm) {
        LocalDate loanStartDate = LocalDate.now();

        LocalDate loanEndDate = loanStartDate
                .plusDays(newLoanForm.getTermInDays());

        Loan loan = new Loan(
                newLoanForm.getUserId(),
                localDateToDate(loanStartDate),
                localDateToDate(loanEndDate),
                MyAppConstants.STANDARD_INTEREST_FACTOR,
                newLoanForm.getAmount(),
                0d,
                MyAppConstants.STATUS_WAITING_FOR_APPROVAL
        );

        loanRepository.save(loan);
    }

    private Double calculateLoanInterestToReturn(
            Double interestFactor,
            Double loanAmount,
            Date startDate,
            Date endDate) {

        LocalDate start = dateToLocalDate(startDate);
        LocalDate end = dateToLocalDate(endDate);

        Double termInDays = (double) DAYS.between(start, end);

        return interestFactor * termInDays * loanAmount / 100;
    }

    private Double calculateLoanInterestToReturn(
            Double interestFactor,
            Double loanAmount,
            Double termInDays) {
        return interestFactor * termInDays * loanAmount / 100;
    }

    public Optional<Integer> returnDaysOverdueIfPresent(Date loanEndDate) {
        LocalDate end = dateToLocalDate(loanEndDate);
        Long daysAfterLoanEndDate = DAYS.between(end, LocalDate.now());
        if (daysAfterLoanEndDate > 0) {
            return Optional.of(daysAfterLoanEndDate.intValue());
        } else {
            return Optional.empty();
        }
    }

    public boolean isNumberBetween(int value, int minValueInclusive, int maxValueInclusive) {
        return (value >= minValueInclusive) && (value <= maxValueInclusive);
    }

    private Date localDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate dateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}

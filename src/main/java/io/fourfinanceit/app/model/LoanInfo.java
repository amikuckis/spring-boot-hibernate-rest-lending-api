package io.fourfinanceit.app.model;

import java.util.Date;

public class LoanInfo {

    private Date loanStartDate;

    private Date loanEndDate;

    private Double loanAmount;

    private Double totalInterestAmount;

    private Double amountToReturn;

    private Double amountReturned;

    private String status;

    private ExtendedLoanInfo extendedLoanInfo;

    private OverdueLoanInfo overdueLoanInfo;

    public Date getLoanStartDate() {
        return loanStartDate;
    }

    public void setLoanStartDate(Date loanStartDate) {
        this.loanStartDate = loanStartDate;
    }

    public Date getLoanEndDate() {
        return loanEndDate;
    }

    public void setLoanEndDate(Date loanEndDate) {
        this.loanEndDate = loanEndDate;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Double getTotalInterestAmount() {
        return totalInterestAmount;
    }

    public void setTotalInterestAmount(Double totalInterestAmount) {
        this.totalInterestAmount = totalInterestAmount;
    }

    public Double getAmountToReturn() {
        return amountToReturn;
    }

    public void setAmountToReturn(Double amountToReturn) {
        this.amountToReturn = amountToReturn;
    }

    public Double getAmountReturned() {
        return amountReturned;
    }

    public void setAmountReturned(Double amountReturned) {
        this.amountReturned = amountReturned;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ExtendedLoanInfo getExtendedLoanInfo() {
        return extendedLoanInfo;
    }

    public void setExtendedLoanInfo(ExtendedLoanInfo extendedLoanInfo) {
        this.extendedLoanInfo = extendedLoanInfo;
    }

    public OverdueLoanInfo getOverdueLoanInfo() {
        return overdueLoanInfo;
    }

    public void setOverdueLoanInfo(OverdueLoanInfo overdueLoanInfo) {
        this.overdueLoanInfo = overdueLoanInfo;
    }
}

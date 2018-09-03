package io.fourfinanceit.app.model;

import java.util.Date;

public class ExtendedLoanInfo {

    private Date loanEndDate;

    private Double totalInterestAmount;

    private String status;

    public Date getLoanEndDate() {
        return loanEndDate;
    }

    public void setLoanEndDate(Date loanEndDate) {
        this.loanEndDate = loanEndDate;
    }

    public Double getTotalInterestAmount() {
        return totalInterestAmount;
    }

    public void setTotalInterestAmount(Double totalInterestAmount) {
        this.totalInterestAmount = totalInterestAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package io.fourfinanceit.app.model;

public class OverdueLoanInfo {

    private Integer overdueDays;

    private Double totalInterestAmount;

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public Double getTotalInterestAmount() {
        return totalInterestAmount;
    }

    public void setTotalInterestAmount(Double totalInterestAmount) {
        this.totalInterestAmount = totalInterestAmount;
    }
}

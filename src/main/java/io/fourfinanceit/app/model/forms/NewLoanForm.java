package io.fourfinanceit.app.model.forms;

import io.fourfinanceit.app.utils.MyAppConstants;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

public class NewLoanForm {

    @Positive
    private Long userId;

    @Min(10)
    @Max(MyAppConstants.MAX_LOAN_TERM_IN_DAYS)
    private Integer termInDays;

    @Min(10)
    @Max(MyAppConstants.MAX_LOAN_AMOUNT)
    private Double amount;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getTermInDays() {
        return termInDays;
    }

    public void setTermInDays(Integer termInDays) {
        this.termInDays = termInDays;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}


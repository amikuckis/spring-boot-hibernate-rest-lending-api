package io.fourfinanceit.app.model;

import io.fourfinanceit.app.utils.MyAppConstants;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

public class ExtendLoanForm {

    @Positive
    private Long loanToExtendId;

    @Positive
    private Long userId;

    @Min(10)
    @Max(MyAppConstants.MAX_LOAN_EXTEND_TERM_IN_DAYS)
    private Integer termInDays;

    public Long getLoanToExtendId() {
        return loanToExtendId;
    }

    public void setLoanToExtendId(Long loanToExtendId) {
        this.loanToExtendId = loanToExtendId;
    }

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
}

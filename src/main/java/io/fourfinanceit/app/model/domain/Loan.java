package io.fourfinanceit.app.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "loans")
@EntityListeners(AuditingEntityListener.class)
public class Loan implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    private Long userId;

    @CreatedDate
    @Temporal(TemporalType.DATE)
    private Date loanStartDate;

    @Temporal(TemporalType.DATE)
    private Date loanEndDate;

    private Double interestFactor;

    private Double loanAmount;

    private Double amountReturned;

    @NotBlank
    private String status;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "loan")
    private ExtendedLoan extendedLoan;

    @JsonIgnore
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @JsonIgnore
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;

    public Loan() { }

    public Loan(
            Long userId,
            Date loanStartDate,
            Date loanEndDate,
            Double interestFactor,
            Double loanAmount,
            Double amountReturned,
            String status) {
        this.userId = userId;
        this.loanStartDate = loanStartDate;
        this.loanEndDate = loanEndDate;
        this.interestFactor = interestFactor;
        this.loanAmount = loanAmount;
        this.amountReturned = amountReturned;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public Double getInterestFactor() {
        return interestFactor;
    }

    public void setInterestFactor(Double interestFactor) {
        this.interestFactor = interestFactor;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
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

    public ExtendedLoan getExtendedLoan() {
        return extendedLoan;
    }

    public void setExtendedLoan(ExtendedLoan extendedLoan) {
        this.extendedLoan = extendedLoan;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}

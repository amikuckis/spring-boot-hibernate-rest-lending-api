package io.fourfinanceit.app.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "extended_loans")
@EntityListeners(AuditingEntityListener.class)
public class ExtendedLoan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Temporal(TemporalType.DATE)
    private Date loanEndDate;

    private Double interestFactor;

    @NotBlank
    private String status;

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

    public ExtendedLoan() { }

    public ExtendedLoan(
            Double interestFactor,
            Date loanEndDate,
            String status) {
        this.interestFactor = interestFactor;
        this.loanEndDate = loanEndDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

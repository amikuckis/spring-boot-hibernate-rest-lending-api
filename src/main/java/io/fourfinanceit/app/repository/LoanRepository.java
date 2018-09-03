package io.fourfinanceit.app.repository;

import io.fourfinanceit.app.model.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findLoansByUserId(Long userId);

    Optional<Loan> findLoanByIdAndStatusNot(Long loanId, String status);

    List<Loan> findLoansByUserIdAndStatusNot(Long userId, String status);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Loan loan set loan.status =:status where loan.id =:loanId")
    void changeLoanStatus(@Param("loanId") Long loanId, @Param("status") String status);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Loan loan set loan.amountReturned = loan.amountReturned + :amount where loan.id =:loanId")
    void returnLoanAmount(@Param("loanId") Long loanId, @Param("amount") Double amount);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Loan loan set loan.amountReturned = loan.amountReturned + :amount, loan.status=:status where loan.id =:loanId")
    void returnLoanAmountAndUpdateStatus(
            @Param("loanId") Long loanId,
            @Param("amount")Double amount,
            @Param("status")String status);

}

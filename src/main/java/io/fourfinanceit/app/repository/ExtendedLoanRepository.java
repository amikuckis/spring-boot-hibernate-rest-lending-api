package io.fourfinanceit.app.repository;


import io.fourfinanceit.app.model.domain.ExtendedLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ExtendedLoanRepository extends JpaRepository<ExtendedLoan, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update ExtendedLoan extendedLoan set extendedLoan.status =:status where loan.id =:loanId")
    void changeExtendedLoanStatus(@Param("loanId") Long loanId, @Param("status") String status);
}

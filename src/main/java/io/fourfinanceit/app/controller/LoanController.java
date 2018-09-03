package io.fourfinanceit.app.controller;

import io.fourfinanceit.app.exception.*;
import io.fourfinanceit.app.model.ExtendLoanForm;
import io.fourfinanceit.app.model.LoanInfo;
import io.fourfinanceit.app.model.NewLoanForm;
import io.fourfinanceit.app.model.ReturnLoanAmountForm;
import io.fourfinanceit.app.model.domain.Loan;
import io.fourfinanceit.app.service.LoanService;
import io.fourfinanceit.app.service.LoggedRemoteAddressOfRequestService;
import io.fourfinanceit.app.service.UserService;
import io.fourfinanceit.app.utils.MyAppConstants;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/loan")
public class LoanController {

    private static final String CREATE_LOAN_REQUEST_NAME = "CREATE_LOAN";
    private static final String RESOURCE_NAME_USER = "User";
    private static final String RESOURCE_NAME_LOAN = "Loan";

    private static final int REMOTE_ADDRESS_CHECK_PERIOD = 24 * 60 * 1000;
    private static final int ENDPOINT_REQUEST_LIMIT_FOR_REMOTE_ADDRESS = 3;

    @Autowired
    LoanService loanService;

    @Autowired
    UserService userService;

    @Autowired
    LoggedRemoteAddressOfRequestService remoteAddressService;

    @RequestMapping(
            value = "/approve/{loanId}",
            method = RequestMethod.PUT)
    public ResponseEntity approveLoan(@PathVariable("loanId") Long loanId) {

        Optional<Loan> loanToApprove = loanService.findLoan(loanId);

        if (!loanToApprove.isPresent()) {
            throw new ResourceNotFoundException(RESOURCE_NAME_LOAN);
        }
        if (!loanToApprove.get().getStatus().equals(MyAppConstants.STATUS_WAITING_FOR_APPROVAL)) {
            throw new LoanAlreadyApprovedException();
        }
        loanService.approveLoan(loanId);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(
            value = "/approveExtended/{loanId}",
            method = RequestMethod.PUT)
    public ResponseEntity approveExtendedLoan(@PathVariable("loanId") Long loanId) {

        Optional<Loan> mainLoan = loanService.findLoan(loanId);

        if (!mainLoan.isPresent()) {
            throw new ResourceNotFoundException(RESOURCE_NAME_LOAN);
        }

        if (mainLoan.get().getExtendedLoan() == null) {
            throw new ResourceNotFoundException("Extended Loan");
        }

        if (!mainLoan.get().getExtendedLoan().getStatus().equals(MyAppConstants.STATUS_WAITING_FOR_APPROVAL)) {
            throw new LoanAlreadyApprovedException();
        }
        loanService.approveExtendedLoan(mainLoan.get().getExtendedLoan().getId());

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(
            value = "/returnLoanAmount",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity returnLoanAmount(@RequestBody ReturnLoanAmountForm returnLoanAmountForm) {

        Optional<LoanInfo> optionalLoanInfo = loanService.getOptionalLoanInfoForUser(returnLoanAmountForm.getLoanId());

        if (!optionalLoanInfo.isPresent()) {
            throw new ResourceNotFoundException(RESOURCE_NAME_LOAN);
        }

        LoanInfo loanInfo = optionalLoanInfo.get();

        if (loanInfo.getStatus().equals(MyAppConstants.STATUS_WAITING_FOR_APPROVAL)) {
            throw new LoanNotApprovedException();
        }

        Double amountToReturn = Precision.round(loanInfo.getAmountToReturn() - loanInfo.getAmountReturned(), 2);
        Double requestedReturnAmount = Precision.round(returnLoanAmountForm.getAmount(), 2);

        if (requestedReturnAmount > amountToReturn) {
            throw new LoanAmountReturnExceeds(requestedReturnAmount, amountToReturn);
        }

        boolean fullAmountReturn = requestedReturnAmount >= amountToReturn;
        loanService.returnLoanAmount(
                returnLoanAmountForm.getLoanId(),
                requestedReturnAmount,
                fullAmountReturn);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(
            value = "/{loanId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Loan getLoan(@PathVariable("loanId") Long loanId) {

        return loanService.findLoan(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("loan"));
    }

    @RequestMapping(
            value = "/userLoans/{userId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<LoanInfo> getUserLoans(@PathVariable("userId") Long userId) {
        return loanService.getInformationAboutUserLoans(userId);
    }

    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity newLoan(
            HttpServletRequest request,
            @Valid @RequestBody NewLoanForm loan) {

        int forbiddenRequestTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if ((loan.getAmount() >= MyAppConstants.MAX_LOAN_AMOUNT) &&
                loanService.between(forbiddenRequestTime,
                MyAppConstants.FORBIDDEN_HOUR_MIN,
                MyAppConstants.FORBIDDEN_HOUR_MAX)) {
            throw new ForbiddenRequestHoursException(
                    MyAppConstants.MAX_LOAN_AMOUNT,
                    MyAppConstants.FORBIDDEN_HOUR_MIN,
                    MyAppConstants.FORBIDDEN_HOUR_MAX);
        }

        if (!userService.findById(loan.getUserId()).isPresent()) {
            throw new ResourceNotFoundException(RESOURCE_NAME_USER);
        }

        if (loanService.getLoansByUserIdWithoutClosedLoans(loan.getUserId()).size() >=
                MyAppConstants.LOANS_PER_USER_LIMIT) {
            throw new LoanLimitPerUserExceededException(MyAppConstants.LOANS_PER_USER_LIMIT);
        }

        boolean requestsExceededForRemoteAddress = remoteAddressService.checkIfRequestLimitForRemoteAddressExceeded(
                request.getRemoteAddr(),
                Collections.singletonList(CREATE_LOAN_REQUEST_NAME),
                REMOTE_ADDRESS_CHECK_PERIOD,
                ENDPOINT_REQUEST_LIMIT_FOR_REMOTE_ADDRESS);

        if (requestsExceededForRemoteAddress) {
            throw new RequestsForRemoteAddressExceededException(request.getRemoteAddr(), ENDPOINT_REQUEST_LIMIT_FOR_REMOTE_ADDRESS);
        }

        loanService.createLoan(loan);

        remoteAddressService.logRequestRemoteAddress(request.getRemoteAddr(), CREATE_LOAN_REQUEST_NAME);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(
            value = "/extend",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity extendLoan(
            @Valid @RequestBody ExtendLoanForm loanExtension) {

        if (!userService.findById(loanExtension.getUserId()).isPresent()) {
            throw new ResourceNotFoundException(RESOURCE_NAME_USER);
        }

        Optional<Loan> loanToExtend = loanService.findLoan(loanExtension.getLoanToExtendId());

        if (!loanToExtend.isPresent()) {
            throw new ResourceNotFoundException(RESOURCE_NAME_LOAN);
        }

        if (loanToExtend.get().getStatus().equals(MyAppConstants.STATUS_WAITING_FOR_APPROVAL)) {
            throw new LoanNotApprovedException();
        }

        loanService.extendLoan(loanToExtend.get(), loanExtension);

        return ResponseEntity.ok(HttpStatus.OK);
    }

}

package uk.gov.hmcts.reform.ref.pup.controller;

import uk.gov.hmcts.reform.auth.checker.spring.serviceanduser.ServiceAndUserDetails;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountAssignment;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountCreation;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountDto;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException;
import uk.gov.hmcts.reform.ref.pup.service.adaptor.PaymentAccountServiceAdaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

@RestController
@RequestMapping("pup/payment-accounts")
public class PaymentAccountController {

    private static final ResponseEntity<PaymentAccountDto> NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();
    private final PaymentAccountServiceAdaptor paymentAccountService;

    @Autowired
    public PaymentAccountController(PaymentAccountServiceAdaptor paymentAccountService) {
        this.paymentAccountService = paymentAccountService;
    }

    @PostMapping
    @ApiOperation("Create payment account.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = PaymentAccountDto.class)
    })
    public ResponseEntity<PaymentAccountDto> createPaymentAccount(@RequestBody @Valid PaymentAccountCreation paymentAccount) throws ApplicationException {
        return ResponseEntity.ok(paymentAccountService.create(paymentAccount));
    }

    @GetMapping(value = "{pbaNumber}")
    @ApiOperation("Retrieve payment account.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = PaymentAccountDto.class)
    })
    public ResponseEntity<PaymentAccountDto> getProfessionalUser(@PathVariable String pbaNumber) throws ApplicationException {

        Optional<PaymentAccountDto> paymentAccount = paymentAccountService.retrieve(pbaNumber);

        if (!paymentAccount.isPresent()) {
            return NOT_FOUND_RESPONSE;
        }

        return ResponseEntity.ok(paymentAccount.get());
    }

    @DeleteMapping(value = "{pbaNumber}")
    @ApiOperation("Delete payment account.")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "No Content")
    })
    public ResponseEntity<Void> deletePaymentAccount(@PathVariable String pbaNumber) throws ApplicationException {

        paymentAccountService.delete(pbaNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "mine")
    @ApiOperation("Retrieve my payment account.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success", response = PaymentAccountDto.class)
    })
    public ResponseEntity<List<PaymentAccountDto>> myPaymentAccounts(@AuthenticationPrincipal ServiceAndUserDetails userDetails) throws ApplicationException {

        List<PaymentAccountDto> paymentAccount = paymentAccountService.retrieveForUser(userDetails.getUsername());
        return ResponseEntity.ok(paymentAccount);
    }

    @PostMapping(value = "{pbaNumber}/assign")
    @ApiOperation("Assign a payment account.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content")
    })
    public ResponseEntity<PaymentAccountDto> assignPaymentAccounts(
            @PathVariable String pbaNumber,
            @RequestBody @Valid PaymentAccountAssignment paymentAccountAssignment) throws ApplicationException {

        PaymentAccountDto paymentAccount = paymentAccountService.assign(pbaNumber, paymentAccountAssignment);
        return ResponseEntity.ok(paymentAccount);
    }

    @PostMapping(value = "{pbaNumber}/unassign")
    @ApiOperation("Unassign a payment account.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content")
    })
    public ResponseEntity<PaymentAccountDto> unassignPaymentAccounts(
            @PathVariable String pbaNumber,
            @RequestBody @Valid PaymentAccountAssignment paymentAccountAssignment) throws ApplicationException {

        PaymentAccountDto paymentAccount = paymentAccountService.unassign(pbaNumber, paymentAccountAssignment);
        return ResponseEntity.ok(paymentAccount);
    }
}

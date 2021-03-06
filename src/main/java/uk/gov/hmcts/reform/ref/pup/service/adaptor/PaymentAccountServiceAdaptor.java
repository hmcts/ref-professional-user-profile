package uk.gov.hmcts.reform.ref.pup.service.adaptor;

import uk.gov.hmcts.reform.ref.pup.converter.PaymentAccountConverter;
import uk.gov.hmcts.reform.ref.pup.domain.PaymentAccount;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountAssignment;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountCreation;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountDto;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException;
import uk.gov.hmcts.reform.ref.pup.service.PaymentAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
@Transactional
public class PaymentAccountServiceAdaptor {

    private final PaymentAccountService paymentAccountService;
    private final PaymentAccountConverter paymentAccountConverter;

    @Autowired
    public PaymentAccountServiceAdaptor(PaymentAccountService paymentAccountService, PaymentAccountConverter paymentAccountConverter) {
        this.paymentAccountService = paymentAccountService;
        this.paymentAccountConverter = paymentAccountConverter;
    }

    public PaymentAccountDto create(PaymentAccountCreation paymentAccount) throws ApplicationException {
        return paymentAccountConverter.apply(paymentAccountService.create(paymentAccount));
    }

    public Optional<PaymentAccountDto> retrieve(String pbaNumber) throws ApplicationException {
        Optional<PaymentAccount> retrieve = paymentAccountService.retrieve(pbaNumber);
        if (retrieve.isPresent()) {
            return Optional.of(paymentAccountConverter.apply(retrieve.get()));
        } else {
            return Optional.empty();
        }
    }

    public void delete(String pbaNumber) throws ApplicationException {
        paymentAccountService.delete(pbaNumber);
    }

    public List<PaymentAccountDto> retrieveForUser(String username) throws ApplicationException {
        List<PaymentAccount> retrieve = paymentAccountService.retrieveForUser(username);
        return retrieve.stream().map(paymentAccountConverter)
                                .collect(Collectors.toList());

    }

    public PaymentAccountDto assign(String pbaNumber, PaymentAccountAssignment paymentAccountAssignment) throws ApplicationException {
        paymentAccountService.assign(pbaNumber, paymentAccountAssignment);
        return retrieve(pbaNumber).orElseThrow(NullPointerException::new);
    }

    public PaymentAccountDto unassign(String pbaNumber, PaymentAccountAssignment paymentAccountAssignment) throws ApplicationException {
        paymentAccountService.unassign(pbaNumber, paymentAccountAssignment);
        return retrieve(pbaNumber).orElseThrow(NullPointerException::new);
    }
}
package uk.gov.hmcts.reform.ref.pup.service.impl;

import uk.gov.hmcts.reform.ref.pup.domain.Organisation;
import uk.gov.hmcts.reform.ref.pup.domain.PaymentAccount;
import uk.gov.hmcts.reform.ref.pup.domain.ProfessionalUser;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountCreation;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException.ApplicationErrorCode;
import uk.gov.hmcts.reform.ref.pup.repository.PaymentAccountRepository;
import uk.gov.hmcts.reform.ref.pup.repository.ProfessionalUserRepository;
import uk.gov.hmcts.reform.ref.pup.service.OrganisationService;
import uk.gov.hmcts.reform.ref.pup.service.PaymentAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ref.pup.service.ProfessionalUserService;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
@Transactional
public class PaymentAccountServiceImpl implements PaymentAccountService {

    private final PaymentAccountRepository paymentAccountRepository;

    private final OrganisationService organisationService;

    private final ProfessionalUserRepository professionalUserRepository;

    @Autowired
    public PaymentAccountServiceImpl(PaymentAccountRepository paymentAccountRepository,
                                     OrganisationService organisationService,
                                     ProfessionalUserRepository professionalUserRepository) {
        this.paymentAccountRepository = paymentAccountRepository;
        this.organisationService = organisationService;
        this.professionalUserRepository = professionalUserRepository;
    }

    @Override
    public PaymentAccount findOrCreate(PaymentAccountCreation paymentAccountInput) throws ApplicationException {

        Organisation organisation = organisationService.retrieve(paymentAccountInput.getOrganisationId())
            .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.PAYMENT_ACCOUNT_ID_DOES_NOT_EXIST));

        Optional<PaymentAccount> maybePaymentAccount = paymentAccountRepository.findByPbaNumberAndAndOrganisation(paymentAccountInput.getPbaNumber(), organisation);

        if (maybePaymentAccount.isPresent()) {
            return maybePaymentAccount.get();
        } else {
            PaymentAccount paymentAccount = new PaymentAccount();
            paymentAccount.setPbaNumber(paymentAccountInput.getPbaNumber());
            paymentAccount.setOrganisation(organisation);
            return paymentAccountRepository.save(paymentAccount);
        }
    }

    @Override
    public Optional<PaymentAccount> retrieve(String pbaNumber) {
        return paymentAccountRepository.findByPbaNumber(pbaNumber);
    }

    @Override
    public void delete(String pbaNumber) {
        paymentAccountRepository.deleteByPbaNumber(pbaNumber);
    }

    @Override
    public List<PaymentAccount> findByUserEmail(String email) throws ApplicationException {
        Optional<ProfessionalUser> maybeProfessionalUser = professionalUserRepository.findOneByEmail(email);
        List<PaymentAccount> paymentAccounts = new LinkedList<>();
        if (maybeProfessionalUser.isPresent()) {
            paymentAccounts.addAll(maybeProfessionalUser.get().getAccountAssignments());
        }
        return paymentAccounts;
    }
}

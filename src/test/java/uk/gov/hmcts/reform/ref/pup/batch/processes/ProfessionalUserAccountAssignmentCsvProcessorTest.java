package uk.gov.hmcts.reform.ref.pup.batch.processes;

import uk.gov.hmcts.reform.ref.pup.batch.model.ProfessionalUserAccountAssignmentCsvModel;
import uk.gov.hmcts.reform.ref.pup.domain.Organisation;
import uk.gov.hmcts.reform.ref.pup.domain.OrganisationType;
import uk.gov.hmcts.reform.ref.pup.domain.PaymentAccount;
import uk.gov.hmcts.reform.ref.pup.domain.ProfessionalUser;
import uk.gov.hmcts.reform.ref.pup.dto.OrganisationRequest;
import uk.gov.hmcts.reform.ref.pup.dto.PaymentAccountRequest;
import uk.gov.hmcts.reform.ref.pup.dto.ProfessionalUserRequest;
import uk.gov.hmcts.reform.ref.pup.exception.ApplicationException;
import uk.gov.hmcts.reform.ref.pup.services.OrganisationService;
import uk.gov.hmcts.reform.ref.pup.services.PaymentAccountService;
import uk.gov.hmcts.reform.ref.pup.services.ProfessionalUserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ProfessionalUserAccountAssignmentCsvProcessorTest {

    @Mock
    private OrganisationService organisationService;
    
    @Mock
    private PaymentAccountService paymentAccountService;

    @Mock
    private ProfessionalUserService professionalUserService;
    
    
    @InjectMocks
    private ProfessionalUserAccountAssignmentCsvProcessor professionalUserAccountAssignmentCsv;

    private Organisation testOrganisation;
    private ProfessionalUser testProfessionalUser;
    private PaymentAccount testPaymentAccount;
    
    @Captor
    ArgumentCaptor<PaymentAccountRequest> paymentAccountRequestCaptor;
    @Captor
    ArgumentCaptor<String> userIdCaptor;
    @Captor
    ArgumentCaptor<UUID> organisationUUIDCaptor;
    @Captor
    ArgumentCaptor<ProfessionalUserRequest> professionalUserRequestCaptor;
    @Captor
    ArgumentCaptor<OrganisationRequest> organisationRequestCaptor;
    
    private ProfessionalUserAccountAssignmentCsvModel testProfessionalUserAccountAssignmentCsvModel;

    @Before
    public void setUp() {

        testOrganisation = createFakeOrganisation();
        testProfessionalUser = createFakeProfessionalUser();
        testPaymentAccount = createFakePaymentAccount();
        testProfessionalUserAccountAssignmentCsvModel = createFakeModel();
        
    }

    private ProfessionalUser createFakeProfessionalUser() {
        ProfessionalUser firstTestUser = new ProfessionalUser();
        firstTestUser.setEmail("DUMMY@DUMMY.com");
        firstTestUser.setFirstName("DUMMY");
        firstTestUser.setPhoneNumber("DUMMY");
        firstTestUser.setSurname("DUMMY");
        firstTestUser.setUserId("DUMMY");
        firstTestUser.setUuid(UUID.randomUUID());
        firstTestUser.setAccountAssignments(new HashSet<>());
        return firstTestUser;
    }
    
    private Organisation createFakeOrganisation() {
        Organisation firstTestOrganisation = new Organisation();
        firstTestOrganisation.setName("DUMMY");
        firstTestOrganisation.setOrganisationType(new OrganisationType());
        firstTestOrganisation.setUuid(UUID.randomUUID());
        return firstTestOrganisation;
    }
    
    private PaymentAccount createFakePaymentAccount() {
        PaymentAccount paymentAccount = new PaymentAccount();
        paymentAccount.setPbaNumber("DUMMY");
        paymentAccount.setUuid(UUID.randomUUID());
        return paymentAccount;
    }
    
    private ProfessionalUserAccountAssignmentCsvModel createFakeModel() {
        ProfessionalUserAccountAssignmentCsvModel firstTestProfessionalUserAccountAssignmentCsvModel = new ProfessionalUserAccountAssignmentCsvModel();
        firstTestProfessionalUserAccountAssignmentCsvModel.setOrgName("DUMMY");
        firstTestProfessionalUserAccountAssignmentCsvModel.setPbaNumber("DUMMY");
        firstTestProfessionalUserAccountAssignmentCsvModel.setUserEmail("DUMMY@DUMMY.com");
        return firstTestProfessionalUserAccountAssignmentCsvModel;
    }
    
    
    @Test
    public void process_happyPath() throws ApplicationException {
        when(organisationService.create(any())).thenReturn(testOrganisation);
        when(professionalUserService.create(any())).thenReturn(testProfessionalUser);
        doNothing().when(professionalUserService).assignPaymentAccount(any(), any());
        when(paymentAccountService.create(any())).thenReturn(testPaymentAccount);
        
        professionalUserAccountAssignmentCsv.process(testProfessionalUserAccountAssignmentCsvModel);

        
        verify(organisationService, only()).create(organisationRequestCaptor.capture());
        verify(professionalUserService).create(professionalUserRequestCaptor.capture());
        verify(professionalUserService).assignPaymentAccount(userIdCaptor.capture(), organisationUUIDCaptor.capture());
        verify(paymentAccountService, only()).create(paymentAccountRequestCaptor.capture());
        
        assertThat(organisationRequestCaptor.getValue().getName(), equalTo(testProfessionalUserAccountAssignmentCsvModel.getOrgName()));
        assertThat(professionalUserRequestCaptor.getValue().getEmail(), equalTo(testProfessionalUserAccountAssignmentCsvModel.getUserEmail()));
        assertThat(paymentAccountRequestCaptor.getValue().getPbaNumber(), equalTo(testProfessionalUserAccountAssignmentCsvModel.getPbaNumber()));
        
    }
    
}

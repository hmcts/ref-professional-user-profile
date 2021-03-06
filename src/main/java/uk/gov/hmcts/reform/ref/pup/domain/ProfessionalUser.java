package uk.gov.hmcts.reform.ref.pup.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userId" }))
public class ProfessionalUser extends AbstractDomain {

    @NotEmpty
    private String userId;

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String surname;

    private String email;

    private String phoneNumber;

    @NotNull
    @ManyToOne
    private Organisation organisation;

}

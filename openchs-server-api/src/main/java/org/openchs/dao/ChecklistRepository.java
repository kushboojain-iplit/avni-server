package org.openchs.dao;

import org.joda.time.DateTime;
import org.openchs.domain.Checklist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(collectionResourceRel = "txNewChecklistEntity", path = "txNewChecklistEntity", exported = false)
@PreAuthorize(value = "hasAnyAuthority('user', 'admin')")
public interface ChecklistRepository extends PagingAndSortingRepository<Checklist, Long>, CHSRepository<Checklist> {
    Page<Checklist> findByProgramEnrolmentIndividualAddressLevelVirtualCatchmentsIdAndAuditLastModifiedDateTimeIsBetweenOrderByAuditLastModifiedDateTimeAscIdAsc(
            long catchmentId,
            DateTime lastModifiedDateTime,
            DateTime now,
            Pageable pageable);

    Checklist findByProgramEnrolmentId(long programEnrolmentId);

    Checklist findByProgramEnrolmentUuidAndChecklistDetailName(String enrolmentUUID, String name);
}
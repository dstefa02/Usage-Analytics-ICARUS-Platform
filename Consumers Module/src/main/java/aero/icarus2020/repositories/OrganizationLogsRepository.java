package aero.icarus2020.repositories;

import aero.icarus2020.models.OrganizationLogsModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_READONLY;

@Repository
public interface OrganizationLogsRepository extends CrudRepository<OrganizationLogsModel, Long> {
    @QueryHints(value = {
            @QueryHint(name = HINT_READONLY, value = "true")
    })
    @Query(value = "SELECT * FROM organizationlogs WHERE organization_id=?1", nativeQuery = true)
    List<Object[]> findByOrganizationId(long assetId);

    @Query(value = "SELECT * FROM organizationlogs WHERE event_id=?1", nativeQuery = true)
    List<Object[]> findByEventId(long eventId);

    @Query(value = "DELETE FROM organizationlogs WHERE organization_id=?1 AND event_id=?2", nativeQuery = true)
    void deleteLog(long assetId, long eventId);
}


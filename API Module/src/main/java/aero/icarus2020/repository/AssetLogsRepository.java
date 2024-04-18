package aero.icarus2020.repository;

import aero.icarus2020.models.AssetLogsModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_READONLY;

@Repository
public interface AssetLogsRepository extends CrudRepository<AssetLogsModel, Long> {

    @QueryHints(value = {
            @QueryHint(name = HINT_READONLY, value = "true")
    })
    @Query(value = "SELECT * FROM assetlogs WHERE asset_id=?1", nativeQuery = true)
    List<Object[]> findByAssetId(long assetId);

    @Query(value = "SELECT * FROM assetlogs WHERE event_id=?1", nativeQuery = true)
    List<Object[]> findByEventId(long eventId);

    @Query(value = "DELETE FROM assetlogs WHERE asset_id=?1 AND event_id=?2", nativeQuery = true)
    void deleteLog(long assetId, long eventId);
}

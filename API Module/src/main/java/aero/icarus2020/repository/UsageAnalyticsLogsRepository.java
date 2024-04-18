package aero.icarus2020.repository;

import aero.icarus2020.models.UsageAnalyticsLogsModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.QueryHints;
import javax.persistence.QueryHint;

import static org.hibernate.jpa.QueryHints.HINT_READONLY;

@Repository
public interface UsageAnalyticsLogsRepository extends CrudRepository<UsageAnalyticsLogsModel, Long>  {

    @QueryHints(value = {
            @QueryHint(name = HINT_READONLY, value = "true")
    })
    @Query(value = "SELECT cast(a.created_on AS date), a.meta->>'user_id' as user_id FROM usageanalyticslogs as a WHERE a.event_type=?1", nativeQuery = true)
    Stream<Object[]> getActiveUsers(String event_type);

    @Query(value = "SELECT  a.meta->>'user_id' as user_id, a.meta->>'user_agent' as user_agent FROM usageanalyticslogs as a WHERE a.event_type='USER_CONNECT'", nativeQuery = true)
    Stream<Object[]> getConnectEvent();

    @Query(value = "SELECT cast(a.created_on AS date), count(a) FROM usageanalyticslogs as a WHERE a.event_type=?1 GROUP BY cast(a.created_on AS date)", nativeQuery = true)
    List<Object[]> getCountsByEventType(String event_type);

    @Query(value = "select u.event_type, cast(u.created_on AS date) from  assetlogs a, usageanalyticslogs u where a.asset_id =?1 and a.event_id = u.id ", nativeQuery = true)
    List<Object[]> getUserAssetEvents(Integer id);

    @Query(value = "select u.event_type, cast(u.created_on AS date), count(u) from  assetlogs a, usageanalyticslogs u where a.asset_id =?1 and a.event_id = u.id  GROUP BY u.event_type ,cast(u.created_on AS DATE) order by event_type, created_on", nativeQuery = true)
    List<Object[]> getUserAssetEventsByDate(Integer id);

    @Query(value = "SELECT  cast(a.meta->>'asset_id' AS varchar) FROM usageanalyticslogs as a WHERE a.event_type=?1", nativeQuery = true)
    List<String> getSpecificEvents(String event_type);

}


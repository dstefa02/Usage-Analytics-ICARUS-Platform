package aero.icarus2020.repositories;

import aero.icarus2020.models.UsageAnalyticsLogsModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageAnalyticsLogsRepository extends CrudRepository<UsageAnalyticsLogsModel, Long>  {
}

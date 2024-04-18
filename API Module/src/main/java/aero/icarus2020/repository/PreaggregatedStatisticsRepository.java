package aero.icarus2020.repository;

import aero.icarus2020.models.PreaggregatedStatisticsModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreaggregatedStatisticsRepository extends CrudRepository<PreaggregatedStatisticsModel, Long> {

    //@Query("SELECT p FROM preaggregatedstatistics p where p.name = :name")
    //List<PreaggregatedStatisticsModel> findMetricByName(@Param("name") String name);

}

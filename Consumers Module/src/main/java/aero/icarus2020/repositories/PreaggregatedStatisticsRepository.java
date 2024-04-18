package aero.icarus2020.repositories;

import aero.icarus2020.models.PreaggregatedStatisticsModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PreaggregatedStatisticsRepository extends CrudRepository<PreaggregatedStatisticsModel, Long> {

    //@Query("SELECT p FROM preaggregatedstatistics p where p.name=:name")
    //List<PreaggregatedStatisticsModel> findByNameEquals(String name);

}

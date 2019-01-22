package in.disruptech.platform.repository;

import in.disruptech.platform.domain.Datapoint;
import in.disruptech.platform.domain.Membership;
import in.disruptech.platform.domain.Rack;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Datapoint entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DatapointRepository extends JpaRepository<Datapoint, Long> {
    @Query(value = "SELECT * FROM Datapoint where tag = ?tag and rack = ?rack order by captureTime limit 1", nativeQuery = true)
    Optional<Datapoint> getLatestData(@Param("rack") Long rackId, @Param("tag") String tag);
}

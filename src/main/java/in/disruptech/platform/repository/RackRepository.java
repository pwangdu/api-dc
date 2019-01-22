package in.disruptech.platform.repository;

import in.disruptech.platform.domain.Rack;
import in.disruptech.platform.domain.ZoneMonitor;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Rack entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {
    Optional<Rack> findByZoneMonitor(ZoneMonitor zoneMonitor);
}

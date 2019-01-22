package in.disruptech.platform.repository;

import in.disruptech.platform.domain.ZoneMonitor;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the ZoneMonitor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ZoneMonitorRepository extends JpaRepository<ZoneMonitor, Long> {
    Optional<ZoneMonitor> findByZoneMonitorId(String zoneMonitorId);
}

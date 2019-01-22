package in.disruptech.platform.repository;

import in.disruptech.platform.domain.Membership;
import in.disruptech.platform.domain.Rack;
import in.disruptech.platform.domain.Server;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Membership entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    @Query(value = "SELECT m FROM Membership m where m.rack = :rack and m.endTime is null")
    List<Membership> getServersInRack(@Param("rack") Rack rack);
    @Query(value = "SELECT m FROM Membership m where m.server = :server and m.endTime is null")
    List<Membership> getLastReportedLocation(@Param("server") Server server);
}

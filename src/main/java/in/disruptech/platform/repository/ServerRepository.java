package in.disruptech.platform.repository;

import in.disruptech.platform.domain.Server;
import in.disruptech.platform.domain.Tag;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Server entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
    Optional<Server> findByTag(Tag tag);
}

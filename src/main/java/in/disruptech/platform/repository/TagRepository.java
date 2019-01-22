package in.disruptech.platform.repository;

import in.disruptech.platform.domain.Tag;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Tag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByTagId(String tagId);
}

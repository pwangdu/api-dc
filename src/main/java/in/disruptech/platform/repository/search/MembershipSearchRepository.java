package in.disruptech.platform.repository.search;

import in.disruptech.platform.domain.Membership;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Membership entity.
 */
public interface MembershipSearchRepository extends ElasticsearchRepository<Membership, Long> {
}

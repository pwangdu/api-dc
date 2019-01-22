package in.disruptech.platform.repository.search;

import in.disruptech.platform.domain.Rack;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Rack entity.
 */
public interface RackSearchRepository extends ElasticsearchRepository<Rack, Long> {
}

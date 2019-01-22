package in.disruptech.platform.repository.search;

import in.disruptech.platform.domain.Datapoint;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Datapoint entity.
 */
public interface DatapointSearchRepository extends ElasticsearchRepository<Datapoint, Long> {
}

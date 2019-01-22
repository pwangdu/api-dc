package in.disruptech.platform.repository.search;

import in.disruptech.platform.domain.Server;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Server entity.
 */
public interface ServerSearchRepository extends ElasticsearchRepository<Server, Long> {
}

package in.disruptech.platform.repository.search;

import in.disruptech.platform.domain.ZoneMonitor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ZoneMonitor entity.
 */
public interface ZoneMonitorSearchRepository extends ElasticsearchRepository<ZoneMonitor, Long> {
}

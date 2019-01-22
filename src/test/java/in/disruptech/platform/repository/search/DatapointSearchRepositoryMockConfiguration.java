package in.disruptech.platform.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of DatapointSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class DatapointSearchRepositoryMockConfiguration {

    @MockBean
    private DatapointSearchRepository mockDatapointSearchRepository;

}

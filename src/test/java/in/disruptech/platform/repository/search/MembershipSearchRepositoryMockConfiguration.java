package in.disruptech.platform.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of MembershipSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class MembershipSearchRepositoryMockConfiguration {

    @MockBean
    private MembershipSearchRepository mockMembershipSearchRepository;

}

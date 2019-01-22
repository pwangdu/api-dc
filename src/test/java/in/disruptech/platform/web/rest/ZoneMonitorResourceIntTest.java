package in.disruptech.platform.web.rest;

import in.disruptech.platform.PlatformApp;

import in.disruptech.platform.domain.ZoneMonitor;
import in.disruptech.platform.repository.ZoneMonitorRepository;
import in.disruptech.platform.repository.search.ZoneMonitorSearchRepository;
import in.disruptech.platform.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static in.disruptech.platform.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ZoneMonitorResource REST controller.
 *
 * @see ZoneMonitorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlatformApp.class)
public class ZoneMonitorResourceIntTest {

    private static final String DEFAULT_ZONE_MONITOR_ID = "AAAAAAAAAA";
    private static final String UPDATED_ZONE_MONITOR_ID = "BBBBBBBBBB";

    @Autowired
    private ZoneMonitorRepository zoneMonitorRepository;

    /**
     * This repository is mocked in the in.disruptech.platform.repository.search test package.
     *
     * @see in.disruptech.platform.repository.search.ZoneMonitorSearchRepositoryMockConfiguration
     */
    @Autowired
    private ZoneMonitorSearchRepository mockZoneMonitorSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restZoneMonitorMockMvc;

    private ZoneMonitor zoneMonitor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ZoneMonitorResource zoneMonitorResource = new ZoneMonitorResource(zoneMonitorRepository, mockZoneMonitorSearchRepository);
        this.restZoneMonitorMockMvc = MockMvcBuilders.standaloneSetup(zoneMonitorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ZoneMonitor createEntity(EntityManager em) {
        ZoneMonitor zoneMonitor = new ZoneMonitor()
            .zoneMonitorId(DEFAULT_ZONE_MONITOR_ID);
        return zoneMonitor;
    }

    @Before
    public void initTest() {
        zoneMonitor = createEntity(em);
    }

    @Test
    @Transactional
    public void createZoneMonitor() throws Exception {
        int databaseSizeBeforeCreate = zoneMonitorRepository.findAll().size();

        // Create the ZoneMonitor
        restZoneMonitorMockMvc.perform(post("/api/zone-monitors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(zoneMonitor)))
            .andExpect(status().isCreated());

        // Validate the ZoneMonitor in the database
        List<ZoneMonitor> zoneMonitorList = zoneMonitorRepository.findAll();
        assertThat(zoneMonitorList).hasSize(databaseSizeBeforeCreate + 1);
        ZoneMonitor testZoneMonitor = zoneMonitorList.get(zoneMonitorList.size() - 1);
        assertThat(testZoneMonitor.getZoneMonitorId()).isEqualTo(DEFAULT_ZONE_MONITOR_ID);

        // Validate the ZoneMonitor in Elasticsearch
        verify(mockZoneMonitorSearchRepository, times(1)).save(testZoneMonitor);
    }

    @Test
    @Transactional
    public void createZoneMonitorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = zoneMonitorRepository.findAll().size();

        // Create the ZoneMonitor with an existing ID
        zoneMonitor.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restZoneMonitorMockMvc.perform(post("/api/zone-monitors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(zoneMonitor)))
            .andExpect(status().isBadRequest());

        // Validate the ZoneMonitor in the database
        List<ZoneMonitor> zoneMonitorList = zoneMonitorRepository.findAll();
        assertThat(zoneMonitorList).hasSize(databaseSizeBeforeCreate);

        // Validate the ZoneMonitor in Elasticsearch
        verify(mockZoneMonitorSearchRepository, times(0)).save(zoneMonitor);
    }

    @Test
    @Transactional
    public void getAllZoneMonitors() throws Exception {
        // Initialize the database
        zoneMonitorRepository.saveAndFlush(zoneMonitor);

        // Get all the zoneMonitorList
        restZoneMonitorMockMvc.perform(get("/api/zone-monitors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zoneMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].zoneMonitorId").value(hasItem(DEFAULT_ZONE_MONITOR_ID.toString())));
    }
    
    @Test
    @Transactional
    public void getZoneMonitor() throws Exception {
        // Initialize the database
        zoneMonitorRepository.saveAndFlush(zoneMonitor);

        // Get the zoneMonitor
        restZoneMonitorMockMvc.perform(get("/api/zone-monitors/{id}", zoneMonitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(zoneMonitor.getId().intValue()))
            .andExpect(jsonPath("$.zoneMonitorId").value(DEFAULT_ZONE_MONITOR_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingZoneMonitor() throws Exception {
        // Get the zoneMonitor
        restZoneMonitorMockMvc.perform(get("/api/zone-monitors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateZoneMonitor() throws Exception {
        // Initialize the database
        zoneMonitorRepository.saveAndFlush(zoneMonitor);

        int databaseSizeBeforeUpdate = zoneMonitorRepository.findAll().size();

        // Update the zoneMonitor
        ZoneMonitor updatedZoneMonitor = zoneMonitorRepository.findById(zoneMonitor.getId()).get();
        // Disconnect from session so that the updates on updatedZoneMonitor are not directly saved in db
        em.detach(updatedZoneMonitor);
        updatedZoneMonitor
            .zoneMonitorId(UPDATED_ZONE_MONITOR_ID);

        restZoneMonitorMockMvc.perform(put("/api/zone-monitors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedZoneMonitor)))
            .andExpect(status().isOk());

        // Validate the ZoneMonitor in the database
        List<ZoneMonitor> zoneMonitorList = zoneMonitorRepository.findAll();
        assertThat(zoneMonitorList).hasSize(databaseSizeBeforeUpdate);
        ZoneMonitor testZoneMonitor = zoneMonitorList.get(zoneMonitorList.size() - 1);
        assertThat(testZoneMonitor.getZoneMonitorId()).isEqualTo(UPDATED_ZONE_MONITOR_ID);

        // Validate the ZoneMonitor in Elasticsearch
        verify(mockZoneMonitorSearchRepository, times(1)).save(testZoneMonitor);
    }

    @Test
    @Transactional
    public void updateNonExistingZoneMonitor() throws Exception {
        int databaseSizeBeforeUpdate = zoneMonitorRepository.findAll().size();

        // Create the ZoneMonitor

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restZoneMonitorMockMvc.perform(put("/api/zone-monitors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(zoneMonitor)))
            .andExpect(status().isBadRequest());

        // Validate the ZoneMonitor in the database
        List<ZoneMonitor> zoneMonitorList = zoneMonitorRepository.findAll();
        assertThat(zoneMonitorList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ZoneMonitor in Elasticsearch
        verify(mockZoneMonitorSearchRepository, times(0)).save(zoneMonitor);
    }

    @Test
    @Transactional
    public void deleteZoneMonitor() throws Exception {
        // Initialize the database
        zoneMonitorRepository.saveAndFlush(zoneMonitor);

        int databaseSizeBeforeDelete = zoneMonitorRepository.findAll().size();

        // Get the zoneMonitor
        restZoneMonitorMockMvc.perform(delete("/api/zone-monitors/{id}", zoneMonitor.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ZoneMonitor> zoneMonitorList = zoneMonitorRepository.findAll();
        assertThat(zoneMonitorList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ZoneMonitor in Elasticsearch
        verify(mockZoneMonitorSearchRepository, times(1)).deleteById(zoneMonitor.getId());
    }

    @Test
    @Transactional
    public void searchZoneMonitor() throws Exception {
        // Initialize the database
        zoneMonitorRepository.saveAndFlush(zoneMonitor);
        when(mockZoneMonitorSearchRepository.search(queryStringQuery("id:" + zoneMonitor.getId())))
            .thenReturn(Collections.singletonList(zoneMonitor));
        // Search the zoneMonitor
        restZoneMonitorMockMvc.perform(get("/api/_search/zone-monitors?query=id:" + zoneMonitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(zoneMonitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].zoneMonitorId").value(hasItem(DEFAULT_ZONE_MONITOR_ID)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ZoneMonitor.class);
        ZoneMonitor zoneMonitor1 = new ZoneMonitor();
        zoneMonitor1.setId(1L);
        ZoneMonitor zoneMonitor2 = new ZoneMonitor();
        zoneMonitor2.setId(zoneMonitor1.getId());
        assertThat(zoneMonitor1).isEqualTo(zoneMonitor2);
        zoneMonitor2.setId(2L);
        assertThat(zoneMonitor1).isNotEqualTo(zoneMonitor2);
        zoneMonitor1.setId(null);
        assertThat(zoneMonitor1).isNotEqualTo(zoneMonitor2);
    }
}

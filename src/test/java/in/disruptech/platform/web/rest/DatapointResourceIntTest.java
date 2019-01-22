package in.disruptech.platform.web.rest;

import in.disruptech.platform.PlatformApp;

import in.disruptech.platform.domain.Datapoint;
import in.disruptech.platform.repository.DatapointRepository;
import in.disruptech.platform.repository.search.DatapointSearchRepository;
import in.disruptech.platform.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Test class for the DatapointResource REST controller.
 *
 * @see DatapointResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlatformApp.class)
public class DatapointResourceIntTest {

    private static final String DEFAULT_TAG = "AAAAAAAAAA";
    private static final String UPDATED_TAG = "BBBBBBBBBB";

    private static final Instant DEFAULT_CAPTURE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CAPTURE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_VALUE = 1D;
    private static final Double UPDATED_VALUE = 2D;

    @Autowired
    private DatapointRepository datapointRepository;

    /**
     * This repository is mocked in the in.disruptech.platform.repository.search test package.
     *
     * @see in.disruptech.platform.repository.search.DatapointSearchRepositoryMockConfiguration
     */
    @Autowired
    private DatapointSearchRepository mockDatapointSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDatapointMockMvc;

    private Datapoint datapoint;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DatapointResource datapointResource = new DatapointResource(datapointRepository, mockDatapointSearchRepository);
        this.restDatapointMockMvc = MockMvcBuilders.standaloneSetup(datapointResource)
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
    public static Datapoint createEntity(EntityManager em) {
        Datapoint datapoint = new Datapoint()
            .tag(DEFAULT_TAG)
            .captureTime(DEFAULT_CAPTURE_TIME)
            .value(DEFAULT_VALUE);
        return datapoint;
    }

    @Before
    public void initTest() {
        datapoint = createEntity(em);
    }

    @Test
    @Transactional
    public void createDatapoint() throws Exception {
        int databaseSizeBeforeCreate = datapointRepository.findAll().size();

        // Create the Datapoint
        restDatapointMockMvc.perform(post("/api/datapoints")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(datapoint)))
            .andExpect(status().isCreated());

        // Validate the Datapoint in the database
        List<Datapoint> datapointList = datapointRepository.findAll();
        assertThat(datapointList).hasSize(databaseSizeBeforeCreate + 1);
        Datapoint testDatapoint = datapointList.get(datapointList.size() - 1);
        assertThat(testDatapoint.getTag()).isEqualTo(DEFAULT_TAG);
        assertThat(testDatapoint.getCaptureTime()).isEqualTo(DEFAULT_CAPTURE_TIME);
        assertThat(testDatapoint.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the Datapoint in Elasticsearch
        verify(mockDatapointSearchRepository, times(1)).save(testDatapoint);
    }

    @Test
    @Transactional
    public void createDatapointWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = datapointRepository.findAll().size();

        // Create the Datapoint with an existing ID
        datapoint.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDatapointMockMvc.perform(post("/api/datapoints")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(datapoint)))
            .andExpect(status().isBadRequest());

        // Validate the Datapoint in the database
        List<Datapoint> datapointList = datapointRepository.findAll();
        assertThat(datapointList).hasSize(databaseSizeBeforeCreate);

        // Validate the Datapoint in Elasticsearch
        verify(mockDatapointSearchRepository, times(0)).save(datapoint);
    }

    @Test
    @Transactional
    public void getAllDatapoints() throws Exception {
        // Initialize the database
        datapointRepository.saveAndFlush(datapoint);

        // Get all the datapointList
        restDatapointMockMvc.perform(get("/api/datapoints?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(datapoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG.toString())))
            .andExpect(jsonPath("$.[*].captureTime").value(hasItem(DEFAULT_CAPTURE_TIME.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.doubleValue())));
    }
    
    @Test
    @Transactional
    public void getDatapoint() throws Exception {
        // Initialize the database
        datapointRepository.saveAndFlush(datapoint);

        // Get the datapoint
        restDatapointMockMvc.perform(get("/api/datapoints/{id}", datapoint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(datapoint.getId().intValue()))
            .andExpect(jsonPath("$.tag").value(DEFAULT_TAG.toString()))
            .andExpect(jsonPath("$.captureTime").value(DEFAULT_CAPTURE_TIME.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingDatapoint() throws Exception {
        // Get the datapoint
        restDatapointMockMvc.perform(get("/api/datapoints/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDatapoint() throws Exception {
        // Initialize the database
        datapointRepository.saveAndFlush(datapoint);

        int databaseSizeBeforeUpdate = datapointRepository.findAll().size();

        // Update the datapoint
        Datapoint updatedDatapoint = datapointRepository.findById(datapoint.getId()).get();
        // Disconnect from session so that the updates on updatedDatapoint are not directly saved in db
        em.detach(updatedDatapoint);
        updatedDatapoint
            .tag(UPDATED_TAG)
            .captureTime(UPDATED_CAPTURE_TIME)
            .value(UPDATED_VALUE);

        restDatapointMockMvc.perform(put("/api/datapoints")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDatapoint)))
            .andExpect(status().isOk());

        // Validate the Datapoint in the database
        List<Datapoint> datapointList = datapointRepository.findAll();
        assertThat(datapointList).hasSize(databaseSizeBeforeUpdate);
        Datapoint testDatapoint = datapointList.get(datapointList.size() - 1);
        assertThat(testDatapoint.getTag()).isEqualTo(UPDATED_TAG);
        assertThat(testDatapoint.getCaptureTime()).isEqualTo(UPDATED_CAPTURE_TIME);
        assertThat(testDatapoint.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the Datapoint in Elasticsearch
        verify(mockDatapointSearchRepository, times(1)).save(testDatapoint);
    }

    @Test
    @Transactional
    public void updateNonExistingDatapoint() throws Exception {
        int databaseSizeBeforeUpdate = datapointRepository.findAll().size();

        // Create the Datapoint

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDatapointMockMvc.perform(put("/api/datapoints")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(datapoint)))
            .andExpect(status().isBadRequest());

        // Validate the Datapoint in the database
        List<Datapoint> datapointList = datapointRepository.findAll();
        assertThat(datapointList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Datapoint in Elasticsearch
        verify(mockDatapointSearchRepository, times(0)).save(datapoint);
    }

    @Test
    @Transactional
    public void deleteDatapoint() throws Exception {
        // Initialize the database
        datapointRepository.saveAndFlush(datapoint);

        int databaseSizeBeforeDelete = datapointRepository.findAll().size();

        // Get the datapoint
        restDatapointMockMvc.perform(delete("/api/datapoints/{id}", datapoint.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Datapoint> datapointList = datapointRepository.findAll();
        assertThat(datapointList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Datapoint in Elasticsearch
        verify(mockDatapointSearchRepository, times(1)).deleteById(datapoint.getId());
    }

    @Test
    @Transactional
    public void searchDatapoint() throws Exception {
        // Initialize the database
        datapointRepository.saveAndFlush(datapoint);
        when(mockDatapointSearchRepository.search(queryStringQuery("id:" + datapoint.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(datapoint), PageRequest.of(0, 1), 1));
        // Search the datapoint
        restDatapointMockMvc.perform(get("/api/_search/datapoints?query=id:" + datapoint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(datapoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].tag").value(hasItem(DEFAULT_TAG)))
            .andExpect(jsonPath("$.[*].captureTime").value(hasItem(DEFAULT_CAPTURE_TIME.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Datapoint.class);
        Datapoint datapoint1 = new Datapoint();
        datapoint1.setId(1L);
        Datapoint datapoint2 = new Datapoint();
        datapoint2.setId(datapoint1.getId());
        assertThat(datapoint1).isEqualTo(datapoint2);
        datapoint2.setId(2L);
        assertThat(datapoint1).isNotEqualTo(datapoint2);
        datapoint1.setId(null);
        assertThat(datapoint1).isNotEqualTo(datapoint2);
    }
}

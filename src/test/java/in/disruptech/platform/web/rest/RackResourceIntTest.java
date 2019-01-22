package in.disruptech.platform.web.rest;

import in.disruptech.platform.PlatformApp;

import in.disruptech.platform.domain.Rack;
import in.disruptech.platform.repository.DatapointRepository;
import in.disruptech.platform.repository.MembershipRepository;
import in.disruptech.platform.repository.RackRepository;
import in.disruptech.platform.repository.search.RackSearchRepository;
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
 * Test class for the RackResource REST controller.
 *
 * @see RackResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlatformApp.class)
public class RackResourceIntTest {

    private static final String DEFAULT_RACK_ID = "AAAAAAAAAA";
    private static final String UPDATED_RACK_ID = "BBBBBBBBBB";

    @Autowired
    private RackRepository rackRepository;
    @Autowired
    MembershipRepository membershipRepository;
    /**
     * This repository is mocked in the in.disruptech.platform.repository.search test package.
     *
     * @see in.disruptech.platform.repository.search.RackSearchRepositoryMockConfiguration
     */
    @Autowired
    private RackSearchRepository mockRackSearchRepository;

    @Autowired
    private DatapointRepository datapointRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRackMockMvc;

    private Rack rack;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RackResource rackResource = new RackResource(rackRepository, mockRackSearchRepository, membershipRepository, datapointRepository);
        this.restRackMockMvc = MockMvcBuilders.standaloneSetup(rackResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rack createEntity(EntityManager em) {
        Rack rack = new Rack()
            .rackId(DEFAULT_RACK_ID);
        return rack;
    }

    @Before
    public void initTest() {
        rack = createEntity(em);
    }

    @Test
    @Transactional
    public void createRack() throws Exception {
        int databaseSizeBeforeCreate = rackRepository.findAll().size();

        // Create the Rack
        restRackMockMvc.perform(post("/api/racks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rack)))
            .andExpect(status().isCreated());

        // Validate the Rack in the database
        List<Rack> rackList = rackRepository.findAll();
        assertThat(rackList).hasSize(databaseSizeBeforeCreate + 1);
        Rack testRack = rackList.get(rackList.size() - 1);
        assertThat(testRack.getRackId()).isEqualTo(DEFAULT_RACK_ID);

        // Validate the Rack in Elasticsearch
        verify(mockRackSearchRepository, times(1)).save(testRack);
    }

    @Test
    @Transactional
    public void createRackWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = rackRepository.findAll().size();

        // Create the Rack with an existing ID
        rack.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRackMockMvc.perform(post("/api/racks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rack)))
            .andExpect(status().isBadRequest());

        // Validate the Rack in the database
        List<Rack> rackList = rackRepository.findAll();
        assertThat(rackList).hasSize(databaseSizeBeforeCreate);

        // Validate the Rack in Elasticsearch
        verify(mockRackSearchRepository, times(0)).save(rack);
    }

    @Test
    @Transactional
    public void getAllRacks() throws Exception {
        // Initialize the database
        rackRepository.saveAndFlush(rack);

        // Get all the rackList
        restRackMockMvc.perform(get("/api/racks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rack.getId().intValue())))
            .andExpect(jsonPath("$.[*].rackId").value(hasItem(DEFAULT_RACK_ID.toString())));
    }

    @Test
    @Transactional
    public void getRack() throws Exception {
        // Initialize the database
        rackRepository.saveAndFlush(rack);

        // Get the rack
        restRackMockMvc.perform(get("/api/racks/{id}", rack.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(rack.getId().intValue()))
            .andExpect(jsonPath("$.rackId").value(DEFAULT_RACK_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRack() throws Exception {
        // Get the rack
        restRackMockMvc.perform(get("/api/racks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRack() throws Exception {
        // Initialize the database
        rackRepository.saveAndFlush(rack);

        int databaseSizeBeforeUpdate = rackRepository.findAll().size();

        // Update the rack
        Rack updatedRack = rackRepository.findById(rack.getId()).get();
        // Disconnect from session so that the updates on updatedRack are not directly saved in db
        em.detach(updatedRack);
        updatedRack
            .rackId(UPDATED_RACK_ID);

        restRackMockMvc.perform(put("/api/racks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRack)))
            .andExpect(status().isOk());

        // Validate the Rack in the database
        List<Rack> rackList = rackRepository.findAll();
        assertThat(rackList).hasSize(databaseSizeBeforeUpdate);
        Rack testRack = rackList.get(rackList.size() - 1);
        assertThat(testRack.getRackId()).isEqualTo(UPDATED_RACK_ID);

        // Validate the Rack in Elasticsearch
        verify(mockRackSearchRepository, times(1)).save(testRack);
    }

    @Test
    @Transactional
    public void updateNonExistingRack() throws Exception {
        int databaseSizeBeforeUpdate = rackRepository.findAll().size();

        // Create the Rack

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRackMockMvc.perform(put("/api/racks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rack)))
            .andExpect(status().isBadRequest());

        // Validate the Rack in the database
        List<Rack> rackList = rackRepository.findAll();
        assertThat(rackList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Rack in Elasticsearch
        verify(mockRackSearchRepository, times(0)).save(rack);
    }

    @Test
    @Transactional
    public void deleteRack() throws Exception {
        // Initialize the database
        rackRepository.saveAndFlush(rack);

        int databaseSizeBeforeDelete = rackRepository.findAll().size();

        // Get the rack
        restRackMockMvc.perform(delete("/api/racks/{id}", rack.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Rack> rackList = rackRepository.findAll();
        assertThat(rackList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Rack in Elasticsearch
        verify(mockRackSearchRepository, times(1)).deleteById(rack.getId());
    }

    @Test
    @Transactional
    public void searchRack() throws Exception {
        // Initialize the database
        rackRepository.saveAndFlush(rack);
        when(mockRackSearchRepository.search(queryStringQuery("id:" + rack.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(rack), PageRequest.of(0, 1), 1));
        // Search the rack
        restRackMockMvc.perform(get("/api/_search/racks?query=id:" + rack.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rack.getId().intValue())))
            .andExpect(jsonPath("$.[*].rackId").value(hasItem(DEFAULT_RACK_ID)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rack.class);
        Rack rack1 = new Rack();
        rack1.setId(1L);
        Rack rack2 = new Rack();
        rack2.setId(rack1.getId());
        assertThat(rack1).isEqualTo(rack2);
        rack2.setId(2L);
        assertThat(rack1).isNotEqualTo(rack2);
        rack1.setId(null);
        assertThat(rack1).isNotEqualTo(rack2);
    }
}

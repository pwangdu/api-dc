package in.disruptech.platform.web.rest;

import in.disruptech.platform.PlatformApp;

import in.disruptech.platform.domain.Server;
import in.disruptech.platform.repository.ServerRepository;
import in.disruptech.platform.repository.search.ServerSearchRepository;
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
 * Test class for the ServerResource REST controller.
 *
 * @see ServerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PlatformApp.class)
public class ServerResourceIntTest {

    private static final String DEFAULT_SERVER_ID = "AAAAAAAAAA";
    private static final String UPDATED_SERVER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SERVER_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_SERVER_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_SERVER_MANUFACTURER = "AAAAAAAAAA";
    private static final String UPDATED_SERVER_MANUFACTURER = "BBBBBBBBBB";

    @Autowired
    private ServerRepository serverRepository;

    /**
     * This repository is mocked in the in.disruptech.platform.repository.search test package.
     *
     * @see in.disruptech.platform.repository.search.ServerSearchRepositoryMockConfiguration
     */
    @Autowired
    private ServerSearchRepository mockServerSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restServerMockMvc;

    private Server server;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ServerResource serverResource = new ServerResource(serverRepository, mockServerSearchRepository);
        this.restServerMockMvc = MockMvcBuilders.standaloneSetup(serverResource)
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
    public static Server createEntity(EntityManager em) {
        Server server = new Server()
            .serverId(DEFAULT_SERVER_ID)
            .serverModel(DEFAULT_SERVER_MODEL)
            .serverManufacturer(DEFAULT_SERVER_MANUFACTURER);
        return server;
    }

    @Before
    public void initTest() {
        server = createEntity(em);
    }

    @Test
    @Transactional
    public void createServer() throws Exception {
        int databaseSizeBeforeCreate = serverRepository.findAll().size();

        // Create the Server
        restServerMockMvc.perform(post("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isCreated());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeCreate + 1);
        Server testServer = serverList.get(serverList.size() - 1);
        assertThat(testServer.getServerId()).isEqualTo(DEFAULT_SERVER_ID);
        assertThat(testServer.getServerModel()).isEqualTo(DEFAULT_SERVER_MODEL);
        assertThat(testServer.getServerManufacturer()).isEqualTo(DEFAULT_SERVER_MANUFACTURER);

        // Validate the Server in Elasticsearch
        verify(mockServerSearchRepository, times(1)).save(testServer);
    }

    @Test
    @Transactional
    public void createServerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = serverRepository.findAll().size();

        // Create the Server with an existing ID
        server.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restServerMockMvc.perform(post("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isBadRequest());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeCreate);

        // Validate the Server in Elasticsearch
        verify(mockServerSearchRepository, times(0)).save(server);
    }

    @Test
    @Transactional
    public void getAllServers() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get all the serverList
        restServerMockMvc.perform(get("/api/servers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(server.getId().intValue())))
            .andExpect(jsonPath("$.[*].serverId").value(hasItem(DEFAULT_SERVER_ID.toString())))
            .andExpect(jsonPath("$.[*].serverModel").value(hasItem(DEFAULT_SERVER_MODEL.toString())))
            .andExpect(jsonPath("$.[*].serverManufacturer").value(hasItem(DEFAULT_SERVER_MANUFACTURER.toString())));
    }
    
    @Test
    @Transactional
    public void getServer() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        // Get the server
        restServerMockMvc.perform(get("/api/servers/{id}", server.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(server.getId().intValue()))
            .andExpect(jsonPath("$.serverId").value(DEFAULT_SERVER_ID.toString()))
            .andExpect(jsonPath("$.serverModel").value(DEFAULT_SERVER_MODEL.toString()))
            .andExpect(jsonPath("$.serverManufacturer").value(DEFAULT_SERVER_MANUFACTURER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingServer() throws Exception {
        // Get the server
        restServerMockMvc.perform(get("/api/servers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateServer() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        int databaseSizeBeforeUpdate = serverRepository.findAll().size();

        // Update the server
        Server updatedServer = serverRepository.findById(server.getId()).get();
        // Disconnect from session so that the updates on updatedServer are not directly saved in db
        em.detach(updatedServer);
        updatedServer
            .serverId(UPDATED_SERVER_ID)
            .serverModel(UPDATED_SERVER_MODEL)
            .serverManufacturer(UPDATED_SERVER_MANUFACTURER);

        restServerMockMvc.perform(put("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedServer)))
            .andExpect(status().isOk());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeUpdate);
        Server testServer = serverList.get(serverList.size() - 1);
        assertThat(testServer.getServerId()).isEqualTo(UPDATED_SERVER_ID);
        assertThat(testServer.getServerModel()).isEqualTo(UPDATED_SERVER_MODEL);
        assertThat(testServer.getServerManufacturer()).isEqualTo(UPDATED_SERVER_MANUFACTURER);

        // Validate the Server in Elasticsearch
        verify(mockServerSearchRepository, times(1)).save(testServer);
    }

    @Test
    @Transactional
    public void updateNonExistingServer() throws Exception {
        int databaseSizeBeforeUpdate = serverRepository.findAll().size();

        // Create the Server

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServerMockMvc.perform(put("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isBadRequest());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Server in Elasticsearch
        verify(mockServerSearchRepository, times(0)).save(server);
    }

    @Test
    @Transactional
    public void deleteServer() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);

        int databaseSizeBeforeDelete = serverRepository.findAll().size();

        // Get the server
        restServerMockMvc.perform(delete("/api/servers/{id}", server.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Server in Elasticsearch
        verify(mockServerSearchRepository, times(1)).deleteById(server.getId());
    }

    @Test
    @Transactional
    public void searchServer() throws Exception {
        // Initialize the database
        serverRepository.saveAndFlush(server);
        when(mockServerSearchRepository.search(queryStringQuery("id:" + server.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(server), PageRequest.of(0, 1), 1));
        // Search the server
        restServerMockMvc.perform(get("/api/_search/servers?query=id:" + server.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(server.getId().intValue())))
            .andExpect(jsonPath("$.[*].serverId").value(hasItem(DEFAULT_SERVER_ID)))
            .andExpect(jsonPath("$.[*].serverModel").value(hasItem(DEFAULT_SERVER_MODEL)))
            .andExpect(jsonPath("$.[*].serverManufacturer").value(hasItem(DEFAULT_SERVER_MANUFACTURER)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Server.class);
        Server server1 = new Server();
        server1.setId(1L);
        Server server2 = new Server();
        server2.setId(server1.getId());
        assertThat(server1).isEqualTo(server2);
        server2.setId(2L);
        assertThat(server1).isNotEqualTo(server2);
        server1.setId(null);
        assertThat(server1).isNotEqualTo(server2);
    }
}

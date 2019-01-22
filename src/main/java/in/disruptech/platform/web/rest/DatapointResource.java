package in.disruptech.platform.web.rest;

import com.codahale.metrics.annotation.Timed;
import in.disruptech.platform.domain.Datapoint;
import in.disruptech.platform.repository.DatapointRepository;
import in.disruptech.platform.repository.search.DatapointSearchRepository;
import in.disruptech.platform.web.rest.errors.BadRequestAlertException;
import in.disruptech.platform.web.rest.util.HeaderUtil;
import in.disruptech.platform.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Datapoint.
 */
@RestController
@RequestMapping("/api")
public class DatapointResource {

    private final Logger log = LoggerFactory.getLogger(DatapointResource.class);

    private static final String ENTITY_NAME = "datapoint";

    private final DatapointRepository datapointRepository;

    private final DatapointSearchRepository datapointSearchRepository;

    public DatapointResource(DatapointRepository datapointRepository, DatapointSearchRepository datapointSearchRepository) {
        this.datapointRepository = datapointRepository;
        this.datapointSearchRepository = datapointSearchRepository;
    }

    /**
     * POST  /datapoints : Create a new datapoint.
     *
     * @param datapoint the datapoint to create
     * @return the ResponseEntity with status 201 (Created) and with body the new datapoint, or with status 400 (Bad Request) if the datapoint has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/datapoints")
    @Timed
    public ResponseEntity<Datapoint> createDatapoint(@RequestBody Datapoint datapoint) throws URISyntaxException {
        log.debug("REST request to save Datapoint : {}", datapoint);
        if (datapoint.getId() != null) {
            throw new BadRequestAlertException("A new datapoint cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Datapoint result = datapointRepository.save(datapoint);
        datapointSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/datapoints/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /datapoints : Updates an existing datapoint.
     *
     * @param datapoint the datapoint to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated datapoint,
     * or with status 400 (Bad Request) if the datapoint is not valid,
     * or with status 500 (Internal Server Error) if the datapoint couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/datapoints")
    @Timed
    public ResponseEntity<Datapoint> updateDatapoint(@RequestBody Datapoint datapoint) throws URISyntaxException {
        log.debug("REST request to update Datapoint : {}", datapoint);
        if (datapoint.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Datapoint result = datapointRepository.save(datapoint);
        datapointSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, datapoint.getId().toString()))
            .body(result);
    }

    /**
     * GET  /datapoints : get all the datapoints.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of datapoints in body
     */
    @GetMapping("/datapoints")
    @Timed
    public ResponseEntity<List<Datapoint>> getAllDatapoints(Pageable pageable) {
        log.debug("REST request to get a page of Datapoints");
        Page<Datapoint> page = datapointRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/datapoints");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /datapoints/:id : get the "id" datapoint.
     *
     * @param id the id of the datapoint to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the datapoint, or with status 404 (Not Found)
     */
    @GetMapping("/datapoints/{id}")
    @Timed
    public ResponseEntity<Datapoint> getDatapoint(@PathVariable Long id) {
        log.debug("REST request to get Datapoint : {}", id);
        Optional<Datapoint> datapoint = datapointRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(datapoint);
    }

    /**
     * DELETE  /datapoints/:id : delete the "id" datapoint.
     *
     * @param id the id of the datapoint to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/datapoints/{id}")
    @Timed
    public ResponseEntity<Void> deleteDatapoint(@PathVariable Long id) {
        log.debug("REST request to delete Datapoint : {}", id);

        datapointRepository.deleteById(id);
        datapointSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/datapoints?query=:query : search for the datapoint corresponding
     * to the query.
     *
     * @param query the query of the datapoint search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/datapoints")
    @Timed
    public ResponseEntity<List<Datapoint>> searchDatapoints(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Datapoints for query {}", query);
        Page<Datapoint> page = datapointSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/datapoints");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

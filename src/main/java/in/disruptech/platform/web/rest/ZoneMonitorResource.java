package in.disruptech.platform.web.rest;

import com.codahale.metrics.annotation.Timed;
import in.disruptech.platform.domain.ZoneMonitor;
import in.disruptech.platform.repository.ZoneMonitorRepository;
import in.disruptech.platform.repository.search.ZoneMonitorSearchRepository;
import in.disruptech.platform.web.rest.errors.BadRequestAlertException;
import in.disruptech.platform.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing ZoneMonitor.
 */
@RestController
@RequestMapping("/api")
public class ZoneMonitorResource {

    private final Logger log = LoggerFactory.getLogger(ZoneMonitorResource.class);

    private static final String ENTITY_NAME = "zoneMonitor";

    private final ZoneMonitorRepository zoneMonitorRepository;

    private final ZoneMonitorSearchRepository zoneMonitorSearchRepository;

    public ZoneMonitorResource(ZoneMonitorRepository zoneMonitorRepository, ZoneMonitorSearchRepository zoneMonitorSearchRepository) {
        this.zoneMonitorRepository = zoneMonitorRepository;
        this.zoneMonitorSearchRepository = zoneMonitorSearchRepository;
    }

    /**
     * POST  /zone-monitors : Create a new zoneMonitor.
     *
     * @param zoneMonitor the zoneMonitor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new zoneMonitor, or with status 400 (Bad Request) if the zoneMonitor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/zone-monitors")
    @Timed
    public ResponseEntity<ZoneMonitor> createZoneMonitor(@RequestBody ZoneMonitor zoneMonitor) throws URISyntaxException {
        log.debug("REST request to save ZoneMonitor : {}", zoneMonitor);
        if (zoneMonitor.getId() != null) {
            throw new BadRequestAlertException("A new zoneMonitor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ZoneMonitor result = zoneMonitorRepository.save(zoneMonitor);
        zoneMonitorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/zone-monitors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /zone-monitors : Updates an existing zoneMonitor.
     *
     * @param zoneMonitor the zoneMonitor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated zoneMonitor,
     * or with status 400 (Bad Request) if the zoneMonitor is not valid,
     * or with status 500 (Internal Server Error) if the zoneMonitor couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/zone-monitors")
    @Timed
    public ResponseEntity<ZoneMonitor> updateZoneMonitor(@RequestBody ZoneMonitor zoneMonitor) throws URISyntaxException {
        log.debug("REST request to update ZoneMonitor : {}", zoneMonitor);
        if (zoneMonitor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ZoneMonitor result = zoneMonitorRepository.save(zoneMonitor);
        zoneMonitorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, zoneMonitor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /zone-monitors : get all the zoneMonitors.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of zoneMonitors in body
     */
    @GetMapping("/zone-monitors")
    @Timed
    public List<ZoneMonitor> getAllZoneMonitors() {
        log.debug("REST request to get all ZoneMonitors");
        return zoneMonitorRepository.findAll();
    }

    /**
     * GET  /zone-monitors/:id : get the "id" zoneMonitor.
     *
     * @param id the id of the zoneMonitor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the zoneMonitor, or with status 404 (Not Found)
     */
    @GetMapping("/zone-monitors/{id}")
    @Timed
    public ResponseEntity<ZoneMonitor> getZoneMonitor(@PathVariable Long id) {
        log.debug("REST request to get ZoneMonitor : {}", id);
        Optional<ZoneMonitor> zoneMonitor = zoneMonitorRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(zoneMonitor);
    }

    /**
     * DELETE  /zone-monitors/:id : delete the "id" zoneMonitor.
     *
     * @param id the id of the zoneMonitor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/zone-monitors/{id}")
    @Timed
    public ResponseEntity<Void> deleteZoneMonitor(@PathVariable Long id) {
        log.debug("REST request to delete ZoneMonitor : {}", id);

        zoneMonitorRepository.deleteById(id);
        zoneMonitorSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/zone-monitors?query=:query : search for the zoneMonitor corresponding
     * to the query.
     *
     * @param query the query of the zoneMonitor search
     * @return the result of the search
     */
    @GetMapping("/_search/zone-monitors")
    @Timed
    public List<ZoneMonitor> searchZoneMonitors(@RequestParam String query) {
        log.debug("REST request to search ZoneMonitors for query {}", query);
        return StreamSupport
            .stream(zoneMonitorSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}

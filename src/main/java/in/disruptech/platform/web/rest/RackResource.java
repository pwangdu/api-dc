package in.disruptech.platform.web.rest;

import com.codahale.metrics.annotation.Timed;
import in.disruptech.platform.domain.Rack;
import in.disruptech.platform.domain.dto.RackDTO;
import in.disruptech.platform.repository.DatapointRepository;
import in.disruptech.platform.repository.MembershipRepository;
import in.disruptech.platform.repository.RackRepository;
import in.disruptech.platform.repository.search.RackSearchRepository;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Rack.
 */
@RestController
@RequestMapping("/api")
public class RackResource {

    private final Logger log = LoggerFactory.getLogger(RackResource.class);

    private static final String ENTITY_NAME = "rack";

    private final RackRepository rackRepository;

    private final RackSearchRepository rackSearchRepository;

    private final MembershipRepository membershipRepository;

    private final DatapointRepository datapointRepository;

    public RackResource(RackRepository rackRepository, RackSearchRepository rackSearchRepository, MembershipRepository membershipRepository, DatapointRepository datapointRepository) {
        this.rackRepository = rackRepository;
        this.rackSearchRepository = rackSearchRepository;
        this.membershipRepository = membershipRepository;
        this.datapointRepository = datapointRepository;
    }

    /**
     * POST  /racks : Create a new rack.
     *
     * @param rack the rack to create
     * @return the ResponseEntity with status 201 (Created) and with body the new rack, or with status 400 (Bad Request) if the rack has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/racks")
    @Timed
    public ResponseEntity<RackDTO> createRack(@RequestBody Rack rack) throws URISyntaxException {
        log.debug("REST request to save Rack : {}", rack);
        if (rack.getId() != null) {
            throw new BadRequestAlertException("A new rack cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Rack result = rackRepository.save(rack);
        rackSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/racks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(toDto(rack));
    }

    /**
     * PUT  /racks : Updates an existing rack.
     *
     * @param rack the rack to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated rack,
     * or with status 400 (Bad Request) if the rack is not valid,
     * or with status 500 (Internal Server Error) if the rack couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/racks")
    @Timed
    public ResponseEntity<RackDTO> updateRack(@RequestBody Rack rack) throws URISyntaxException {
        log.debug("REST request to update Rack : {}", rack);
        if (rack.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Rack result = rackRepository.save(rack);
        rackSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, rack.getId().toString()))
            .body(toDto(result));
    }

    /**
     * GET  /racks : get all the racks.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of racks in body
     */
    @GetMapping("/racks")
    @Timed
    public ResponseEntity<List<RackDTO>> getAllRacks(Pageable pageable) {
        log.debug("REST request to get a page of Racks");
        Page<Rack> page = rackRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/racks");
        return ResponseEntity.ok().headers(headers).body(page.getContent().stream().map(rack -> toDto(rack)).collect(Collectors.toList()));
    }

    /**
     * GET  /racks/:id : get the "id" rack.
     *
     * @param id the id of the rack to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the rack, or with status 404 (Not Found)
     */
    @GetMapping("/racks/{id}")
    @Timed
    public ResponseEntity<RackDTO> getRack(@PathVariable Long id) {
        log.debug("REST request to get Rack : {}", id);
        Optional<Rack> result = rackRepository.findById(id);
        final RackDTO[] dto = {null};
        result.ifPresent(rack -> dto[0] = toDto(rack));
        Optional<RackDTO> rackDTO = Optional.ofNullable(dto[0]);
        return ResponseUtil.wrapOrNotFound(rackDTO);
    }

    static Random random = new Random();

    private RackDTO toDto(Rack rack) {
        RackDTO rackDTO = new RackDTO();
        rackDTO.setId(rack.getId());
        rackDTO.setRackId(rack.getRackId());
        rackDTO.setZoneMonitor(rack.getZoneMonitor());
        rackDTO.setServers(membershipRepository.getServersInRack(rack).stream().map(res -> res.getServer()).collect(Collectors.toList()));
        return rackDTO;
    }

    /**
     * DELETE  /racks/:id : delete the "id" rack.
     *
     * @param id the id of the rack to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/racks/{id}")
    @Timed
    public ResponseEntity<Void> deleteRack(@PathVariable Long id) {
        log.debug("REST request to delete Rack : {}", id);

        rackRepository.deleteById(id);
        rackSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/racks?query=:query : search for the rack corresponding
     * to the query.
     *
     * @param query    the query of the rack search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/racks")
    @Timed
    public ResponseEntity<List<RackDTO>> searchRacks(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Racks for query {}", query);
        Page<Rack> page = rackSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/racks");
        List<RackDTO> list = page.getContent().stream().map(rack -> toDto(rack)).sorted().collect(Collectors.toList());
        list.sort(RackDTO::compareTo);
        return new ResponseEntity<>(list, headers, HttpStatus.OK);
    }
}

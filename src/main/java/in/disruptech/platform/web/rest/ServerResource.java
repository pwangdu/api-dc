package in.disruptech.platform.web.rest;

import com.codahale.metrics.annotation.Timed;
import in.disruptech.platform.domain.Server;
import in.disruptech.platform.repository.ServerRepository;
import in.disruptech.platform.repository.search.ServerSearchRepository;
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
 * REST controller for managing Server.
 */
@RestController
@RequestMapping("/api")
public class ServerResource {

    private final Logger log = LoggerFactory.getLogger(ServerResource.class);

    private static final String ENTITY_NAME = "server";

    private final ServerRepository serverRepository;

    private final ServerSearchRepository serverSearchRepository;

    public ServerResource(ServerRepository serverRepository, ServerSearchRepository serverSearchRepository) {
        this.serverRepository = serverRepository;
        this.serverSearchRepository = serverSearchRepository;
    }

    /**
     * POST  /servers : Create a new server.
     *
     * @param server the server to create
     * @return the ResponseEntity with status 201 (Created) and with body the new server, or with status 400 (Bad Request) if the server has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/servers")
    @Timed
    public ResponseEntity<Server> createServer(@RequestBody Server server) throws URISyntaxException {
        log.debug("REST request to save Server : {}", server);
        if (server.getId() != null) {
            throw new BadRequestAlertException("A new server cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Server result = serverRepository.save(server);
        serverSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/servers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /servers : Updates an existing server.
     *
     * @param server the server to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated server,
     * or with status 400 (Bad Request) if the server is not valid,
     * or with status 500 (Internal Server Error) if the server couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/servers")
    @Timed
    public ResponseEntity<Server> updateServer(@RequestBody Server server) throws URISyntaxException {
        log.debug("REST request to update Server : {}", server);
        if (server.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Server result = serverRepository.save(server);
        serverSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, server.getId().toString()))
            .body(result);
    }

    /**
     * GET  /servers : get all the servers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of servers in body
     */
    @GetMapping("/servers")
    @Timed
    public ResponseEntity<List<Server>> getAllServers(Pageable pageable) {
        log.debug("REST request to get a page of Servers");
        Page<Server> page = serverRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/servers");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /servers/:id : get the "id" server.
     *
     * @param id the id of the server to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the server, or with status 404 (Not Found)
     */
    @GetMapping("/servers/{id}")
    @Timed
    public ResponseEntity<Server> getServer(@PathVariable Long id) {
        log.debug("REST request to get Server : {}", id);
        Optional<Server> server = serverRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(server);
    }

    /**
     * DELETE  /servers/:id : delete the "id" server.
     *
     * @param id the id of the server to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/servers/{id}")
    @Timed
    public ResponseEntity<Void> deleteServer(@PathVariable Long id) {
        log.debug("REST request to delete Server : {}", id);

        serverRepository.deleteById(id);
        serverSearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/servers?query=:query : search for the server corresponding
     * to the query.
     *
     * @param query the query of the server search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/servers")
    @Timed
    public ResponseEntity<List<Server>> searchServers(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Servers for query {}", query);
        Page<Server> page = serverSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/servers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}

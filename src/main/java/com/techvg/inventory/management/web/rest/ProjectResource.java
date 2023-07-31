package com.techvg.inventory.management.web.rest;

import com.techvg.inventory.management.domain.enumeration.OrderType;
import com.techvg.inventory.management.domain.enumeration.Status;
import com.techvg.inventory.management.repository.ProjectRepository;
import com.techvg.inventory.management.service.ProjectQueryService;
import com.techvg.inventory.management.service.ProjectService;
import com.techvg.inventory.management.service.PurchaseQuotationQueryService;
import com.techvg.inventory.management.service.criteria.ProjectCriteria;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria.OrderTypeFilter;
import com.techvg.inventory.management.service.criteria.PurchaseQuotationCriteria.StatusFilter;
import com.techvg.inventory.management.service.dto.ProjectDTO;
import com.techvg.inventory.management.service.dto.PurchaseQuotationDTO;
import com.techvg.inventory.management.service.dto.SecurityUserDTO;
import com.techvg.inventory.management.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing
 * {@link com.techvg.inventory.management.domain.Project}.
 */
@RestController
@RequestMapping("/api")
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "project";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectService projectService;

    private final ProjectRepository projectRepository;

    private final ProjectQueryService projectQueryService;

    @Autowired
    private PurchaseQuotationQueryService purchaseQuotationQueryService;

    public ProjectResource(ProjectService projectService, ProjectRepository projectRepository, ProjectQueryService projectQueryService) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.projectQueryService = projectQueryService;
    }

    /**
     * {@code POST  /projects} : Create a new project.
     *
     * @param projectDTO the projectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new projectDTO, or with status {@code 400 (Bad Request)} if
     *         the project has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/projects")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) throws URISyntaxException {
        log.debug("REST request to save Project : {}", projectDTO);
        if (projectDTO.getId() != null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity
            .created(new URI("/api/projects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /projects/:id} : Updates an existing project.
     *
     * @param id         the id of the projectDTO to save.
     * @param projectDTO the projectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated projectDTO, or with status {@code 400 (Bad Request)} if
     *         the projectDTO is not valid, or with status
     *         {@code 500 (Internal Server Error)} if the projectDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProjectDTO projectDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Project : {}, {}", id, projectDTO);
        if (projectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ProjectDTO result = projectService.save(projectDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /projects/:id} : Partial updates given fields of an existing
     * project, field will ignore if it is null
     *
     * @param id         the id of the projectDTO to save.
     * @param projectDTO the projectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated projectDTO, or with status {@code 400 (Bad Request)} if
     *         the projectDTO is not valid, or with status {@code 404 (Not Found)}
     *         if the projectDTO is not found, or with status
     *         {@code 500 (Internal Server Error)} if the projectDTO couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/projects/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProjectDTO> partialUpdateProject(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProjectDTO projectDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Project partially : {}, {}", id, projectDTO);
        if (projectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!projectRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProjectDTO> result = projectService.partialUpdate(projectDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, projectDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /projects} : get all the projects.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of projects in body.
     */
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectDTO>> getAllProjects(
        ProjectCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Projects by criteria: {}", criteria);

        Page<ProjectDTO> page = projectQueryService.findByCriteria(criteria, pageable);

        for (ProjectDTO projectDTO : page.getContent()) {
            PurchaseQuotationCriteria pocriteria = new PurchaseQuotationCriteria();

            LongFilter projectId = new LongFilter();
            projectId.setEquals(projectDTO.getId());
            pocriteria.setProjectId(projectId);

            OrderTypeFilter orderType = new OrderTypeFilter();
            orderType.setEquals(OrderType.PURCHASE_ORDER);
            pocriteria.setOrderType(orderType);

            StatusFilter orderStatus = new StatusFilter();
            List<Status> in = new ArrayList<>();
            in.add(Status.COMPLETED);
            orderStatus.setIn(in);
            pocriteria.setOrderStatus(orderStatus);

            // addition of total purchase order
            List<PurchaseQuotationDTO> pagePo = purchaseQuotationQueryService.findByCriteria(pocriteria);

            double totalPoAmount = 0;
            for (PurchaseQuotationDTO po : pagePo) {
                totalPoAmount += po.getTotalPOAmount();
            }

            projectDTO.setUsedBudget(totalPoAmount);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /projects/count} : count all the projects.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count
     *         in body.
     */
    @GetMapping("/projects/count")
    public ResponseEntity<Long> countProjects(ProjectCriteria criteria) {
        log.debug("REST request to count Projects by criteria: {}", criteria);
        return ResponseEntity.ok().body(projectQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /projects/:id} : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the projectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        log.debug("REST request to get Project : {}", id);
        Optional<ProjectDTO> projectDTO = projectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectDTO);
    }

    /**
     * {@code DELETE  /projects/:id} : delete the "id" project.
     *
     * @param id the id of the projectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.debug("REST request to delete Project : {}", id);
        projectService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

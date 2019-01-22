package in.disruptech.platform.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Rack.
 */
@Entity
@Table(name = "rack")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "rack")
public class Rack implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "rack_id")
    private String rackId;

    @OneToOne    @JoinColumn(unique = true)
    private ZoneMonitor zoneMonitor;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRackId() {
        return rackId;
    }

    public Rack rackId(String rackId) {
        this.rackId = rackId;
        return this;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public ZoneMonitor getZoneMonitor() {
        return zoneMonitor;
    }

    public Rack zoneMonitor(ZoneMonitor zoneMonitor) {
        this.zoneMonitor = zoneMonitor;
        return this;
    }

    public void setZoneMonitor(ZoneMonitor zoneMonitor) {
        this.zoneMonitor = zoneMonitor;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rack rack = (Rack) o;
        if (rack.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), rack.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Rack{" +
            "id=" + getId() +
            ", rackId='" + getRackId() + "'" +
            "}";
    }
}

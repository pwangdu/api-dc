package in.disruptech.platform.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ZoneMonitor.
 */
@Entity
@Table(name = "zone_monitor")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "zonemonitor")
public class ZoneMonitor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "zone_monitor_id")
    private String zoneMonitorId;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZoneMonitorId() {
        return zoneMonitorId;
    }

    public ZoneMonitor zoneMonitorId(String zoneMonitorId) {
        this.zoneMonitorId = zoneMonitorId;
        return this;
    }

    public void setZoneMonitorId(String zoneMonitorId) {
        this.zoneMonitorId = zoneMonitorId;
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
        ZoneMonitor zoneMonitor = (ZoneMonitor) o;
        if (zoneMonitor.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), zoneMonitor.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ZoneMonitor{" +
            "id=" + getId() +
            ", zoneMonitorId='" + getZoneMonitorId() + "'" +
            "}";
    }
}

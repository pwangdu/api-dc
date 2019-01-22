package in.disruptech.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Datapoint.
 */
@Entity
@Table(name = "datapoint")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "datapoint")
public class Datapoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "tag")
    private String tag;

    @Column(name = "capture_time")
    private Instant captureTime;

    @Column(name = "jhi_value")
    private Double value;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Rack rack;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public Datapoint tag(String tag) {
        this.tag = tag;
        return this;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Instant getCaptureTime() {
        return captureTime;
    }

    public Datapoint captureTime(Instant captureTime) {
        this.captureTime = captureTime;
        return this;
    }

    public void setCaptureTime(Instant captureTime) {
        this.captureTime = captureTime;
    }

    public Double getValue() {
        return value;
    }

    public Datapoint value(Double value) {
        this.value = value;
        return this;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Rack getRack() {
        return rack;
    }

    public Datapoint rack(Rack rack) {
        this.rack = rack;
        return this;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
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
        Datapoint datapoint = (Datapoint) o;
        if (datapoint.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), datapoint.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Datapoint{" +
            "id=" + getId() +
            ", tag='" + getTag() + "'" +
            ", captureTime='" + getCaptureTime() + "'" +
            ", value=" + getValue() +
            "}";
    }
}

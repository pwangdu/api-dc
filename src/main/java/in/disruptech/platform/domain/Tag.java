package in.disruptech.platform.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Tag.
 */
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "tag_id")
    private String tagId;

    @Column(name = "remaining_battery")
    private Double remainingBattery;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagId() {
        return tagId;
    }

    public Tag tagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Double getRemainingBattery() {
        return remainingBattery;
    }

    public Tag remainingBattery(Double remainingBattery) {
        this.remainingBattery = remainingBattery;
        return this;
    }

    public void setRemainingBattery(Double remainingBattery) {
        this.remainingBattery = remainingBattery;
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
        Tag tag = (Tag) o;
        if (tag.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Tag{" +
            "id=" + getId() +
            ", tagId='" + getTagId() + "'" +
            ", remainingBattery=" + getRemainingBattery() +
            "}";
    }
}

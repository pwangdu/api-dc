package in.disruptech.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.sql.Date;
import java.time.Instant;
import java.util.Objects;

/**
 * A Membership.
 */
@Entity
@Table(name = "membership")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "membership")
public class Membership implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Server server;

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

    public Instant getStartTime() {
        return startTime;
    }

    public Membership startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Membership endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Server getServer() {
        return server;
    }

    public Membership server(Server server) {
        this.server = server;
        return this;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Rack getRack() {
        return rack;
    }

    public Membership rack(Rack rack) {
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
        Membership membership = (Membership) o;
        if (membership.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), membership.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Membership{" +
            "id=" + getId() +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }

    public String getDuration() {
        long diff = ((endTime == null) ? Date.from(Instant.now()).getTime() : Date.from(endTime).getTime()) - Date.from(startTime).getTime();
        int diffInDays = (int) (diff / (1000 * 60 * 60 * 24));
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        if (diffInDays >= 1) {
            return diffInDays + " days";
        } else if (diffHours >= 1) {
            return diffHours + " hours";
        } else if (diffMinutes >= 1) {
            return diffMinutes + " minutes";
        }
        return "Just now";
    }
}

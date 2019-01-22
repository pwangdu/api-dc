package in.disruptech.platform.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Server.
 */
@Entity
@Table(name = "server")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "server")
public class Server implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "server_id")
    private String serverId;

    @Column(name = "server_model")
    private String serverModel;

    @Column(name = "server_manufacturer")
    private String serverManufacturer;

    @OneToOne    @JoinColumn(unique = true)
    private Tag tag;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public Server serverId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerModel() {
        return serverModel;
    }

    public Server serverModel(String serverModel) {
        this.serverModel = serverModel;
        return this;
    }

    public void setServerModel(String serverModel) {
        this.serverModel = serverModel;
    }

    public String getServerManufacturer() {
        return serverManufacturer;
    }

    public Server serverManufacturer(String serverManufacturer) {
        this.serverManufacturer = serverManufacturer;
        return this;
    }

    public void setServerManufacturer(String serverManufacturer) {
        this.serverManufacturer = serverManufacturer;
    }

    public Tag getTag() {
        return tag;
    }

    public Server tag(Tag tag) {
        this.tag = tag;
        return this;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
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
        Server server = (Server) o;
        if (server.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), server.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Server{" +
            "id=" + getId() +
            ", serverId='" + getServerId() + "'" +
            ", serverModel='" + getServerModel() + "'" +
            ", serverManufacturer='" + getServerManufacturer() + "'" +
            "}";
    }
}

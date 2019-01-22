package in.disruptech.platform.domain.dto;

import in.disruptech.platform.domain.Server;
import in.disruptech.platform.domain.ZoneMonitor;

import java.util.List;
import java.util.Objects;

public class RackDTO implements Comparable<RackDTO> {

    private Long id;
    private String rackId;
    private ZoneMonitor zoneMonitor;
    private List<Server> servers;
    private Double temprature;
    private Double humidity;

    public RackDTO() {
    }

    public RackDTO(Long id, String rackId, ZoneMonitor zoneMonitor, List<Server> servers, Double temprature, Double humidity) {
        this.id = id;
        this.rackId = rackId;
        this.zoneMonitor = zoneMonitor;
        this.servers = servers;
        this.temprature = temprature;
        this.humidity = humidity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public ZoneMonitor getZoneMonitor() {
        return zoneMonitor;
    }

    public void setZoneMonitor(ZoneMonitor zoneMonitor) {
        this.zoneMonitor = zoneMonitor;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public Double getTemprature() {
        return temprature;
    }

    public void setTemprature(Double temprature) {
        this.temprature = temprature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RackDTO dto = (RackDTO) o;
        return Objects.equals(id, dto.id) &&
            Objects.equals(rackId, dto.rackId) &&
            Objects.equals(zoneMonitor, dto.zoneMonitor) &&
            Objects.equals(servers, dto.servers) &&
            Objects.equals(temprature, dto.temprature) &&
            Objects.equals(humidity, dto.humidity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rackId, zoneMonitor, servers, temprature, humidity);
    }

    @Override
    public int compareTo(RackDTO o) {
        return servers != null && o.getServers() != null
            ? servers.size() > o.getServers().size() ? 1 : 0 : 0;
    }
}

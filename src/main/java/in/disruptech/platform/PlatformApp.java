package in.disruptech.platform;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.disruptech.platform.config.ApplicationProperties;
import in.disruptech.platform.config.DefaultProfileUtil;

import in.disruptech.platform.domain.*;
import in.disruptech.platform.repository.*;
import io.github.jhipster.config.JHipsterConstants;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class PlatformApp implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PlatformApp.class);

    private final Environment env;

    public PlatformApp(Environment env) {
        this.env = env;
    }

    /**
     * Initializes platform.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PlatformApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }

    @Autowired
    TagRepository tagRepository;
    @Autowired
    ZoneMonitorRepository zoneMonitorRepository;
    @Autowired
    ServerRepository serverRepository;
    @Autowired
    RackRepository rackRepository;
    @Autowired
    MembershipRepository membershipRepository;
    @Autowired
    DatapointRepository datapointRepository;
    static Random random = new Random();
    @Value("${test-data}")
    private boolean generateTestData;

    @Override
    public void run(String... args) throws Exception {
        //testData();

    }


    @Bean
    public IntegrationFlow mqttInFlow() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return IntegrationFlows.from(mqttInbound())
            .handle(message -> {
                try {
                    String payload = message.getPayload().toString();
                    System.out.println(payload);
                    if (payload.contains("\"type\":2")) {
                        ZmMessage zmMessage = objectMapper.readValue(payload, ZmMessage.class);
                        Optional<ZoneMonitor> byZoneMonitorId = zoneMonitorRepository.findByZoneMonitorId(zmMessage.getId().substring(zmMessage.getId().lastIndexOf("-") + 1));
                        if (!byZoneMonitorId.isPresent()) {
                            ZoneMonitor zoneMonitor = new ZoneMonitor();
                            zoneMonitor.setZoneMonitorId(zmMessage.getId().substring(zmMessage.getId().lastIndexOf("-") + 1));
                            zoneMonitorRepository.save(zoneMonitor);
                            Rack rack = new Rack();
                            rack.setZoneMonitor(zoneMonitor);
                            rack.setRackId("RK001-" + Math.abs(random.nextInt(50)) + "-" + Math.abs(random.nextInt(50)) + "-" + Math.abs(random.nextInt(50)));
                            rackRepository.save(rack);
                        }
                    }
                    if (payload.contains("\"type\":1")) {
                        TagMessage tagMessage = objectMapper.readValue(payload, TagMessage.class);
                        Optional<Tag> byTagId = tagRepository.findByTagId(tagMessage.getId());
                        if (!byTagId.isPresent()) {
                            Tag tag = new Tag();
                            tag.setTagId(tagMessage.getId());
                            tagRepository.save(tag);
                            Server server = new Server();
                            server.setServerId("SR001-" + Math.abs(random.nextInt(50)) + "-" + Math.abs(random.nextInt(50)) + "-" + Math.abs(random.nextInt(50)));
                            server.setServerManufacturer("IBM");
                            server.setServerModel("xSeries");
                            server.setTag(tag);
                            serverRepository.save(server);
                        }
                        byTagId.ifPresent(tag -> {
                            if (lastReportedPlaced(tag) && hasBecomeOrphan(tagMessage)) {
                                endMembership(tag);
                            } else if (lastReportedOrphan(tag) && isNowPlaced(tagMessage)) {
                                createMembership(tag, tagMessage);
                            } else if (lastReportedPlaced(tag) && isNowPlaced(tagMessage) && placedInDifferentRack(tag, tagMessage)) {
                                endMembership(tag);
                                createMembership(tag, tagMessage);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            })
            .get();
    }

    private boolean placedInDifferentRack(Tag tag, TagMessage tagMessage) {
        final Rack[] lastRack = {null};
        final Rack[] currentRack = {null};
        Optional<Server> byTag = serverRepository.findByTag(tag);
        byTag.ifPresent(server -> {
            List<Membership> lastReportedLocation = membershipRepository.getLastReportedLocation(server);
            if (!lastReportedLocation.isEmpty())
                lastRack[0] = lastReportedLocation.get(0).getRack();
        });
        Optional<Rack> byZoneMonitor = rackRepository.findByZoneMonitor(zoneMonitorRepository.findByZoneMonitorId(tagMessage.getArea()).get());
        byZoneMonitor.ifPresent(rack -> {
            currentRack[0] = rack;
        });
        return lastRack[0] != null && currentRack[0] != null ? !lastRack[0].equals(currentRack[0]) : true;
    }

    private void createMembership(Tag tag, TagMessage tagMessage) {
        Optional<Server> byTag = serverRepository.findByTag(tag);
        byTag.ifPresent(server -> {
            Optional<Rack> byZoneMonitor = rackRepository.findByZoneMonitor(zoneMonitorRepository.findByZoneMonitorId(tagMessage.getArea()).get());
            byZoneMonitor.ifPresent(rack -> {
                Membership membership = new Membership();
                membership.setServer(server);
                membership.setRack(rack);
                membership.setStartTime(Instant.now());
                membershipRepository.save(membership);
            });
        });
    }

    private void endMembership(Tag tag) {
        Optional<Server> byTag = serverRepository.findByTag(tag);
        byTag.ifPresent(server -> {
            List<Membership> lastReportedLocation = membershipRepository.getLastReportedLocation(server);
            lastReportedLocation.stream()
                .forEach(membership -> {
                    membership.setEndTime(Instant.now());
                    membershipRepository.save(membership);
                });
        });
    }

    private boolean lastReportedOrphan(Tag tag) {
        Optional<Server> byTag = serverRepository.findByTag(tag);
        final boolean[] lastReportedLocationEmpty = {false};
        byTag.ifPresent(server -> {
            List<Membership> lastReportedLocation = membershipRepository.getLastReportedLocation(server);
            lastReportedLocationEmpty[0] = lastReportedLocation.isEmpty();
        });
        return lastReportedLocationEmpty[0];
    }

    private boolean lastReportedPlaced(Tag tag) {
        Optional<Server> byTag = serverRepository.findByTag(tag);
        final boolean[] lastReportedLocationEmpty = {false};
        byTag.ifPresent(server -> {
            List<Membership> lastReportedLocation = membershipRepository.getLastReportedLocation(server);
            lastReportedLocationEmpty[0] = !lastReportedLocation.isEmpty();
        });
        return lastReportedLocationEmpty[0];
    }

    private boolean isNowPlaced(TagMessage tagMessage) {
        return tagMessage.getArea().length() == 1;
    }

    private boolean hasBecomeOrphan(TagMessage tagMessage) {
        return tagMessage.getArea().length() > 1;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TagMessage {
        private String id;
        private int type;
        private String area;

        public TagMessage() {
        }

        public TagMessage(String id, int type, String area) {
            this.id = id;
            this.type = type;
            this.area = area;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ZmMessage {

        private String id;
        private int type;
        private Double humidity;
        private Double temperature;
        private int zoneStatus;

        public ZmMessage() {
        }

        public ZmMessage(String id, int type, Double humidity, Double temperature, int zoneStatus) {
            this.id = id;
            this.type = type;
            this.humidity = humidity;
            this.temperature = temperature;
            this.zoneStatus = zoneStatus;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Double getHumidity() {
            return humidity;
        }

        public void setHumidity(Double humidity) {
            this.humidity = humidity;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        public int getZoneStatus() {
            return zoneStatus;
        }

        public void setZoneStatus(int zoneStatus) {
            this.zoneStatus = zoneStatus;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    private void testData() {
        if (generateTestData) {
            List<ZoneMonitor> zoneMonitors = new ArrayList<>();
            List<Rack> racks = new ArrayList<>();
            List<Tag> tags = new ArrayList<>();
            List<Server> servers = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                ZoneMonitor zoneMonitor = new ZoneMonitor();
                zoneMonitor.setZoneMonitorId("ZM-001" + i);
                zoneMonitors.add(zoneMonitorRepository.save(zoneMonitor));
                Rack rack = new Rack();
                rack.setRackId("RK-001" + i);
                rack.setZoneMonitor(zoneMonitor);
                racks.add(rackRepository.save(rack));
                Instant time = Instant.now();
                time = time.minus(500, ChronoUnit.MINUTES);
                int mins = 1;
                while (time.isBefore(Instant.now())) {
                    Datapoint datapoint = new Datapoint();
                    datapoint.setCaptureTime(time);
                    datapoint.setRack(rack);
                    datapoint.setTag("temperature");
                    datapoint.setValue(Double.valueOf(random.nextInt(35)));
                    datapoint = new Datapoint();
                    datapoint.setCaptureTime(time);
                    datapoint.setRack(rack);
                    datapoint.setTag("humidity");
                    datapoint.setValue(Double.valueOf(random.nextInt(40)));
                    datapointRepository.save(datapoint);
                    time = time.plus(mins++, ChronoUnit.MINUTES);
                }
            }
            for (int i = 0; i < 200; i++) {
                Tag tag = new Tag();
                tag.setRemainingBattery(Math.abs(new Double(random.nextInt(100))));
                tag.setTagId("TS-001" + i);
                tags.add(tagRepository.save(tag));
                Server server = new Server();
                server.setServerId("SR-001" + i);
                server.setServerManufacturer("IBM");
                server.setServerModel("xSeries");
                server.setTag(tag);
                servers.add(serverRepository.save(server));
                for (int j = 5; j > 1; j--) {
                    Membership membership = new Membership();
                    membership.setStartTime(Instant.now().minus(j * 30, ChronoUnit.DAYS));
                    membership.setEndTime(Instant.now().minus((j - 1) * 30, ChronoUnit.DAYS));
                    membership.setServer(server);
                    membership.setRack(racks.get(Math.abs(random.nextInt()) % 20));
                    membershipRepository.save(membership);
                }
                Membership membership = new Membership();
                membership.setStartTime(Instant.now());
                membership.setServer(server);
                membership.setRack(racks.get(Math.abs(random.nextInt()) % 20));
                membershipRepository.save(membership);
            }
        }
    }

    @Bean
    public MessageProducerSupport mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("siSampleConsumer",
            mqttClientFactory(), "/dbp/sm/vacus/");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }


    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"tcp://35.196.143.24:1883"});
        factory.setConnectionOptions(options);
        return factory;
    }
}

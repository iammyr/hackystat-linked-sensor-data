package org.hackystat.linkedservicedata.server;

import static org.hackystat.linkedservicedata.server.ServerProperties.CONTEXT_ROOT_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.HOSTNAME_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.LOGGING_LEVEL_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.PORT_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.SENSORBASE_FULLHOST_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.TELEMETRY_FULLHOST_KEY;
import java.util.Map;
import java.util.logging.Logger;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.linkedservicedata.frontsidecache.FrontSideCache;
import org.hackystat.linkedservicedata.resource.cache.CacheOtherResource;
import org.hackystat.linkedservicedata.resource.cache.CacheResource;
import org.hackystat.linkedservicedata.resource.command.CommandResource;
import org.hackystat.linkedservicedata.resource.dataset.VocabResource;
import org.hackystat.linkedservicedata.resource.dataset.VocabVoIDResource;
import org.hackystat.linkedservicedata.resource.issue.IssueResource;
import org.hackystat.linkedservicedata.resource.issue.IssuesResource;
import org.hackystat.linkedservicedata.resource.machine.MachineResource;
import org.hackystat.linkedservicedata.resource.network.LiSeDNetworkResource;
import org.hackystat.linkedservicedata.resource.network.NetworkResource;
import org.hackystat.linkedservicedata.resource.operatingSystem.OperatingSystemResource;
import org.hackystat.linkedservicedata.resource.ping.PingResource;
import org.hackystat.linkedservicedata.resource.programmingLanguage.LanguageResource;
import org.hackystat.linkedservicedata.resource.project.ProjectsResource;
import org.hackystat.linkedservicedata.resource.project.UserProjectResource;
import org.hackystat.linkedservicedata.resource.project.UserProjectsResource;
import org.hackystat.linkedservicedata.resource.repository.RepositoryResource;
import org.hackystat.linkedservicedata.resource.swdevelopmentphase.SWDevelopmentPhaseResource;
import org.hackystat.linkedservicedata.resource.tool.ToolResource;
import org.hackystat.linkedservicedata.resource.user.UserResource;
import org.hackystat.linkedservicedata.resource.user.UsersResource;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.utilities.logger.HackystatLogger;
import org.hackystat.utilities.logger.RestletLoggerUtil;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Guard;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.Protocol;

/**
 * Sets up the HTTP Server process and dispatching to the associated resources.
 *
 * @author Philip Johnson, Myriam Leggieri
 */
public class Server extends Application {

  /** Holds the Restlet Component associated with this Server. */
  private Component component;

  /** Holds the host name associated with this Server. */
  private String hostName;

  /** Holds the HackystatLogger for this Service. */
  private Logger logger;

  /** Holds the ServerProperties instance for this Service. */
  private ServerProperties properties;

  private FrontSideCache frontSideCache;

  /**
   * Creates a new instance of a DailyProjectData HTTP server, listening on the supplied port.
   *
   * @return The Server instance created.
   * @throws Exception If problems occur starting up this server.
   */
  public static Server newInstance() throws Exception {
    return newInstance(new ServerProperties());
  }

  /**
   * Creates a new instance of a DailyProjectData HTTP server suitable for unit testing. DPD
   * properties are initialized from the User's dailyprojectdata.properties file, then set to their
   * "testing" versions.
   *
   * @return The Server instance created.
   * @throws Exception If problems occur starting up this server.
   */
  public static Server newTestInstance() throws Exception {
    ServerProperties properties = new ServerProperties();
    properties.setTestProperties();
    return newInstance(properties);
  }

  /**
   * Creates a new instance of a LiSeD HTTP server, listening on the supplied port.
   *
   * @param properties The ServerProperties instance used to initialize this server.
   * @return The Server instance created.
   * @throws Exception If problems occur starting up this server.
   */
  public static Server newInstance(ServerProperties properties) throws Exception {
    Server server = new Server();
    server.logger = HackystatLogger.getLogger("org.hackystat.linkedservicedata.server",
        "linkedservicedata");
    server.properties = properties;
    server.hostName = "http://" + server.properties.get(HOSTNAME_KEY) + ":"
        + server.properties.get(PORT_KEY) + "/" + server.properties.get(CONTEXT_ROOT_KEY) + "/";
    int port = Integer.valueOf(server.properties.get(PORT_KEY));
    server.component = new Component();
    server.component.getServers().add(Protocol.HTTP, port);
    server.component.getDefaultHost().attach("/" + server.properties.get(CONTEXT_ROOT_KEY), server);
    server.frontSideCache = new FrontSideCache(server);

    // Load the list of servers included in the Hackystat network.
    LiSeDNetworkResource.loadList();

    // Create and store the JAXBContext instances on the server context.
    // They are supposed to be thread safe.
    Map<String, Object> attributes = server.getContext().getAttributes();

    // Provide a pointer to this server in the Context so that Resources can get at this server.
    attributes.put("LinkedServiceDataServer", server);

    // Move Restlet Logging into a file.
    RestletLoggerUtil.useFileHandler("linkedservicedata");

    // Now let's open for business.
    server.logger.warning("Host: " + server.hostName);
    HackystatLogger.setLoggingLevel(server.logger, server.properties.get(LOGGING_LEVEL_KEY));
    server.logger.info(server.properties.echoProperties());
    String sensorBaseHost = server.properties.get(SENSORBASE_FULLHOST_KEY);

    String dailyProjectDataHost = server.properties.get(DAILYPROJECTDATA_FULLHOST_KEY);
    String telemetryHost = server.properties.get(TELEMETRY_FULLHOST_KEY);
    boolean sensorBaseOK = SensorBaseClient.isHost(sensorBaseHost);
    boolean dailyProjectDataOK = DailyProjectDataClient.isHost(dailyProjectDataHost);
    boolean telemetryOK = TelemetryClient.isHost(telemetryHost);
    server.logger.warning("Service SensorBase "
        + sensorBaseHost
        + ((sensorBaseOK) ? " was contacted successfully."
            : " NOT AVAILABLE. Therefore, the LinkedServiceData service will not run correctly."));
    server.logger.warning("Service DailyProjectData "
        + dailyProjectDataHost
        + ((dailyProjectDataOK) ? " was contacted successfully."
            : " NOT AVAILABLE. Therefore, the LinkedServiceData service will not run correctly."));
    server.logger.warning("Service Telemetry "
        + telemetryHost
        + ((telemetryOK) ? " was contacted successfully."
            : " NOT AVAILABLE. Therefore, the LinkedServiceData service will not run correctly."));
    server.logger.warning("LinkedServiceData (Version " + getVersion() + ") now running.");
    server.component.start();

    return server;
  }

  /**
   * Starts up the web service. Control-c to exit.
   *
   * @param args Ignored.
   * @throws Exception if problems occur.
   */
  public static void main(final String[] args) throws Exception {
    Server.newInstance();
  }

  /**
   * Dispatch to the specific LinkedServiceData resource based upon the URI. We will authenticate
   * all requests.
   *
   * @return The router Restlet.
   */
  @Override
  public Restlet createRoot() {
    // First, create a Router that will have a Guard placed in front of it so that this Router's
    // requests will require authentication.
    Router authRouter = new Router(getContext());

    // SPARQL
    authRouter.attach("/users/sparql", UsersResource.class);
    authRouter.attach("/projects/sparql", ProjectsResource.class);
    authRouter.attach("/issues/sparql", IssuesResource.class);

    // USERS
    authRouter.attach("/users", UsersResource.class);
    authRouter.attach("/users/{user}", UserResource.class);

    // ISSUE
    authRouter.attach("/issues/{issueId}", IssueResource.class);
    authRouter.attach("/issues", IssuesResource.class);

    // PROJECTS
    authRouter.attach("/projects", ProjectsResource.class);
    authRouter.attach("/projects/{user}", UserProjectsResource.class);
    String projectUri = "/projects/{user}/{projectname}";
    authRouter.attach(projectUri, UserProjectResource.class);

    // COMMAND
    authRouter.attach("/command/{user}/{commandName}", CommandResource.class);

    // SOFTWARE DEVELOPMENT PHASE OF A PROJECT
    authRouter.attach("/devPhase/{user}/{projectname}/{phaseId}", SWDevelopmentPhaseResource.class);

    // REPOSITORY
    authRouter.attach("/repository/{location}/", RepositoryResource.class);

    // CACHE
    authRouter.attach("/cache/others", CacheOtherResource.class);
    authRouter.attach("/cache/{user}/{project}", CacheResource.class);
    authRouter.attach("/cache", CacheResource.class);
    authRouter.attach("/cache/{user}", CacheResource.class);

    // NETWORK
    authRouter.attach("/network", NetworkResource.class);
    authRouter.attach("/network/{uri}", NetworkResource.class);

    // Here's the Guard that we will place in front of authRouter.
    Guard guard = new Authenticator(getContext(), this.getServerProperties().get(
        SENSORBASE_FULLHOST_KEY), this.getServerProperties().get(DAILYPROJECTDATA_FULLHOST_KEY),
        this.getServerProperties().get(TELEMETRY_FULLHOST_KEY));
    guard.setNext(authRouter);

    // Now create our "top-level" router which will allow the Ping URI to proceed without
    // authentication, but all other URI patterns will go to the guarded Router.
    Router router = new Router(getContext());
    router.attach("/ping", PingResource.class);
    router.attach("/ping?user={user}&password={password}", PingResource.class);
    // HACKYSTAT VOCABULARY
    router.attach("/vocab", VocabResource.class);
    // DATASET DESCRIPTION
    router.attach("/vocab/void", VocabVoIDResource.class);
    router.attachDefault(guard);
    // PROGRAMMING LANGUAGE
    router.attach("/programmingLanguage/{languageName}", LanguageResource.class);
    // OPERATING SYSTEM
    router.attach("/operatingSystem/{osName}", OperatingSystemResource.class);
    // MACHINE
    router.attach("/machine/{machineName}", MachineResource.class);
    // TOOL
    router.attach("/tool/{toolName}", ToolResource.class);

    return router;
  }

  /**
   * Returns the version associated with this Package, if available from the jar file manifest. If
   * not being run from a jar file, then returns "Development".
   *
   * @return The version.
   */
  public static String getVersion() {
    String version = Package.getPackage("org.hackystat.linkedservicedata.server")
        .getImplementationVersion();
    return (version == null) ? "Development" : version;
  }

  /**
   * Returns the host name associated with this server. Example:
   * "http://localhost:9877/linkedservicedata"
   *
   * @return The host name.
   */
  public String getHostName() {
    return this.hostName;
  }

  /**
   * Returns the ServerProperties instance associated with this server.
   *
   * @return The server properties.
   */
  public ServerProperties getServerProperties() {
    return this.properties;
  }

  /**
   * Returns the logger for this service.
   *
   * @return The logger.
   */
  @Override
  public Logger getLogger() {
    return this.logger;
  }

  /**
   * Returns the front side cache for this server.
   *
   * @return The FrontSideCache.
   */
  public FrontSideCache getFrontSideCache() {
    return this.frontSideCache;
  }
}

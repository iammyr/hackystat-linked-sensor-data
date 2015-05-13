package org.hackystat.linkedservicedata.server;

import java.util.HashMap;
import java.util.Map;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.restlet.Context;
import org.restlet.Guard;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Request;

/**
 * Performs authentication of each HTTP request using HTTP Basic authentication. Checks
 * user/password credentials by pinging SensorBase, then caching authentic user/password
 * combinations. If a cached user/password combo does not match the current user/password combo,
 * then the SensorBase is pinged again (because maybe the user has changed their password recently).
 * <p>
 * Because LiSeD resources will always want to communicate with the underlying SensorBase, DPD and
 * Telemetry, this Authenticator also creates a map of
 * <ul>
 * <li>user-to-SensorBaseClient instances</li>
 * <li>user-to-DailyProjectDataClient instances</li>
 * <li>user-to-TelemetryClient instances</li>
 * </ul>
 * which can be retrieved from the server context. This keeps the user password info in this class,
 * while making the Client instances available to the service.
 *
 * @author Philip Johnson, Myriam Leggieri
 */
public class Authenticator extends Guard {

  /** A singleton map containing previously verified credentials. */
  private static Map<String, String> credentials = new HashMap<String, String>();

  /** A singleton map containing DailyProjectDataClient instances, one per credentialled user. */
  private static Map<String, DailyProjectDataClient> dpdClientMap = new HashMap<String, DailyProjectDataClient>();

  /** A singleton map containing SensorBaseClient instances, one per credentialled user. */
  private static Map<String, SensorBaseClient> sensorBaseClientMap = new HashMap<String, SensorBaseClient>();

  /** A singleton map containing TelemtryClient instances, one per credentialled user. */
  private static Map<String, TelemetryClient> telemetryClientMap = new HashMap<String, TelemetryClient>();

  /** The sensorbase host, such as "http://localhost:9876/sensorbase/". */
  private String sensorBaseHost;

  /** The DailyProjectData host, such as "http://localhost:9877/dailyprojectdata/". */
  private String dpdHost;

  /** The Telemetry host, such as "http://localhost:9878/telemetry/". */
  private String telemetryHost;

  /** The key to be used to retrieve the DailyProjectDataClient map from the server context. */
  public static final String AUTHENTICATOR_TELEMETRY_CLIENTS_KEY = "authenticator.telemetry.clients";

  /** The key to be used to retrieve the DailyProjectDataClient map from the server context. */
  public static final String AUTHENTICATOR_DPD_CLIENTS_KEY = "authenticator.dpd.clients";

  /** The key to be used to retrieve the SensorbaseClient map from the server context. */
  public static final String AUTHENTICATOR_SENSORBASE_CLIENTS_KEY = "authenticator.sensorbase.clients";

  /**
   * Initializes this Guard to do HTTP Basic authentication. Puts the credentials map in the server
   * context so that Resources can get the password associated with the uriUser for their own
   * invocations to the SensorBase.
   *
   * @param context The server context.
   * @param sensorBaseHost The sensorbase service, such as 'http://localhost:9876/sensorbase/'.
   * @param dpdHost The DPD service, such as 'http://localhost:9877/dailyprojectdata/'.
   * @param telemetryHost The Telemetry service, such as 'http://localhost:9878/telemetry/'.
   */
  public Authenticator(Context context, String sensorBaseHost, String dpdHost, String telemetryHost) {
    super(context, ChallengeScheme.HTTP_BASIC, "LinkedServiceData");
    this.sensorBaseHost = sensorBaseHost;
    this.dpdHost = dpdHost;
    this.telemetryHost = telemetryHost;
    context.getAttributes().put(AUTHENTICATOR_TELEMETRY_CLIENTS_KEY, telemetryClientMap);
    context.getAttributes().put(AUTHENTICATOR_DPD_CLIENTS_KEY, dpdClientMap);
    context.getAttributes().put(AUTHENTICATOR_SENSORBASE_CLIENTS_KEY, sensorBaseClientMap);
  }

  /**
   * Returns true if the passed credentials are OK.
   *
   * @param identifier The account name.
   * @param secretCharArray The password.
   * @return If the credentials are valid.
   */
  @Override
  public boolean checkSecret(Request request, String identifier, char[] secretCharArray) {
    /*
     * I am synchronizing here on a static (class-wide) variable for two reasons: (1) JCS
     * write-through caching fails when multiple threads access the same back-end file:
     * <https://issues.apache.org/jira/browse/JCS-31>. Thus, it is vitally important to ensure that
     * only one instance of a SensorBaseClient for any given user is created. (2) I do not know if
     * Restlet allows multiple Authenticator instances. Thus, I am synchronizing on a class-wide
     * variable just in case. This synchronization creates a bottleneck on every request, but the
     * benefits of reliable caching should outweigh this potential performance hit under high loads.
     */
    synchronized (sensorBaseClientMap) {
      synchronized (dpdClientMap) {
        synchronized (telemetryClientMap) {

          String secret = new String(secretCharArray);
          // Return true if the user/password credentials are in the cache.
          if (credentials.containsKey(identifier) && secret.equals(credentials.get(identifier))) {
            return true;
          }
          // Otherwise we check the credentials with the SensorBase.
          boolean isRegistered = SensorBaseClient.isRegistered(sensorBaseHost, identifier, secret);
          if (isRegistered) {
            // Credentials are good, so save them and create clients for this user.
            credentials.put(identifier, secret);
            // Only create a new client if there is no old one.
            if (!dpdClientMap.containsKey(identifier)) {
              org.hackystat.linkedservicedata.server.Server server = (org.hackystat.linkedservicedata.server.Server) getContext()
                  .getAttributes().get("LinkedServiceDataServer");
              org.hackystat.linkedservicedata.server.ServerProperties props = server
                  .getServerProperties();
              DailyProjectDataClient client = new DailyProjectDataClient(dpdHost, identifier,
                  secret);
              if (props.isCacheEnabled()) { // NOPMD
                client.enableCaching(identifier, "linkedservicedata", props.getCacheMaxLife(),
                    props.getCacheCapacity());
              }
              dpdClientMap.put(identifier, client);
            }
            if (!telemetryClientMap.containsKey(identifier)) {
              TelemetryClient client = new TelemetryClient(telemetryHost, identifier, secret);
              telemetryClientMap.put(identifier, client);
            }
            if (!sensorBaseClientMap.containsKey(identifier)) {
              SensorBaseClient client = new SensorBaseClient(sensorBaseHost, identifier, secret);
              sensorBaseClientMap.put(identifier, client);
            }
          }
          return isRegistered;
        }
      }
    }
  }
}

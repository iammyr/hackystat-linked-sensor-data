package org.hackystat.linkedservicedata.test;

import static org.hackystat.linkedservicedata.server.ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.TELEMETRY_FULLHOST_KEY;
import org.hackystat.linkedservicedata.server.Server;
import org.hackystat.linkedservicedata.server.ServerProperties;
import org.junit.BeforeClass;

/**
 * Provides a helper class to facilitate JUnit testing.
 *
 * @author Philip Johnson, Myriam Leggieri
 */
public class LinkedServiceDataTestHelper {
  /** The admin email. */
  protected static final String admin = "myrpandemon@yahoo.it";
  /** The admin password. */
  protected static final String admin_password = "polpetta";

//  /** The Sensorbase server used in these tests. */
//  private static org.hackystat.sensorbase.server.Server sensorbaseServer;
//  /** The DailyProjectData server used in these tests. */
//  private static org.hackystat.dailyprojectdata.server.Server dpdServer;
//  /** The Telemetry server used in these tests. */
//  private static org.hackystat.telemetry.service.server.Server telemetryServer;
  /** The LinkedServiceData server used in these tests. */
  private static org.hackystat.linkedservicedata.server.Server lisedServer;


  /**
   * Constructor.
   */
  public LinkedServiceDataTestHelper() {
    // Does nothing.
  }

  /**
   * Starts the server going for these tests.
   * @throws Exception If problems occur setting up the server.
   */
  @BeforeClass public static void setupServer() throws Exception {
    // Create a testing version of the Sensorbase.
//    LinkedServiceDataTestHelper.sensorbaseServer =
//      org.hackystat.sensorbase.server.Server.newInstance();
//    LinkedServiceDataTestHelper.dpdServer =
//    org.hackystat.dailyprojectdata.server.Server.newInstance();
//    LinkedServiceDataTestHelper.telemetryServer =
//    org.hackystat.telemetry.service.server.Server.newInstance();
    LinkedServiceDataTestHelper.lisedServer = Server.newInstance();
  }

  /**
   * Returns the hostname associated with this LiSeD test server.
   *
   * @return The host name, including the context root.
   */
  protected String getLinkedServiceDataHostName() {
    return LinkedServiceDataTestHelper.lisedServer.getHostName();
  }

  /**
   * Returns the sensorbase hostname that this LiSeD server communicates with.
   *
   * @return The host name, including the context root.
   */
  protected String getSensorBaseHostName() {
    return System
    .getProperty(
        ServerProperties.SENSORBASE_FULLHOST_KEY);
  }

  /**
   * Returns the DPD hostname that this Telemetry server communicates with.
   *
   * @return The host name, including the context root.
   */
  protected String getDailyProjectDataHostName() {
    return System
    .getProperty(
        DAILYPROJECTDATA_FULLHOST_KEY);
  }

  /**
   * Returns the DPD hostname that this Telemetry server communicates with.
   *
   * @return The host name, including the context root.
   */
  protected String getTelemetryHostName() {
    return System
    .getProperty(TELEMETRY_FULLHOST_KEY);
  }

  /**
   * Returns the LiSeD server instance.
   *
   * @return The LiSeD server instance.
   */
  protected Server getLiSeDServer() {
    return LinkedServiceDataTestHelper.lisedServer;
  }

}

package org.hackystat.linkedservicedata.resource.ping;

import static org.junit.Assert.assertTrue;
import org.hackystat.linkedservicedata.client.LinkedServiceDataClient;
import org.hackystat.linkedservicedata.test.LinkedServiceDataTestHelper;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.junit.Test;

/**
 * Test for the Ping REST Api.
 *
 * @author Myriam Leggieri.
 *
 */
public class TestPingRestApi extends LinkedServiceDataTestHelper {
  /**
   * Test that GET {host}/ping returns the service name.
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testPing() throws Exception {
    // First, just call isHost, which uses the standard ping.
    String lsdHost = getLinkedServiceDataHostName();
    assertTrue("Checking ping", LinkedServiceDataClient.isHost(lsdHost));
    // Next, check authenticated ping.
    String user = "TestTelPing@hackystat.org";
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);
    LinkedServiceDataClient client = new LinkedServiceDataClient(user, user, null, null, true, true);
    client.authenticate();
  }
}

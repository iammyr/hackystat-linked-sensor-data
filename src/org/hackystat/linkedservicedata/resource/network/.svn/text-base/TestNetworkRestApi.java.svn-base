package org.hackystat.linkedservicedata.resource.network;

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import org.hackystat.linkedservicedata.client.LinkedServiceDataClient;
import org.hackystat.linkedservicedata.client.LinkedServiceDataClientException;
import org.hackystat.linkedservicedata.test.LinkedServiceDataTestHelper;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;

/**
 * Test for the Networj REST Api.
 *
 * @author Myriam Leggieri.
 *
 */
public class TestNetworkRestApi extends LinkedServiceDataTestHelper {

  /** The user for this test case. */


  private LinkedServiceDataClient lisedClient = null;
  private SensorBaseClient sbClient = null;

  /** Change the following attributes to test different RDF serialization languages **/
  private String lang = HackystatConstants.LANG_NTRIPLE;
  private MediaType media =
  // MediaType.APPLICATION_RDF_XML;
  new MediaType(HackystatConstants.MEDIA_TYPE_NTRIPLES);

  private String uri = "http://185.164.301:9875/linkedservicedata/";
  private String admin1 = "hola1@yahoo.com";
  private String password1 = "hola1@yahoo.com";
  private String uri2 = "http://185.164.302:9875/linkedservicedata/";
  private String admin2 = "hola2@yahoo.com";
  private String password2 = "hola2@yahoo.com";
  private String uri3 = "http://185.164.303:9875/linkedservicedata/";
  private String admin3 = "hola3@yahoo.com";
  private String password3 = "hola3@yahoo.com";

  /**
   * Create data and send to server if we haven't done it already.
   *
   * @throws Exception If problems occur.
   */
  @Before
  public void setUp() throws Exception {
    lisedClient = new LinkedServiceDataClient(admin, admin_password, lang, media, true, true);
    lisedClient.authenticate();
    lisedClient.clearServerCache();
    lisedClient.clearLocalCache();

    sbClient = new SensorBaseClient(getSensorBaseHostName(), admin, admin_password);
    sbClient.authenticate();
    sbClient.clearCache();
  }

  /**
   * Test that GET {host}/projects/{owner}/{project} works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testAddGetRemoveAssociatedServers() {
    HashMap<String, String[]> map = new HashMap<String, String[]>();
    map.put(uri, new String[] { admin1, password1 });
    map.put(uri2, new String[] { admin2, password2 });
    map.put(uri3, new String[] { admin3, password3 });
    try {
      lisedClient.addNetworkLiSeDServer(map);
      for (String uri : map.keySet()) {
        System.out.println("key=" + uri + "\tvalue=" + map.get(uri)[0] + "," + map.get(uri)[1]);
      }
      map = null;
      map = lisedClient.getAssociatedLiSeDServers();
      for (String uri : map.keySet()) {
        System.out.println("key=" + uri + "\tvalue=" + map.get(uri)[0] + "," + map.get(uri)[1]);
      }
      assertTrue(map.containsKey(uri));
      assertTrue(map.get(uri)[0].equals(admin1));
      assertTrue(map.containsKey(uri2));
      assertTrue(map.get(uri2)[0].equals(admin2));
      assertTrue(map.containsKey(uri3));
      assertTrue(map.get(uri3)[0].equals(admin3));

      lisedClient.removeAssociatedLiSeDServers(uri);
      map = null;
      map = lisedClient.getAssociatedLiSeDServers();
      assertTrue(!map.containsKey(uri));
      lisedClient.removeAssociatedLiSeDServers(uri2);
      map = null;
      map = lisedClient.getAssociatedLiSeDServers();
      assertTrue(!map.containsKey(uri2));
      lisedClient.removeAssociatedLiSeDServers(uri3);
      map = null;
      map = lisedClient.getAssociatedLiSeDServers();
      assertTrue(!map.containsKey(uri3));
    }
    catch (LinkedServiceDataClientException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      assertTrue(false);
    }

  }

}

package org.hackystat.linkedservicedata.externalDatasets;

import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Client;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * Manage the communication with the PingTheSemanticWeb (PTSW) Api. This will be useful and full
 * working only when at least part of the currently provided data will be available also without the
 * needing to log in with the SensorBase. In fact PTSW accepts pings only if it's able to crawl RDF
 * data from a given URI and this is still not the case.
 *
 * @author Myriam Leggieri.
 *
 */
public class PingTheSemanticWebApiManager {

  /**
   * Request to ping a specified URI to PingTheSemanticWeb.
   *
   * @param query
   * @param appendice
   * @return
   */
  public static String pingPTSWRequest(String query) throws Exception {
    String ret = null;
    try {
      query = java.net.URLEncoder.encode(query, "UTF-8");
      String urlStr = HackystatConstants.PTSW_BASE_URI + query;
      Reference reference = new Reference(urlStr);
      Request request = null;
      request = new Request(Method.GET, reference);
      System.out.println("LinkedServiceData Tracing: " + Method.POST + " " + reference);
      Response response = new Client(Protocol.HTTP).handle(request);
      if (response != null) {
        Status status = response.getStatus();
        System.out.println("  => " + status.getCode() + " " + status.getDescription());
        if (status.getCode() != 200) {
          System.err.println("Request failed.");
          throw new Exception("Ping The Semantic Web request has failed.");
        }
      }
    }
    catch (Exception e) {
      System.err.println("Unable to perform a ping on Ping the Semantic Web");
      e.printStackTrace();
    }
    return ret;
  }

}

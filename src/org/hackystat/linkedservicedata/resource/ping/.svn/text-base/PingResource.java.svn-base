package org.hackystat.linkedservicedata.resource.ping;

import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * The PingResource responds to a GET {host}/ping with the string "LinkedServiceData". It responds
 * to GET {host}/ping?user={user}&password={password} with "LinkedServiceData authenticated" if the
 * user and password are valid, and "LinkedServiceData" if not valid.
 *
 * @author Philip Johnson, Myriam Leggieri
 */
public class PingResource extends LinkedServiceDataResource {
  /** From the URI, if authentication is desired. */
  private String user;
  /** From the URI, if authentication is desired. */
  private String password;

  /**
   * The standard constructor.
   *
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public PingResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.user = (String) request.getAttributes().get("user");
    this.password = (String) request.getAttributes().get("password");
  }

  /**
   * Returns the string "LinkedServiceData" or "LinkedServiceData authenticated", depending upon
   * whether credentials are passed as form parameters and whether they are valid.
   *
   * @param variant The representational variant requested.
   * @return The representation as a string.
   */
  @Override
  public Representation represent(Variant variant) {
    try {
      String unauthenticated = "LinkedServiceData";
      String authenticated = "LinkedServiceData authenticated";
      // Don't try to authenticate unless the user has passed both a user and password.
      if ((user == null) || (password == null)) {
        return new StringRepresentation(unauthenticated);
      }
      // There is a user and password. So, check the SensorBase to see if they're OK.
      String sensorBaseHost = lsdServer.getServerProperties().get(
          org.hackystat.linkedservicedata.server.ServerProperties.SENSORBASE_FULLHOST_KEY);
      boolean OK = SensorBaseClient.isRegistered(sensorBaseHost, user, password);
      return new StringRepresentation((OK ? authenticated : unauthenticated));
    }
    catch (Exception e) {
      setStatusError("Error during ping\n" + e.getMessage(), e);
      return null;
    }
  }

}

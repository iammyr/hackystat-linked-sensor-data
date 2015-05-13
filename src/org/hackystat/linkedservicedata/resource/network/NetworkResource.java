package org.hackystat.linkedservicedata.resource.network;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * Resource representing the Hackystat network associated with this server.
 *
 * @author Myriam Leggieri.
 *
 */
public class NetworkResource extends LiSeDNetworkResource {

  public NetworkResource(Context context, Request request, Response response) {
    super(context, request, response);
    // TODO Auto-generated constructor stub
  }

  /**
   * Returns a RDF Model serialized as N3 containing the User linked data associated with all the
   * registered users, or a string stating "Permission denied" if not authorized.
   *
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    Logger logger = this.lsdServer.getLogger();
    logger.fine("Gathering Network information: Start.");
    Representation ret = null;
    try {
      ret = LinkedServiceDataResource.getStringRepresentation(
          HackystatConstants.hackystatLisedServers_list, true);
    }
    catch (UnsupportedEncodingException e) {
      setStatusError("Error creating LiSeD. "
          + "The URI has been encoded using an unsupported encoding scheme.", e);
      e.printStackTrace();
    }
    logger.fine("Gathering Network information: End.");
    return ret;
  }

  /**
   * Indicate the POST method is supported.
   *
   * @return True.
   */
  @Override
  public boolean allowPost() {
    return true;
  }

  /**
   * Implement the POST method that adds the URI of an external Hackystat LiSeD server that the
   * local admin wants to associate with this one, together with the e-mail and password of its
   * admin.
   *
   * @param entity The String representation of the serverUri-[admin, passw] pair.
   */
  @Override
  public void acceptRepresentation(Representation entity) {
    if (!super.validateUserIsAdmin()) {
      getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
      return;
    }
    // Try to make the String payload into a key-value pair
    try {
      if (super.entityFound != null) {
        LinkedServiceDataResource.addServersList(super.entityFound);
      }
      else {
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
      }
    }
    catch (Exception e) {
      setStatusError("Storing network data failed.", e);
      e.printStackTrace();
      return;
    }
    getResponse().setStatus(Status.SUCCESS_CREATED);
  }

  /**
   * Indicate the DELETE method is supported.
   *
   * @return True.
   */
  @Override
  public boolean allowDelete() {
    return true;
  }

  /**
   * Implement the DELETE method that deletes an existing serverUri-[admin, passw] pair. If the pair
   * doesn't exist, that's fine, it's still "deleted".
   */
  @Override
  public void removeRepresentations() {
    if (!super.validateUserIsAdmin()) {
      getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
      return;
    }
    try {
      if (super.uri != null) {
        LiSeDNetworkResource.removeExternalHackystatLiSeDServer(super.uri);
      }
      else {
        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
      }
    }
    catch (Exception e) {
      setStatusError("Deleting network data failed.", e);
      e.printStackTrace();
      return;
    }
    getResponse().setStatus(Status.SUCCESS_OK);
  }
}

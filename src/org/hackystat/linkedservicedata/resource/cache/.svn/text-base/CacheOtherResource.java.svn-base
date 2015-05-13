package org.hackystat.linkedservicedata.resource.cache;

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
 * This resource responds to requests of form:
 *
 * <pre>
 * DELETE {host}/cache/others
 * </pre>
 *
 * This one clears all entries associated with the authorized user; in other words, all projects
 * that this user owns will have any cached DPDs removed.
 * <p>
 * It also responds to
 *
 * <pre>
 * DELETE {host}/cache/{user}/{project}
 * </pre>
 *
 * This one clears only those cached DPD instances for the specified project owned by that user. In
 * this case, the authorized user must be in the project specified by (project, user).
 *
 * @author Philip Johnson, Myriam Leggieri
 */
public class CacheOtherResource extends LinkedServiceDataResource {

  /**
   * The default constructor.
   *
   * @param context The context.
   * @param request The request.
   * @param response The response.
   */
  public CacheOtherResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  /**
   * Returns 200 if cache delete command succeeded. If deleting the entire cache, then the authUser
   * must be the UriUser. If deleting a project cache, then the authUser must be in the project
   * identified by UriUser and project.
   */
  @Override
  public void removeRepresentations() {
    Logger logger = this.lsdServer.getLogger();
    logger.fine(String.format("Delete cache: %s", HackystatConstants.otherResourceID));
    try {
      // Invoke the clear operation on the entire user's cache.
      super.lsdServer.getFrontSideCache().clear(HackystatConstants.otherResourceID);
      logger.info(String.format("All LiSeD cache entries deleted for resources %s ",
          HackystatConstants.otherResourceID));
      getResponse().setStatus(Status.SUCCESS_OK);
      return;
    }
    catch (Exception e) {
      setStatusError("Error during cache deletion", e);
      return;
    }
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
   * Indicate that GET is not supported.
   *
   * @return False.
   */
  @Override
  public boolean allowGet() {
    return false;
  }

  /**
   * Get is not supported, but the method must be implemented.
   *
   * @param variant Ignored.
   * @return Null.
   */
  @Override
  public Representation represent(Variant variant) {
    getResponse().setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    return null;
  }
}

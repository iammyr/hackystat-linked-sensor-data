package org.hackystat.linkedservicedata.resource.user;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.sensorbase.resource.users.jaxb.UserIndex;
import org.hackystat.sensorbase.resource.users.jaxb.UserRef;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Resource representing a user.
 *
 * @author Myriam Leggieri.
 *
 */
public class UserResource extends LiSeDUserResource {

  public UserResource(Context context, Request request, Response response)
      throws UnsupportedEncodingException {
    super(context, request, response);
    // there are no user-specific data to be set
  }

  /**
   * Returns a RDF Model serialized as N3 containing the User linked data associated with the
   * specified user, or a string stating "Permission denied" if not authorized.
   *
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    Logger logger = this.lsdServer.getLogger();
    logger.fine("User Data as Linked Data: Starting");
    MediaType media = variant.getMediaType();
    Representation ret = null;
    if (media.equals(MediaType.APPLICATION_ALL) || media.equals(MediaType.TEXT_ALL)
        || media.equals(MediaType.TEXT_RDF_N3) || media.equals(MediaType.APPLICATION_RDF_XML)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();

        // [2] get all the User information
        logger.fine("User LiSeD: Requesting user data: " + uriUser);

        // [3] Create the User as LinkedServiceData.
        Model rdfData = ModelFactory.createDefaultModel();
        super.initModel(rdfData);
        User u = client.getUser(uriUser);
        /**
         * BUG: when you're logged in as the admin you should be allowed to see data about any user,
         * but while if you ask for a UserIndex and then iterate over each UserRef you obtain a
         * correct user instance through sbClient.getUser(UserRef), if you indeed try to get data
         * about a particular user being an admin though sbClient.getUser(email), then you get a
         * wrong user instance which is exactly about you as admin. Then I'll make the following
         * check as a temporary patch.
         */
        if (!u.getEmail().equals(uriUser)) {
          UserIndex uind = client.getUserIndex();
          Iterator<UserRef> it = uind.getUserRef().iterator();
          UserRef uref = null;
          u = null;
          while (u == null && it.hasNext()) {
            uref = it.next();
            if (uref.getEmail().equals(uriUser)) {
              u = client.getUser(uref);
            }
          }
        }
        if (isUserProfileAccessible(u)) {
          // [2] Check the front side cache and return if the LiSeD is found and is OK to access.
          String cachedDpd = this.lsdServer.getFrontSideCache().get(uriUser, uriString + media);
          if ((cachedDpd != null)) {
            return super.getStringRepresentationFromRdf(cachedDpd, media);
          }
          logger.fine("User LiSeD: Got user: " + u.getEmail() + " instance.  Now building LiSeD.");
          this.makeUserData(rdfData, u, getSensorBaseClient(), u.getEmail(),
              new LinkedList<String>());
        }
        else {
          responseMsg += " - You don't have the right permissions to get a "
              + "representation of the required user's profile.";
          getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
          return null;
        }
        if (additionalCachedInfo) {
          // Add all the logged user accessible cached data to the returned model
          rdfData = super.addAllAccessibleCachedData(rdfData);
        }

        String str_rdfData = null;
        if (media.equals(MediaType.TEXT_ALL) || media.equals(MediaType.TEXT_RDF_N3)
            || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
          str_rdfData = super.serializeRDFModel(rdfData, null, HackystatConstants.LANG_TURTLE);
        }
        else if (media.equals(MediaType.APPLICATION_ALL)
            || media.equals(MediaType.APPLICATION_RDF_XML)) {
          str_rdfData = super.serializeRDFModel(rdfData, HackystatConstants.RESOURCE_URI_BASE,
              HackystatConstants.LANG_RDFXML);
        }
        else if (media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)) {
          str_rdfData = super.serializeRDFModel(rdfData, HackystatConstants.RESOURCE_URI_BASE,
              HackystatConstants.LANG_NTRIPLE);
        }

        this.lsdServer.getFrontSideCache().put(uriUser, uriString + media, str_rdfData);
        logRequest("User", uriUser);
        ret = super.getStringRepresentationFromRdf(str_rdfData, media);
      }
      catch (Exception e) {
        setStatusError("Error creating User LiSeD.", e);
        e.printStackTrace();
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;
  }

}

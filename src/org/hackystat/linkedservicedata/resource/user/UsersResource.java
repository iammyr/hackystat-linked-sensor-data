package org.hackystat.linkedservicedata.resource.user;

import java.util.LinkedList;
import java.util.List;
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
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Resource representing a list of user URIs.
 *
 * @author Myriam Leggieri.
 *
 */
public class UsersResource extends LiSeDUserResource {

  public UsersResource(Context context, Request request, Response response) {
    super(context, request, response);
    System.out.println(request.toString());
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
    logger.fine("User Data as Linked Data for ALL the registered users: Starting");
    MediaType media = variant.getMediaType();
    Representation ret = null;
    if (media.equals(MediaType.APPLICATION_ALL)
        || media.equals(MediaType.TEXT_ALL)
        || media.equals(MediaType.TEXT_RDF_N3)
        || media.equals(MediaType.APPLICATION_RDF_XML)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)
        || ((media.equals(MediaType.TEXT_XML) || media.getName().equals(
            HackystatConstants.MEDIA_TYPE_SPARQL_RESULTS)) && queryStr != null)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();

        // [2] Check the front side cache and return if the LiSeD is found and is OK to access.
        String cachedDpd = this.lsdServer.getFrontSideCache().get(uriUser, uriString + media);
        if ((cachedDpd != null)) {
          if (queryStr != null) {
            return super.getStringRepresentationForSparqlResults(cachedDpd);
          }
          else {
            return super.getStringRepresentationFromRdf(cachedDpd, media);
          }
        }
        // [2] get all Users information
        logger.fine("Users LiSeD: Requesting user data for ALL the registered users");
        UserIndex uind = null;
        Model rdfData = ModelFactory.createDefaultModel();
        super.initModel(rdfData);
        Bag bag = null;
        try {
          uind = client.getUserIndex();
        }
        catch (Exception e1) {
          User user = client.getUser(authUser);
          logger.fine("User LiSeD: Got user: " + user.getEmail()
              + " instance.  Now building LiSeD.");
            bag = rdfData.createBag(uriString);
            bag.add(getUserUri(user.getEmail()));
          // responseMsg += " - You don't have the right permissions to get a full"
          // + "list of all the locally registered users";
          // getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
          // e1.printStackTrace();
        }
        if (uind != null) {
          List<UserRef> l = uind.getUserRef();
          LinkedList<String> predicates = null;
          if (l != null) {
            User user = null;
            if (queryStr != null) {
              predicates = extractPredicatesFromQuery();
            }
            else {
              predicates = new LinkedList<String>();
            }
            for (UserRef ur : l) {
              user = client.getUser(ur);
              if (isUserProfileAccessible(user)) {
                if (queryStr != null) {
                  /**
                   * Create a RDF model filled with all the users available associated with all the
                   * properties required to allow the application of the Sparql 'Where' clause (if
                   * there's one, otherwise users will be associated with all their associate-able
                   * properties)
                   */
                  logger.fine("User LiSeD: Got user: " + user.getEmail()
                      + " instance.  Now building LiSeD.");
                  this.makeUserData(rdfData, user, getSensorBaseClient(), user.getEmail(),
                      predicates);
                  // LinkedServiceDataResource.printModel(rdfData);
                }
                else {
                  logger.fine("Users LiSeD: Got user: " + user.getEmail()
                      + " instance.  Now building LiSeD.");
                  if (bag == null) {
                    bag = rdfData.createBag(uriString);
                  }
                  bag.add(getUserUri(user.getEmail()));
                }
              }
            }
          }
        }
        if (additionalCachedInfo) {
          // Add all the logged user accessible cached data to the returned model
          rdfData = super.addAllAccessibleCachedData(rdfData);
        }
        String str_rdfData = null;
        if (queryStr != null) {
          Document doc = performSearchLocallyAndPropagate(rdfData,
              HackystatConstants.USER_SPARQL_RESOURCE_TYPE);
          if (doc != null) {
            str_rdfData = serializeDomDocument(doc);
          }
        }
        else {
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
        }
        this.lsdServer.getFrontSideCache().put(uriUser, uriString + media, str_rdfData);
        logRequest("Users", uriUser);

        if (queryStr != null) {
          ret = super.getStringRepresentationForSparqlResults(str_rdfData);
        }
        else {
          ret = super.getStringRepresentationFromRdf(str_rdfData, media);
        }
      }
      catch (Exception e) {
        setStatusError("Error creating Users LiSeD.", e);
        ret = null;
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;
  }

}

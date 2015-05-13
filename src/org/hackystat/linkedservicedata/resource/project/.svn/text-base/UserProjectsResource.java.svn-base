package org.hackystat.linkedservicedata.resource.project;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.ProjectIndex;
import org.hackystat.sensorbase.resource.projects.jaxb.ProjectRef;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Resource representing a list of software projects in which a given user is/has been involved.
 *
 * @author Myriam Leggieri.
 *
 */
public class UserProjectsResource extends LiSeDProjectResource {

  public UserProjectsResource(Context context, Request request, Response response)
      throws UnsupportedEncodingException {
    super(context, request, response);
    // there are no projects-specific data to be set
  }

  /**
   * Returns a RDF Model serialized as N3 containing the Project linked data associated with the
   * specified user, or a string stating "Permission denied" if not authorized.
   *
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    Logger logger = this.lsdServer.getLogger();
    logger.fine("Projects Data as Linked Data: Starting");
    Representation ret = null;
    MediaType media = variant.getMediaType();
    if (media.equals(MediaType.APPLICATION_ALL) || media.equals(MediaType.TEXT_ALL)
        || media.equals(MediaType.TEXT_RDF_N3) || media.equals(MediaType.APPLICATION_RDF_XML)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();
        // Check if the logged user as the rights to get a response
        if (!validateUserIsAdmin() && !validateUserIsUriUser()) {
          responseMsg += " - You don't have the right permissions to get a "
              + "full list of all the projects in which " + uriUser + " is involved.";
          getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
          return null;
        }
        // Check the front side cache and return if the LiSeD is found and is OK to access.
        String cachedDpd = this.lsdServer.getFrontSideCache().get(uriUser, uriString + media);
        if ((cachedDpd != null)) {
          return super.getStringRepresentationFromRdf(cachedDpd, media);
        }
        // [2] get all the Project information
        logger.fine("Projects LiSeD: Requesting projects dat for the user: " + uriUser);
        ProjectIndex pind = client.getProjectIndex(uriUser);
        Project proj = null;
        if (pind != null) {
          List<ProjectRef> l = pind.getProjectRef();
          if (l != null) {
            Model rdfData = ModelFactory.createDefaultModel();
            super.initModel(rdfData);
            Bag bag = rdfData.createBag(uriString);
            for (ProjectRef pref : l) {
              proj = client.getProject(pref);
              if (isProjectProfileAccessible(proj)) {
                logger.fine("Projects LiSeD: Got project: " + pref.getName() + " instance.  "
                    + "Now building LiSeD.");
                // [3]Unify the Project LinkedServiceData with the other user's project data
                bag.add(getProjectUri(proj.getOwner(), proj.getName()));
              }
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
            logRequest("Projects for ", uriUser);
            ret = super.getStringRepresentationFromRdf(str_rdfData, media);
          }
        }
      }
      catch (Exception e) {
        setStatusError("Error creating Projects LiSeD.", e);
        e.printStackTrace();
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;
  }
}

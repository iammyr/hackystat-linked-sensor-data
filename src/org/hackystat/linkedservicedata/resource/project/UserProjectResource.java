package org.hackystat.linkedservicedata.resource.project;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
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
 * Resource representing a software project.
 *
 * @author Myriam Leggieri.
 *
 */
public class UserProjectResource extends LiSeDProjectResource {

  public UserProjectResource(Context context, Request request, Response response)
      throws UnsupportedEncodingException {
    super(context, request, response);
    // there are no project-specific data to be set
  }

  /**
   * Returns a RDF Model serialized as N3 containing the Project linked data associated with the
   * specified user and project, or a string stating "Permission denied" if not authorized.
   *
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    Logger logger = this.lsdServer.getLogger();
    logger.fine("Project Data as Linked Data: Starting");
    MediaType media = variant.getMediaType();
    Representation ret = null;
    if (media.equals(MediaType.APPLICATION_ALL) || media.equals(MediaType.TEXT_ALL)
        || media.equals(MediaType.TEXT_RDF_N3) || media.equals(MediaType.APPLICATION_RDF_XML)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
      try {
        // [1] get the SensorBaseClient for the user making this request.
        SensorBaseClient client = super.getSensorBaseClient();

        // [2] get all the Project information
        logger.fine("Project LiSeD: Requesting project data: " + projectName + " for the user: "
            + uriUser);

        // [3] Create the Project as LinkedServiceData.
        Model rdfData = ModelFactory.createDefaultModel();
        super.initModel(rdfData);
        Project p = client.getProject(uriUser, projectName);
        if (isProjectProfileAccessible(p)) {
          // Check the front side cache and return if the LiSeD is found and
          // is OK to access.
          String cachedDpd = this.lsdServer.getFrontSideCache().get(uriUser, uriString + media);
          if ((cachedDpd != null)) {
            return super.getStringRepresentationFromRdf(cachedDpd, media);
          }
          logger.fine("Project LiSeD: Got project: " + projectName + " instance.  "
              + "Now building LiSeD.");
          this.makeProjectData(rdfData, p, new LinkedList<String>());
        }
        else {
          responseMsg += " - You don't have the right permissions to get a"
              + " representation of the required project's profile.";
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

        logRequest("Project", projectName);
        ret = super.getStringRepresentationFromRdf(str_rdfData, media);
      }
      catch (Exception e) {
        setStatusError("Error creating Project LiSeD.", e);
        e.printStackTrace();
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;
  }

}

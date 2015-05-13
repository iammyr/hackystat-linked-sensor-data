package org.hackystat.linkedservicedata.resource.repository;

import java.util.Iterator;
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
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Resource represnting a repository.
 *
 * @author Myriam Leggieri.
 *
 */
public class RepositoryResource extends LiSeDRepositoryResource {

  public RepositoryResource(Context context, Request request, Response response) {
    super(context, request, response);
    // TODO Auto-generated constructor stub
  }

  /**
   * Returns a RDF Model serialized as N3 containing the Repository Branch linked data associated
   * with the specified user, or a string stating "Permission denied" if not authorized.
   *
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    Logger logger = this.lsdServer.getLogger();
    String resourceName = "Repository";
    MediaType media = variant.getMediaType();
    Representation ret = null;
    logger.fine(resourceName + " as Linked Data: Starting");
    if (media.equals(MediaType.APPLICATION_ALL) || media.equals(MediaType.TEXT_ALL)
        || media.equals(MediaType.TEXT_RDF_N3) || media.equals(MediaType.APPLICATION_RDF_XML)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
      try {
        // The logged user can view only those branch data collected by his own SD
        SensorBaseClient client = getSensorBaseClient();
        if (client == null) {
          return null;
        }
        // [2] Check the front side cache and return if the LiSeD is found and is OK to access.
        String cachedDpd = this.lsdServer.getFrontSideCache().get(
            HackystatConstants.otherResourceID, uriString + "/" + media);
        if ((cachedDpd != null)) {
          return super.getStringRepresentationFromRdf(cachedDpd, media);
        }
        // [2] get all the Repository information
        logger.fine("Repository LiSeD: Search for the corrispondent project");
        Project proj = null;
        ProjectIndex pind = null;
        try {
          pind = client.getProjectIndex();
        }
        catch (Exception e1) {
          responseMsg += " - You don't have the right permissions to get a "
              + "full list of all the locally created projects";
          e1.printStackTrace();
        }
        if (pind != null) {
          List<ProjectRef> list = pind.getProjectRef();
          String uri = null;
          boolean found = false;
          if (list != null) {
            Iterator<ProjectRef> it = list.iterator();
            ProjectRef pref = null;
            while (it.hasNext() && !found) {
              proj = client.getProject(pref);
              uri = getProjectProperty(HackystatConstants.REPOSITORY_LOCATION, "", proj).getValue();
              if (uri.equals(super.location)) {
                found = true;
              }
            }
            if (found) {
              if (!isProjectProfileAccessible(proj)) {
                return null;
              }
              // [3] Create the Repository as LinkedServiceData.
              Model rdfData = ModelFactory.createDefaultModel();
              super.initModel(rdfData);

              logger.fine("Repository LiSeD: Got project: " + proj.getName()
                  + " instance.  Now building repository LiSeD.");
              this.makeRepositoryData(rdfData, proj);
              if (additionalCachedInfo) {
                // Add all the logged user accessible cached data to the returned model
                rdfData = super.addAllAccessibleCachedData(rdfData);
              }
              String str_rdfData = null;
              if (media.equals(MediaType.TEXT_ALL) || media.equals(MediaType.TEXT_RDF_N3)
                  || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
                str_rdfData = super
                    .serializeRDFModel(rdfData, null, HackystatConstants.LANG_TURTLE);
              }
              else if (media.equals(MediaType.APPLICATION_ALL)
                  || media.equals(MediaType.APPLICATION_RDF_XML)) {
                str_rdfData = super.serializeRDFModel(rdfData,
                    HackystatConstants.RESOURCE_URI_BASE, HackystatConstants.LANG_RDFXML);
              }
              else if (media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)) {
                str_rdfData = super.serializeRDFModel(rdfData,
                    HackystatConstants.RESOURCE_URI_BASE, HackystatConstants.LANG_NTRIPLE);
              }
              this.lsdServer.getFrontSideCache().put(HackystatConstants.otherResourceID,
                  uriString + "/" + media, str_rdfData);

              logRequest("Repository", projectName);
              ret = super.getStringRepresentationFromRdf(str_rdfData, media);
              return ret;
            }
          }
        }
        setStatusError("Error creating Repository LiSeD.");
        ret = null;
      }
      catch (Exception e) {
        setStatusError("Error creating Repository LiSeD.", e);
        ret = null;
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;
  }
}

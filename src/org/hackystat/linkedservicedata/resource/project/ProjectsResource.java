package org.hackystat.linkedservicedata.resource.project;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
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
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Resource representing a list of project URIs.
 *
 * @author Myriam Leggieri.
 *
 */
public class ProjectsResource extends LiSeDProjectResource {

  public ProjectsResource(Context context, Request request, Response response)
      throws UnsupportedEncodingException {
    super(context, request, response);
    // TODO Auto-generated constructor stub
  }

  /**
   * Returns a RDF Model serialized as N3 containing linked data about all the available projects,
   * or a string stating "Permission denied" if not authorized.
   *
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    Logger logger = this.lsdServer.getLogger();
    logger.fine("ALL Projects Data as Linked Data: Starting");
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
        // [2] get all the Project information
        logger.fine("ALL Projects LiSeD: Requesting all projects data.");
        ProjectIndex pind = null;
        try {
          pind = client.getProjectIndex();
        }
        catch (Exception e1) {
          pind = client.getProjectIndex(authUser);
          // responseMsg += " - You don't have the right permissions to get a "
          // + "full list of all the locally created projects";
          // e1.printStackTrace();
        }
        Model rdfData = ModelFactory.createDefaultModel();
        super.initModel(rdfData);
        Bag bag = null;
        Project proj = null;
        if (pind != null) {
          List<ProjectRef> l = pind.getProjectRef();
          LinkedList<String> predicates = null;
          if (l != null) {
            if (queryStr != null) {
              predicates = extractPredicatesFromQuery();
            }
            else {
              predicates = new LinkedList<String>();
            }
            for (ProjectRef pref : l) {
              proj = client.getProject(pref);
              if (queryStr == null) {
                if (bag == null) {
                  bag = rdfData.createBag(uriString);
                }
                if (isProjectProfileAccessible(proj)) {
                  logger.fine("ALL Projects LiSeD: Got project: " + pref.getName() + " instance.  "
                      + "Now building LiSeD.");
                  // [3]Unify the Project LinkedServiceData with the other project data
                  bag.add(getProjectUri(proj.getOwner(), proj.getName()));
                }
              }
              else {
                /**
                 * Create a RDF model filled with all the projects available associated with all the
                 * properties required to allow the application of the Sparql 'Where' clause (if
                 * there's one, otherwise projects will be associated with all their associate-able
                 * properties)
                 */
                if (isProjectProfileAccessible(proj)
                    && !proj.getName().equals(HackystatConstants.DEFAULT_PROJECT)) {
                  logger.fine("ALL Projects LiSeD: Got project: " + pref.getName() + " instance.  "
                      + "Now building LiSeD.");
                  System.out.println("ALL Projects LiSeD: Got project: " + pref.getName()
                      + " instance.  " + "Now building LiSeD.");
                  // get interesting project data from the local host
                  this.makeProjectData(rdfData, proj, predicates);
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
              HackystatConstants.PROJECT_SPARQL_RESOURCE_TYPE);
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
        logRequest("ALL Projects for ", uriUser);
        if (queryStr != null) {
          ret = super.getStringRepresentationForSparqlResults(str_rdfData);
        }
        else {
          ret = super.getStringRepresentationFromRdf(str_rdfData, media);
        }
      }
      catch (Exception e) {
        setStatusError("Error creating ALL Projects LiSeD.", e);
        e.printStackTrace();
        ret = null;
      }
    }
    else {
      setStatusError("Error creating ALL Projects LiSeD.");
      ret = new StringRepresentation("You've requested a not supported " + "Media Type which is "
          + media);
    }
    return ret;
  }

}

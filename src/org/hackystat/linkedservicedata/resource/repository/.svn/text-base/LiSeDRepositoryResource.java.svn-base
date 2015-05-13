package org.hackystat.linkedservicedata.resource.repository;

import org.hackystat.linkedservicedata.externalDatasets.SindiceApiManager;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.DoapVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.Property;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Construct a repository resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LiSeDRepositoryResource extends LinkedServiceDataResource {

  private String repType = null;
  protected String location = null;

  public LiSeDRepositoryResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.location = ((String) request.getAttributes().get("location"));
  }

  @Override
  public Representation represent(Variant variant) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Creates and returns a RDF Model instance.
   *
   * @param userObj the User instance containing user data to be added to the model
   * @param additionalCachedInfo state if the user wants to retrieve all the cached data in addition
   * to the required user data
   * @return The RDF Model.
   * @throws Exception
   */
  protected Resource makeRepositoryData(Model m_returned, Project proj) throws Exception {
    Resource resource = null;
    Property webInterface = null, anonroot = null;

    repType = getProjectProperty(HackystatConstants.REPOSITORY_TYPE, "", proj).getValue();
    webInterface = getProjectProperty(HackystatConstants.REPOSITORY_WEB_INTERFACE, proj);
    anonroot = getProjectProperty(HackystatConstants.REPOSITORY_ANON_ROOT, proj);

    resource = LinkedServiceDataResource.createRepositoryResource(m_returned, location, repType);

    if (location != null) {
      resource.addProperty(DoapVocab.LOCATION, location);
    }
    if (webInterface != null) {
      resource.addProperty(DoapVocab.BROWSE, webInterface.getValue());
    }
    if (anonroot != null) {
      resource.addProperty(DoapVocab.ANON_ROOT, anonroot.getValue());
    }
    resource = addLinkedData(resource);
    resource.addProperty(m_returned
        .createProperty("http://dublincore.org/documents/dcmi-terms/#terms-isPartOf"),
        HackystatConstants.voIDURI);
    return resource;
  }

  /**
   * Adds linked data.
   *
   * @param resource
   * @return
   */
  private Resource addLinkedData(Resource resource) {
    // SINDICE
    String uri = SindiceApiManager.performTermSearch(new String[] { repType }, "AND", null);
    if (uri != null) {
      resource.addProperty(RDFS.seeAlso, uri);
    }
    return resource;
  }

}

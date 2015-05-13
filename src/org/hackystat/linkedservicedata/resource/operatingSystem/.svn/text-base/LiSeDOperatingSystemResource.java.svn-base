package org.hackystat.linkedservicedata.resource.operatingSystem;

import org.hackystat.linkedservicedata.externalDatasets.SindiceApiManager;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Construct an operatin system resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LiSeDOperatingSystemResource extends LinkedServiceDataResource {
  protected String osName = null;

  public LiSeDOperatingSystemResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.osName = ((String) request.getAttributes().get("osName"));
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
  protected Resource makeOperatingSystemData(Model m_returned) {
    Resource resource = createOperatingSystemResource(m_returned, osName);
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
    String uri = SindiceApiManager.performTermSearch(new String[] { osName }, "AND", null);
    if (uri != null) {
      resource.addProperty(RDFS.seeAlso, uri);
    }
    return resource;
  }
}

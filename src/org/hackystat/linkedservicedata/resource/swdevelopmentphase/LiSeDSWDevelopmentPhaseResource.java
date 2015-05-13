package org.hackystat.linkedservicedata.resource.swdevelopmentphase;

import java.util.Iterator;
import java.util.List;
import org.hackystat.linkedservicedata.externalDatasets.SindiceApiManager;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.Properties;
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
 * Construct a SW development phase resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LiSeDSWDevelopmentPhaseResource extends LinkedServiceDataResource {
  protected String phaseId = null;

  public LiSeDSWDevelopmentPhaseResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.phaseId = ((String) request.getAttributes().get("phaseId"));
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
  protected Resource makeSWDevelopmentPhaseData(Model m_returned, Project proj) throws Exception {
    Resource resource = createSWDevelopmentPhaseResource(m_returned, phaseId, proj.getOwner(), proj
        .getName());
    resource = addDevelopmentPhaseData(resource, m_returned, proj);
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
    String uri = SindiceApiManager.performTermSearch(new String[] { phaseId }, "AND", null);
    if (uri != null) {
      resource.addProperty(RDFS.seeAlso, uri);
    }
    return resource;
  }

  /**
   *
   * @param resource
   * @param m_returned
   * @param proj
   * @return
   * @throws SensorBaseClientException
   */
  private Resource addDevelopmentPhaseData(Resource resource, Model m_returned, Project proj)
      throws SensorBaseClientException {
    Properties props = proj.getProperties();
    // Project properties
    if (props != null) {
      List<Property> list = props.getProperty();
      if (list != null) {
        Iterator<Property> it = list.iterator();
        Property prop = null;
        String key = null, value = null;
        String[] elems = null, subelems = null;
        while (it.hasNext()) {
          prop = it.next();
          key = prop.getKey();
          value = prop.getValue();
          if (key.equals(HackystatConstants.NEXT_PHASES)) {
            // list of phases potentially related to one another,
            // in the form of: phaseId{SEPARATOR_ID}nextPhaseId phaseId{SEPARATOR_ID}nextPhaseId
            // etc.
            if (value.contains(phaseId)) {
              elems = value.split(HackystatConstants.SEPARATOR1_ID);
              for (int j = 0; j < elems.length; j++) {
                subelems = elems[j].split(HackystatConstants.SEPARATOR2_ID);
                if (elems.length == 2 && elems[0].equals(phaseId)) {
                  resource.addProperty(HackystatVocab.PRECEEDS, getDevPhaseUri(uriUser,
                      projectName, elems[1]));
                }
              }
            }
          }
          else if (key.equals(HackystatConstants.INVOLVED_USERS)) {
            // list of developers involved in this project in the form of:
            // phaseId{SEPARATOR_ID}userEmail phaseId{SEPARATOR_ID}userEmail etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              subelems = elems[i].split(HackystatConstants.SEPARATOR2_ID);
              if (subelems.length == 2 && subelems[0].equals(phaseId)) {
                resource.addProperty(HackystatVocab.INVOLVED_USER, getUserUri(subelems[1]));
              }
            }
          }
          else if (key.equals(HackystatConstants.POTENTIAL_ISSUES)) {
            // list of issues potentially related to the current data
            // in the form of: phaseId{SEPARATOR_ID}issueId phaseId{SEPARATOR_ID}issueId etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              subelems = elems[i].split(HackystatConstants.SEPARATOR2_ID);
              if (subelems.length == 2 && subelems[0].equals(phaseId)) {
                resource.addProperty(HackystatVocab.COULD_CAUSES, getIssueUri(subelems[1]));
              }
            }
          }
        }
      }
    }
    return resource;
  }
}

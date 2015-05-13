package org.hackystat.linkedservicedata.resource.issue;

import java.util.LinkedList;
import java.util.List;
import org.hackystat.linkedservicedata.externalDatasets.HackystatLiSeDManager;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.BaetleVocab;
import org.hackystat.linkedservicedata.vocabularies.CommontagVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Construct the Issue resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LiSeDIssueResource extends LinkedServiceDataResource {
  protected String issueId = null;

  public LiSeDIssueResource(Context context, Request request, Response response) {
    super(context, request, response);
    this.issueId = ((String) request.getAttributes().get("issueId"));
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
  protected Resource makeIssueData(Model m_returned, SensorData sd, String id, String type,
      LinkedList<String> predicates) throws Exception {
    Resource resource = createIssueResource(m_returned, id, type);
    SensorBaseClient client = getSensorBaseClient();
    if (client == null) {
      return null;
    }
    resource = addIssueData(resource, sd, predicates);
    resource = addIssueLinkedData(resource, sd, predicates);
    resource.addProperty(m_returned
        .createProperty("http://dublincore.org/documents/dcmi-terms/#terms-isPartOf"),
        HackystatConstants.voIDURI);
    return resource;
  }

  public String getIssueId(SensorData sd) {
    String ret = null;
    Property prop = getSensorDataProperty(HackystatConstants.ISSUE_ID_PROPERTY_KEY, sd);
    if (prop != null) {
      ret = prop.getValue();
    }
    return ret;
  }

  private Resource addIssueLinkedData(Resource resource, SensorData sd,
      LinkedList<String> predicates) throws SensorBaseClientException {
    /**
     * Potentially useful ontology's predicates: BaetleVocab.DEPENDS_ON BaetleVocab.DUPLICATE
     * BaetleVocab.PROJECT BaetleVocab.RELATES_TO BaetleVocab.REPORTER BaetleVocab.SUBTASK
     * Helios_btVocab.IS_MERGED_INTO Helios_btVocab.REPORTED_ALSO_IN
     * Helios_btVocab.REPORTED_UPSTREAM_IN
     */
    String source = sd.getResource(), sourceCurr = null, uri = null;
    LinkedList<String> sameAsList = new LinkedList<String>();

    // Hackystat servers
    // automatic search for duplicates
    if (predicates.isEmpty() || predicates.contains(OWL.sameAs.getLocalName())) {
      SensorDataIndex sdind = null;
      try {
        sdind = super.getSensorBaseClient().getSensorDataIndex();
      }
      catch (Exception e1) {
        sdind = super.getSensorBaseClient().getSensorDataIndex(authUser);
      }
      if (sdind != null) {
        List<SensorDataRef> list = sdind.getSensorDataRef();
        SensorData sdCurr = null;
        String id = null;
        for (SensorDataRef sdref : list) {
          if (sdref.getSensorDataType().equals(HackystatConstants.ISSUE)) {
            sdCurr = super.getSensorBaseClient().getSensorData(sdref);
            sourceCurr = sdCurr.getResource();
            if (source.equals(sourceCurr)) {
              id = getIssueId(sdCurr);
              if (id != null) {
                uri = getIssueUri(id);
                resource.addProperty(OWL.sameAs, uri);
                sameAsList.add(uri);
              }
            }
          }
        }
      }
    }
    if (predicates.isEmpty() || predicates.contains(BaetleVocab.DUPLICATE.getLocalName())) {
      // duplicates manually annotated by users
      Property prop = getSensorDataProperty(HackystatConstants.ISSUE_DUPLICATES_LIST, sd);
      if (prop != null) {
        String[] dups = prop.getValue().split(HackystatConstants.SEPARATOR1_ID);
        for (int i = 0; i < dups.length; i++) {
          // I don't try to get the 'issue uri' of dups[i] becuase it could have
          // a different uri pattern because of coming from external systems
          resource.addProperty(BaetleVocab.DUPLICATE, getIssueUri(dups[i]));
          sameAsList.add(dups[i]);
        }
      }
    }

    // Hackystat servers
    // search for bugs having at least one same tag
    if (predicates.isEmpty() || predicates.contains(RDFS.seeAlso.getLocalName())) {
      String[] similarIssues = null, tags = null;
      Property prop = getSensorDataProperty(HackystatConstants.ISSUE_TAGS_LIST, sd);
      if (prop != null) {
        tags = prop.getValue().split(HackystatConstants.SEPARATOR1_ID);
        HackystatLiSeDManager hackman = new HackystatLiSeDManager();
        similarIssues = hackman.getSeeAlsoLinkableIssues(tags);
        if (similarIssues != null) {
          for (int i = 0; i < similarIssues.length; i++) {
            if (!sameAsList.contains(similarIssues[i])) {
              resource.addProperty(RDFS.seeAlso, similarIssues[i]);
            }
          }
        }
      }
    }

    return resource;
  }

  private Resource addIssueData(Resource resource, SensorData sd, LinkedList<String> predicates) {
    Property prop = null;
    if (predicates.isEmpty() || predicates.contains(BaetleVocab.ASSIGNED_TO.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_OWNER_PROPERTY_KEY, sd);
      if (prop != null) {
        resource.addProperty(BaetleVocab.ASSIGNED_TO, getUserUri(prop.getValue()));
      }
    }
    if (predicates.isEmpty() || predicates.contains(BaetleVocab.TARGET_MILESTONE.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_MILESTONE_PROPERTY_KEY, sd);
      if (prop != null) {
        resource.addProperty(BaetleVocab.TARGET_MILESTONE, prop.getValue());
      }
    }
    if (predicates.isEmpty() || predicates.contains(BaetleVocab.PRIORITY.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_PRIORITY_PROPERTY_KEY, sd);
      if (prop != null) {
        resource.addProperty(BaetleVocab.PRIORITY, prop.getValue());
      }
    }
    if (predicates.isEmpty() || predicates.contains(BaetleVocab.NAME.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_ID_PROPERTY_KEY, sd);
      if (prop != null) {
        resource.addProperty(BaetleVocab.NAME, prop.getValue());
      }
    }
    if (predicates.isEmpty() || predicates.contains(BaetleVocab.STATUS.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_STATUS_PROPERTY_KEY, sd);
      if (prop != null) {
        resource.addProperty(BaetleVocab.STATUS, prop.getValue());
      }
    }
    if (predicates.isEmpty() || predicates.contains(CommontagVocab.TAGGED.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_TAGS_LIST, sd);
      if (prop != null) {
        String[] tags = prop.getValue().split(HackystatConstants.SEPARATOR1_ID);
        for (int i = 0; i < tags.length; i++) {
          resource.addProperty(CommontagVocab.TAGGED, tags[i]);
        }
      }
    }
    if (predicates.isEmpty() || predicates.contains(BaetleVocab.CREATED.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_OPENED_TIME_PROPERTY_KEY, sd);
      if (prop != null) {
        resource.addProperty(BaetleVocab.CREATED, prop.getValue());
      }
    }
    if (predicates.isEmpty() || predicates.contains(HackystatVocab.CLOSED_TIME.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_CLOSED_TIME_PROPERTY_KEY, sd);
      if (prop != null) {
        resource.addProperty(HackystatVocab.CLOSED_TIME, prop.getValue());
      }
    }
    if (predicates.isEmpty() || predicates.contains(HackystatVocab.MODIFIED_TIME.getLocalName())) {
      prop = getSensorDataProperty(HackystatConstants.ISSUE_MODIFIED_TIME_PROPERTY_KEY, sd);
      if (prop != null) {
        resource.addProperty(HackystatVocab.MODIFIED_TIME, prop.getValue());
      }
    }
    if (predicates.contains(HackystatVocab.PRIVACY_LEVEL.getLocalName()) || predicates.isEmpty()) {
      prop = getSensorDataProperty(HackystatConstants.SENSORDATA_VISIBILITY, sd);
      if (prop != null) {
        String visibility = prop.getValue();
        if (predicates.contains(HackystatVocab.PRIVACY_LEVEL.getLocalName())
            || predicates.isEmpty()) {
          resource.addProperty(HackystatVocab.PRIVACY_LEVEL, visibility);
        }
      }
    }
    return resource;
  }
}

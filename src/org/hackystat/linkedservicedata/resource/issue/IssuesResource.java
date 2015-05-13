package org.hackystat.linkedservicedata.resource.issue;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.resource.sensordata.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
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
 * Resource representing a list of Issue URIs.
 *
 * @author Myriam Leggieri.
 *
 */
public class IssuesResource extends LiSeDIssueResource {

  public IssuesResource(Context context, Request request, Response response)
      throws UnsupportedEncodingException {
    super(context, request, response);
    // there are no users-specific data to be set
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
    logger.fine("Issues Data as Linked Data: Starting");
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
        // [1] get the SensorBaseClient for the user making this
        // request.
        SensorBaseClient client = super.getSensorBaseClient();

        // [2] Check the front side cache and return if the LiSeD is
        // found and is OK to access.
        String cachedDpd = this.lsdServer.getFrontSideCache().get(
            HackystatConstants.otherResourceID, uriString + "/" + media);
        if ((cachedDpd != null)) {
          if (queryStr != null) {
            return super.getStringRepresentationForSparqlResults(cachedDpd);
          }
          else {
            return super.getStringRepresentationFromRdf(cachedDpd, media);
          }
        }
        // [2] get all Users information
        logger.fine("Issues LiSeD: Requesting ALL the issues data");
        SensorDataIndex sdind = null;
        try {
          sdind = client.getSensorDataIndex();
        }
        catch (Exception e1) {
          sdind = client.getSensorDataIndex(authUser);
          // responseMsg += " - You don't have the right permissions to get a "
          // + "full list of all the locally stored issues";
          // e1.printStackTrace();
        }
        Model rdfData = ModelFactory.createDefaultModel();
        super.initModel(rdfData);
        Bag bag = null;
        String issueId = null;
        if (sdind != null) {
          List<SensorDataRef> l = sdind.getSensorDataRef();
          LinkedList<String> predicates = null;
          if (l != null) {
            SensorData sd = null;
            if (queryStr != null) {
              predicates = extractPredicatesFromQuery();
              if (predicates.isEmpty()) {
                setStatusError("Due to performance reasons "
                    + "a search without any filter is not allowed");
                return null;
              }
            }
            else {
              predicates = new LinkedList<String>();
            }
            LinkedList<String> insertedID = new LinkedList<String>();
            ListIterator<SensorDataRef> itSDR = l.listIterator(l.size());
            SensorDataRef ur = null;
            // int count = 0;
            while (itSDR.hasPrevious()) {
              ur = itSDR.previous();
              // count++;
              if (ur.getSensorDataType().equals(HackystatConstants.ISSUE)) {
                sd = client.getSensorData(ur);
                if (isBugAccessible(sd)) {
                  Property prop = getSensorDataProperty(HackystatConstants.ISSUE_ID_PROPERTY_KEY,
                      sd);
                  if (queryStr != null) {
                    logger.fine("Issue LiSeD: Got issue: instance. Now building LiSeD.");
                    if (prop != null) {
                      issueId = prop.getValue();
                    }
                    if (!insertedID.contains(issueId)) {
                      insertedID.add(issueId);
                      prop = getSensorDataProperty(HackystatConstants.ISSUE_TYPE_PROPERTY_KEY, sd);
                      if (prop != null) {
                        /**
                         * Create a RDF model filled with all the issues available associated with
                         * all the properties required to allow the application of the Sparql
                         * 'Where' clause (if there's one, otherwise users will be associated with
                         * all their associate-able properties)
                         */
                        this.makeIssueData(rdfData, sd, issueId, prop.getValue(), predicates);
                      }
                    }
                  }
                  else {
                    if (prop != null) {
                      logger.fine("Issues LiSeD: Got issue: " + prop.getValue() + ". "
                          + "Now building LiSeD.");
                      if (bag == null) {
                        bag = rdfData.createBag(uriString);
                      }
                      String issueUri = getIssueUri(prop.getValue());
                      if (!bag.contains(issueUri))
                        bag.add(issueUri);
                    }
                  }
                }
              }
            }
          }
        }
        if (additionalCachedInfo) {
          // Add all the logged user accessible cached data to
          // the returned model
          rdfData = super.addAllAccessibleCachedData(rdfData);
        }
        String str_rdfData = null;
        if (queryStr != null) {
          Document doc = performSearchLocallyAndPropagate(rdfData,
              HackystatConstants.ISSUE_SPARQL_RESOURCE_TYPE);
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
        this.lsdServer.getFrontSideCache().put(HackystatConstants.otherResourceID,
            uriString + "/" + media, str_rdfData);
        logRequest("Issues", uriString);
        if (queryStr != null) {
          ret = super.getStringRepresentationForSparqlResults(str_rdfData);
        }
        else {
          ret = super.getStringRepresentationFromRdf(str_rdfData, media);
        }
        return ret;

      }
      catch (Exception e) {
        setStatusError("Error creating Issues LiSeD.", e);
        e.printStackTrace();
        ret = null;
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;

  }

}

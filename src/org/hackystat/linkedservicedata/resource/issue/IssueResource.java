package org.hackystat.linkedservicedata.resource.issue;

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
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Resource representing an Issue.
 *
 * @author Myriam Leggieri.
 *
 */
public class IssueResource extends LiSeDIssueResource {

  public IssueResource(Context context, Request request, Response response) {
    super(context, request, response);
    // TODO Auto-generated constructor stub
  }

  /**
   * Returns a RDF Model serialized as N3 containing the Source linked data associated with the
   * specified path
   *
   * @param variant The representational variant requested.
   * @return The representation.
   */
  @Override
  public Representation represent(Variant variant) {
    Logger logger = this.lsdServer.getLogger();
    String resourceName = "Issue";
    MediaType media = variant.getMediaType();
    Representation ret = null;
    logger.fine(resourceName + " as Linked Data: Starting");
    if (media.equals(MediaType.APPLICATION_ALL) || media.equals(MediaType.TEXT_ALL)
        || media.equals(MediaType.TEXT_RDF_N3) || media.equals(MediaType.APPLICATION_RDF_XML)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
      try {
        // Check the front side cache and return if the LiSeD is
        // found and is OK to access.
        String cachedDpd = this.lsdServer.getFrontSideCache().get(
            HackystatConstants.otherResourceID, uriString + "/" + media);
        if ((cachedDpd != null)) {
          return super.getStringRepresentationFromRdf(cachedDpd, media);
        }
        // get the SensorBaseClient for the user making this
        // request.
        SensorBaseClient client = super.getSensorBaseClient();
        // get all the resource information
        logger.fine(resourceName + " LiSeD: Requesting data");
        SensorDataIndex sdind = null;
        try {
          sdind = client.getSensorDataIndex();
        }
        catch (Exception e1) {
          sdind = client.getSensorDataIndex(authUser);
          // responseMsg += " - You don't have the right permissions to get a "
          // + "full list of all the locally stored issues that's needed to retrieve "
          // + "the bug you've specified.";
          // e1.printStackTrace();
          // return null;
        }
        // [3] Create the Repository Branch as LinkedServiceData.
        if (sdind != null) {
          List<SensorDataRef> l = sdind.getSensorDataRef();
          Property prop = null;
          if (l != null) {
            SensorData sd = null, sdSearched = null;
            Model rdfData = ModelFactory.createDefaultModel();
            super.initModel(rdfData);
            /**
             * While waiting for the SensorBase version able to return a unique issue profile per
             * issue ID filled in with all the latest changes, only the last instance stored per
             * issue ID is considered in order to optimize performance.
             */
            ListIterator<SensorDataRef> it = l.listIterator(l.size());
            SensorDataRef ur = null;
            while (it.hasPrevious() && sdSearched == null) {
              ur = it.previous();
              if (ur.getSensorDataType().equals(HackystatConstants.ISSUE)) {
                sd = client.getSensorData(ur);
                prop = getSensorDataProperty(HackystatConstants.ISSUE_ID_PROPERTY_KEY, sd);
                if (prop != null && issueId.equals(prop.getValue())) {
                  sdSearched = sd;
                }
              }
            }
            if (sdSearched != null && isBugAccessible(sdSearched)) {
              logger.fine(resourceName + " LiSeD: Got SensorDataIndex from Sensorbase. "
                  + "Now building LiSeD.");
              prop = getSensorDataProperty(HackystatConstants.ISSUE_TYPE_PROPERTY_KEY, sd);
              if (prop != null) {
                this.makeIssueData(rdfData, sdSearched, issueId, prop.getValue(),
                    new LinkedList<String>());
              }
              if (additionalCachedInfo) {
                // Add all the logged user accessible cached data to
                // the returned model
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
              logRequest(resourceName, issueId);
              ret = super.getStringRepresentationFromRdf(str_rdfData, media);
            }
            else {
              ret = new StringRepresentation("No available data.");
            }
          }
        }
      }
      catch (Exception e) {
        setStatusError("Error creating " + resourceName + "  LiSeD.", e);
        e.printStackTrace();
        ret = null;
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    System.out.println(ret);
    return ret;
  }

}

package org.hackystat.linkedservicedata.resource.operatingSystem;

import java.util.logging.Logger;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Resource representing an operating system.
 *
 * @author Myriam Leggieri.
 *
 */
public class OperatingSystemResource extends LiSeDOperatingSystemResource {

  public OperatingSystemResource(Context context, Request request, Response response) {
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
    String resourceName = "Operating System";
    MediaType media = variant.getMediaType();
    Representation ret = null;
    logger.fine(resourceName + " as Linked Data: Starting");
    if (media.equals(MediaType.APPLICATION_ALL) || media.equals(MediaType.TEXT_ALL)
        || media.equals(MediaType.TEXT_RDF_N3) || media.equals(MediaType.APPLICATION_RDF_XML)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
      try {
        // [2] Check the front side cache and return if the LiSeD is found and is OK to access.
        String cachedDpd = this.lsdServer.getFrontSideCache().get(
            HackystatConstants.otherResourceID, uriString + "/" + media);
        if ((cachedDpd != null)) {
          return super.getStringRepresentationFromRdf(cachedDpd, media);
        }
        // [2] get all the resource information
        logger.fine(resourceName + " LiSeD: Requesting data");

        // [3] Create the Repository Branch as LinkedServiceData.
        Model rdfData = ModelFactory.createDefaultModel();
        super.initModel(rdfData);
        logger.fine(resourceName + " LiSeD: " + "Now building LiSeD.");
        this.makeOperatingSystemData(rdfData);

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
        this.lsdServer.getFrontSideCache().put(HackystatConstants.otherResourceID,
            uriString + "/" + media, str_rdfData);

        logRequest(resourceName, osName);
        ret = super.getStringRepresentationFromRdf(str_rdfData, media);
      }
      catch (Exception e) {
        setStatusError("Error creating " + resourceName + "  LiSeD.", e);
        ret = null;
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;
  }

}

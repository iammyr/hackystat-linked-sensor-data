package org.hackystat.linkedservicedata.resource.dataset;

import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Resource representing the description of the published Hackystat dataset.
 *
 * @author Myriam Leggieri.
 *
 */
public class VocabVoIDResource extends LinkedServiceDataResource {

  public VocabVoIDResource(Context context, Request request, Response response) {
    super(context, request, response);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Representation represent(Variant variant) {
    MediaType media = variant.getMediaType();
    Representation ret = null;
    if (media.equals(MediaType.APPLICATION_ALL) || media.equals(MediaType.TEXT_ALL)
        || media.equals(MediaType.TEXT_RDF_N3) || media.equals(MediaType.APPLICATION_RDF_XML)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_NTRIPLES)
        || media.getName().equals(HackystatConstants.MEDIA_TYPE_TURTLE)) {
      // Check the front side cache
      String cachedDpd = this.lsdServer.getFrontSideCache().get(HackystatConstants.otherResourceID,
          uriString + "/" + media);
      if ((cachedDpd != null)) {
        return super.getStringRepresentationFromRdf(cachedDpd, media);
      }
      String str_rdfData = null;
      Model rdfData = HackystatConstants.voIDModel;
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
      ret = super.getStringRepresentationFromRdf(str_rdfData, media);
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;
  }

}

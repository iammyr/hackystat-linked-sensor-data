package org.hackystat.linkedservicedata.resource.dataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Resource representing the Hackystat vocabulary.
 *
 * @author Myriam Leggieri.
 *
 */
public class VocabResource extends LinkedServiceDataResource {

  public VocabResource(Context context, Request request, Response response) {
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
      StringBuilder contents = new StringBuilder();
      String schemapath = HackystatVocab.class.getResource("hackystat.rdf").getPath();
      try {
        BufferedReader input = new BufferedReader(new FileReader(schemapath));

        String line = null; // not declared within while loop
        /*
         * readLine is a bit quirky : it returns the content of a line MINUS the newline. it returns
         * null only for the END of the stream. it returns an empty String if two newlines appear in
         * a row.
         */
        try {
          while ((line = input.readLine()) != null) {
            contents.append(line);
            contents.append(System.getProperty("line.separator"));
          }
        }
        catch (IOException e) {
          getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
          e.printStackTrace();
          return ret;
        }
        finally {

          input.close();

        }
        str_rdfData = contents.toString();
        Model rdfData = super.deserializeRDFModel(str_rdfData, HackystatVocab.NS,
            HackystatConstants.LANG_RDFXML);
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
      catch (IOException e) {
        getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        e.printStackTrace();
        return ret;
      }
    }
    else {
      setStatusError("You've request a not supported Media Type " + "which is " + media);
    }
    return ret;
  }

}

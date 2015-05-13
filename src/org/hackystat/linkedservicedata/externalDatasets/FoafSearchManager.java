package org.hackystat.linkedservicedata.externalDatasets;

import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Handler searches for FOAF profiles.
 *
 * @author Myriam Leggieri.
 *
 */
public class FoafSearchManager {

  // public static void main(String[] args) {
  // String ret = null;
  // ret = FoafSearchManager.findFoafOnQdosByIFP("http://myr.netsons.org/wordpress");
  // System.out.println("Foaf Profile URI:\n" + ret);
  // ret = SindiceApiManager.findFoafOnSindice(null, null, null, "tuukka", "hastrup");
  // System.out.println("Foaf Profile URI:\n" + ret);
  // }

  /**
   * Searches for the FOAF profile's URI
   *
   * @param url - can be any object of the followinf FOAF Inverse Functional Properties (IFPs):
   * foaf:mbox, foaf:mbox_sha1sum, foaf:homepage or foaf:weblog
   * @return
   */
  public static String findFoafOnQdosByIFP(String url) {
    String ret = null, appendice = "&ifp=";
    Model rdfModel = null;
    try {
      url = java.net.URLEncoder.encode(url, "UTF-8");
      String urlStr = HackystatConstants.FOAFQDOS_PATTERN + url + appendice;
      Reference reference = new Reference(urlStr);
      Request request = null;
      request = new Request(Method.GET, reference);
      System.out.println("LinkedServiceData Tracing: " + Method.GET + " " + reference);
      request.getClientInfo().getAcceptedMediaTypes().add(
          new Preference<MediaType>(MediaType.APPLICATION_RDF_XML));
      Response response = new Client(Protocol.HTTP).handle(request);
      String resp = response.getEntity().getText();
      Status status = response.getStatus();
      System.out.println("  => " + status.getCode() + " " + status.getDescription());
      if (status.getCode() == 200) {
        if (resp != null) {
          // System.out.println(resp);
          rdfModel = LinkedServiceDataResource.deserializeRDFModel(resp, null,
              HackystatConstants.LANG_RDFXML_ABBREV);
          StmtIterator it = rdfModel.listStatements(null, OWL.sameAs, (RDFNode) null);
          Statement st = null;
          if (it.hasNext()) {
            st = it.next();
            ret = st.getSubject().getURI();
          }
        }
      }
      else {
        System.err.println("Request failed.");
      }
    }
    catch (Exception e) {
      System.err.println("Unable to perform search for FOAF profiles");
      e.printStackTrace();
    }
    return ret;
  }

}

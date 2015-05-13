package org.hackystat.linkedservicedata.externalDatasets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.FoafVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.SiocVocab;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Handler requests for the SiocLog API.
 *
 * @author Myriam Leggieri
 *
 */
public class SioclogManager {

  // public static void main(String[] args) {
  // String nick = "Cloud_";
  // try {
  // Object[] resp = SioclogManager.getUserCommunicationData(nick);
  // if (resp != null) {
  // System.out.println("ENTERED CHANNELS LIST");
  // LinkedList<String> channels = (LinkedList<String>) resp[0];
  // for (String str : channels) {
  // System.out.println(str);
  // }
  // System.out.println("NUMBER OF MESSAGES SENT");
  // System.out.println(resp[1]);
  // }
  // else {
  // System.out.println("INFO NOT AVAILABLE");
  // }
  // }
  // catch (Exception e) {
  // e.printStackTrace();
  // }
  // }

  /**
   *
   * @param nick
   * @return
   */
  public static String getLinkableUri(String nick) {
    String ret = null;
    String userHash = "#user", urlStr = HackystatConstants.SIOCLOG_PROJECT_PATTERN;
    Model rdfModel = ModelFactory.createDefaultModel();
    Resource subj = null;
    urlStr = HackystatConstants.SIOCLOG_PROJECT_PATTERN + nick + userHash;
    rdfModel = getSiocLogModel(urlStr);
    if (rdfModel != null) {
      subj = rdfModel.createResource(urlStr);
      if (rdfModel.contains(null, FoafVocab.HOLDS_ACCOUNT, subj)) {
        // this is the only way to check if there have been any data collected for the user of
        // interest
        ret = urlStr;
      }
    }
    return ret;
  }

  /**
   *
   * @param nick
   * @return
   */
  public static Object[] getUserCommunicationData(String nick) {
    Object[] ret = null;
    LinkedList<String> channelsEntered = new LinkedList<String>();
    String userHash = "#user", urlStr = HackystatConstants.SIOCLOG_PROJECT_PATTERN + nick
        + userHash, numPosts = null;
    Model rdfModel = getSiocLogModel(urlStr);
    if (rdfModel != null) {
      Resource subj = rdfModel.createResource(urlStr), channel = null;
      if (rdfModel.contains(null, FoafVocab.HOLDS_ACCOUNT, subj)) { // this is the only way to check
        // if there have been any data
        // collected for the user of
        // interest
        StmtIterator stmtit = rdfModel.listStatements(null, SiocVocab.HAS_SUBSCRIBER, subj);
        Statement statement = null;
        while (stmtit.hasNext()) {
          statement = stmtit.next();
          channel = statement.getSubject();
          channelsEntered.add(channel.getURI());
        }
        NodeIterator nodeit = rdfModel.listObjectsOfProperty(subj, SiocVocab.NUM_POSTS);
        RDFNode x = null;
        if (nodeit.hasNext()) {
          x = nodeit.next();
          if (x.isLiteral()) {
            numPosts = ((Literal) x).getLexicalForm();
          }
        }
        ret = new Object[] { channelsEntered, numPosts };
      }
    }
    return ret;
  }

  /**
   *
   * @param urlStr
   * @return
   */
  private static Model getSiocLogModel(String urlStr) {
    Model rdfModel = null;
    Reference reference = new Reference(urlStr);
    Request request = null;
    request = new Request(Method.GET, reference);
    System.out.println("LinkedServiceData Tracing: " + Method.GET + " " + reference);
    request.getClientInfo().getAcceptedMediaTypes().add(
        new Preference<MediaType>(MediaType.APPLICATION_ALL));
    Response response = new Client(Protocol.HTTP).handle(request);
    try {
      String resp = response.getEntity().getText();
      Status status = response.getStatus();
      System.out.println("  => " + status.getCode() + " " + status.getDescription());
      if (status.getCode() == 200) {
        if (resp != null) {
          System.out.println(resp);
          rdfModel = ModelFactory.createDefaultModel();
          InputStream is = new ByteArrayInputStream(resp.getBytes("UTF-8"));
          rdfModel.read(is, null, HackystatConstants.LANG_TURTLE);
          rdfModel = LinkedServiceDataResource.deserializeRDFModel(resp, null,
              HackystatConstants.LANG_TURTLE);

        }
      }
      else {
        System.err.println("Request failed.");
      }
    }
    catch (Exception e) {
      System.err.println("Request failed.");
      e.printStackTrace();
      return null;
    }
    return rdfModel;
  }

}

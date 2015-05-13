package org.hackystat.linkedservicedata.externalDatasets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.w3c.dom.Document;

/**
 * Handler of requests to search for links into both the external datasets amd the Hackystat network
 * itself.
 *
 * @author Myriam Leggieri.
 *
 */
public class HackystatLiSeDManager {

  private Client client = new Client(Protocol.HTTP);

  /**
   * Get Issues stored in external datasets which can be involved in a SeeAlso relationship, given
   * the specified issue's tags.
   *
   * @param tags tags.
   * @return array of issue URIs.
   */
  public String[] getSeeAlsoLinkableIssues(String[] tags) {
    String[] ret = null;
    String query = "PREFIX baetle: <http://xmlns.com/baetle/#>\n"
        + "PREFIX hacky: <http://dasha.ics.hawaii.edu:9875/linkedservicedata/vocab/>\n"
        + "SELECT ?uri\n WHERE {\n";
    try {
      if (tags != null) {
        for (int i = 0; i < tags.length; i++) {
          query += "OPTIONAL { ?uri hacky:tag ";
          if (!tags[i].startsWith("\"")) {
            tags[i] = java.net.URLEncoder.encode(tags[i], "UTF-8");
            query += "\"" + tags[i] + "\" } . \n";
          }
          else {
            tags[i] = java.net.URLEncoder.encode(tags[i], "UTF-8");
            query += tags[i] + "} . \n";
          }
        }
      }
      query += "    }";

      ret = getSearchResults(query, true, HackystatConstants.ISSUE_SPARQL_RESOURCE_TYPE);
      if (ret != null) {
        for (int i = 0; i < ret.length; i++) {
          ret[i] = ret[i].split(HackystatConstants.SEPARATOR1_ID)[1];
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param email
   * @param homepage
   * @param blog
   * @param name
   * @param surname
   * @return
   */
  public String[] getSeeAlsoLinkableUsers(String email, String homepage, String blog, String name,
      String surname) {
    String ret[] = null;
    String query = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + "SELECT ?uri\n WHERE {\n";
    try {
      if (email != null) {
        query += "OPTIONAL { ?uri foaf:mbox \"" + java.net.URLEncoder.encode(email, "UTF-8")
            + "\" } . \n";
      }
      if (homepage != null) {
        query += "OPTIONAL { ?uri foaf:homepage \"" + java.net.URLEncoder.encode(homepage, "UTF-8")
            + "\" } . \n";
      }
      if (blog != null) {
        query += "OPTIONAL { ?uri foaf:weblog \"" + java.net.URLEncoder.encode(blog, "UTF-8")
            + "\" } . \n";
      }
      if (name != null) {
        query += "OPTIONAL { ?uri foaf:firstName \"" + java.net.URLEncoder.encode(name, "UTF-8")
            + "\" } . \n";
      }
      if (surname != null) {
        query += "OPTIONAL { ?uri foaf:surname \"" + java.net.URLEncoder.encode(surname, "UTF-8")
            + "\" } . \n";
      }
      query += "    }";

      ret = getSearchResults(query, true, HackystatConstants.USER_SPARQL_RESOURCE_TYPE);
      if (ret != null) {
        for (int i = 0; i < ret.length; i++) {
          ret[i] = ret[i].split(HackystatConstants.SEPARATOR1_ID)[1];
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param title
   * @return
   */
  public String[] getSameAsLinkableProjects(String title) {
    String ret[] = null;
    if (title == null)
      return ret;
    try {
      title = java.net.URLEncoder.encode(title, "UTF-8");
      String query = "PREFIX doap: <http://usefulinc.com/ns/doap#>\n"
          + "SELECT ?uri\n WHERE {\n ?uri doap:name ";
      if (!title.startsWith("\"")) {
        query += "\"" + title + "\" . \n";
      }
      else {
        query += title + " . \n";
      }
      ;

      ret = getSearchResults(query, true, HackystatConstants.PROJECT_SPARQL_RESOURCE_TYPE);
      if (ret != null) {
        for (int i = 0; i < ret.length; i++) {
          ret[i] = ret[i].split(HackystatConstants.SEPARATOR1_ID)[1];
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param tags
   * @param tools
   * @return
   */
  public String[] getSeeAlsoLinkableProjects(String[] tags, String[] tools) {
    String ret[] = null;
    if (tags == null && tools == null)
      return ret;
    String query = "PREFIX doap: <http://usefulinc.com/ns/doap#>\n"
        + "PREFIX hacky: <http://dasha.ics.hawaii.edu:9875/linkedservicedata/vocab/>\n"
        + "SELECT ?uri\n WHERE {\n";
    try {
      if (tags != null) {
        for (int i = 0; i < tags.length; i++) {
          query += "OPTIONAL { ?uri doap:category ";
          if (!tags[i].startsWith("\"")) {
            tags[i] = java.net.URLEncoder.encode(tags[i], "UTF-8");
            query += "\"" + tags[i] + "\" } . \n";
          }
          else {
            tags[i] = java.net.URLEncoder.encode(tags[i], "UTF-8");
            query += tags[i] + " } . \n";
          }
        }
      }
      if (tools != null) {
        for (int i = 0; i < tools.length; i++) {
          query += "OPTIONAL { ?uri hacky:usesTool ";
          if (!tools[i].startsWith("\"")) {
            tools[i] = java.net.URLEncoder.encode(tools[i], "UTF-8");
            query += "\"" + tools[i] + "\" } . \n";
          }
          else {
            tools[i] = java.net.URLEncoder.encode(tools[i], "UTF-8");
            query += tools[i] + " } . \n";
          }
        }
      }
      query += "    }";

      ret = getSearchResults(query, true, HackystatConstants.PROJECT_SPARQL_RESOURCE_TYPE);
      if (ret != null) {
        for (int i = 0; i < ret.length; i++) {
          ret[i] = ret[i].split(HackystatConstants.SEPARATOR1_ID)[1];
        }
      }
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param query
   * @param queryToLinkData
   * @param endpoint
   * @return
   */
  public String[] getSearchResults(String query, boolean queryToLinkData, String endpoint) {
    String[] ret = null;
    try {
      String user = null, passw = null;
      for (String uri : HackystatConstants.hackystatLisedServers_list.keySet()) {
        if (!HackystatConstants.noLoop_toPropagate.contains(uri)) {
          user = HackystatConstants.hackystatLisedServers_list.get(uri)[0];
          passw = HackystatConstants.hackystatLisedServers_list.get(uri)[1];
          if (queryToLinkData) {
            ret = handleHackystatSparqlEndpoint(uri, query, user, passw, null, null, endpoint);
          }
          else {
            ret = handleHackystatSparqlEndpoint(uri, query, user, passw,
                HackystatConstants.noLoop_toPropagate,
                HackystatConstants.hackystatLisedServers_list, endpoint);
          }
        }
      }
    }
    catch (Exception e) {
      System.err.print(e.getMessage());
      e.printStackTrace();
    }
    finally {
      // clear();
    }
    return ret;
  }

  /**
   * This method creates, sends, and returns (after some handling), the response received performing
   * the specified query on the Hackystat Sparql endpoint for projects exposed by the specified
   * LiSeD server. To access LiSeD server is necessary to receive also the username and password of
   * its admin.
   *
   * @param hackystatLiSeDUri uri of the LiSeD server that exposes the Sparql endpoint of interest
   * @param query Sparql query not encoded
   * @param user username of the LiSeD server's admin
   * @param passw password of the LiSeD server's admin
   * @param serversToNotRecall list of servers that mustn't be re-called in order to avoid loop
   * @return Response of the invoked Sparql endpoint as an array of strings in the form of:
   * variable{HackystatConstants.SEPARATOR1_ID}value (value can be a literal or a uri).
   * @throws IOException
   */
  public String[] handleHackystatSparqlEndpoint(String hackystatLiSeDUri, String query,
      String user, String passw, LinkedList<String> serversToNotRecall,
      HashMap<String, String[]> localAssociatedServersList, String endpoint) throws IOException {
    query = java.net.URLEncoder.encode(query, "UTF-8");
    String[] ret = null;
    String url = hackystatLiSeDUri + endpoint + query;

    Reference reference = new Reference(url);
    Request request = null;
    if (serversToNotRecall != null) {
      // update the servers list that will be propagated by this
      // server with servers called by this server
      for (String uri : localAssociatedServersList.keySet()) {
        if (!serversToNotRecall.contains(uri)) {
          serversToNotRecall.add(uri);
        }
      }
      Representation entity = null;
      entity = LinkedServiceDataResource.getStringRepresentation(serversToNotRecall);
      request = new Request(Method.GET, reference, entity);
      System.out.println("LinkedServiceData Tracing: " + Method.GET + " " + reference
          + "\nEntity sent:\n" + entity.getText());
    }
    else {
      request = new Request(Method.GET, reference);
      System.out.println("LinkedServiceData Tracing: " + Method.GET + " " + reference);
    }
    request.getClientInfo().getAcceptedMediaTypes().add(
        new Preference<MediaType>(new MediaType(HackystatConstants.MEDIA_TYPE_SPARQL_RESULTS)));
    ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, user,
        passw);
    request.setChallengeResponse(authentication);

    Response response = this.client.handle(request);
    Status status = response.getStatus();
    System.out.println("  => " + status.getCode() + " " + status.getDescription());
    if (status.getCode() == 200) {
      String resp = response.getEntity().getText();
      if (resp != null) {
        Document doc = LinkedServiceDataResource.deserializeDomDocument(resp);
        ret = LinkedServiceDataResource.sparqlResultToArray(doc);
      }
    }
    return ret;
  }

}

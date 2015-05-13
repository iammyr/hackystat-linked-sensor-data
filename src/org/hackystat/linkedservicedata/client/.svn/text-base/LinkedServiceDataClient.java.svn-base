package org.hackystat.linkedservicedata.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.server.ServerProperties;
import org.hackystat.linkedservicedata.sparql.IssueQuery;
import org.hackystat.linkedservicedata.sparql.ProjectQuery;
import org.hackystat.linkedservicedata.sparql.SparqlFilter;
import org.hackystat.linkedservicedata.sparql.UserQuery;
import org.hackystat.linkedservicedata.vocabularies.BaetleVocab;
import org.hackystat.linkedservicedata.vocabularies.CommontagVocab;
import org.hackystat.linkedservicedata.vocabularies.DoapVocab;
import org.hackystat.linkedservicedata.vocabularies.FoafVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.utilities.logger.HackystatLogger;
import org.hackystat.utilities.uricache.UriCache;
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
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Provides a client to support access to the LinkedServiceData service.
 *
 * @author Myriam Leggieri.
 */
public class LinkedServiceDataClient {
  /** The public sensorbase host used for this example. */
  private static final String sensorbaseHost = new org.hackystat.linkedservicedata.server.ServerProperties()
      .get(ServerProperties.SENSORBASE_FULLHOST_KEY);

  /** The public linkedservicedata host used for this example. */
  public static final String lisedHost = new org.hackystat.linkedservicedata.server.ServerProperties()
      .getFullHost();

  /** The user (from HackystatDemo). */
  private String username = "test@hackystat.org";

  /**
   * For password (same as the user for test users-those with a hackystat.org extension).
   */
  private String passwd = "test@hackystat.org";

  /** Indicates whether or not cache is enabled. */
  private boolean isCacheEnabled = true;

  /** An associated UriCache to improve responsiveness. */
  private UriCache uriLocalCache = null;

  /** The SensorBaseClient instance that is necessary. */
  private SensorBaseClient sbclient = null;

  /** The Restlet Client instance used to communicate with the server. */
  private Client client;

  /** The logger for linkedservicedata client information. */
  private Logger logger;

  /** The http authentication approach. */
  private ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;

  /** To facilitate debugging of problems using this system. */
  private boolean isTraceEnabled = true;

  /**
   * The System property key used to retrieve the default timeout value in milliseconds.
   */
  public static final String LINKEDSERVICEDATACLIENT_TIMEOUT_KEY = "linkedservicedataclient.timeout";

  /** Default RDF serialization language. */
  private String language = null;

  /** Default preferred media type while invoking the LiSeD service. */
  private MediaType media = null;

  /** The .hackystat subdirectory containing these cache instances. */
  private String subDir = "linkedservicedata/";

  /**
   * The number of hours that a cached LiSeD instance stays in the cache before being deleted.
   */
  private double maxLife = 1000;

  /** The total capacity of this cache. */
  private long capacity = 1000000;

  /**
   * Constructor of a client instance using default user, password and SensorBase host.
   *
   */
  public LinkedServiceDataClient() {
    new LinkedServiceDataClient(null, null, null, null, false, false);
    this.client = new Client(Protocol.HTTP);
  }

  /**
   *Constructor of a client instance using the specified user, password, serialization language,
   * media type, and listening on the specified host.
   *
   * @param host host.
   * @param user user to log.
   * @param passw user's password.
   * @param serializationLanguage RDF serialization language.
   * @param mediat preferred media type.
   * @param isTraceEnabled1 enables or not the LiSeD client tracing.
   * @param isCachedEnabled1 enables or not the LiSeD client local cache.
   */
  public LinkedServiceDataClient(final String user, final String passw,
      final String serializationLanguage, final MediaType mediat, final boolean isTraceEnabled1,
      final boolean isCachedEnabled1) {
    this.isTraceEnabled = isTraceEnabled1;
    this.isCacheEnabled = isCachedEnabled1;
    if (media != null) {
      this.media = mediat;
    }
    else {
      this.media = MediaType.TEXT_RDF_N3;
    }
    if (serializationLanguage != null) {
      this.setLanguage(serializationLanguage);
    }
    else {
      this.setLanguage(HackystatConstants.LANG_N3);
    }
    if (user != null) {
      this.username = user;
    }
    if (passwd != null) {
      this.passwd = passw;
    }
    this.logger = HackystatLogger.getLogger(
        "org.hackystat.linkedservicedata.client.LinkedServiceDataClient", "linkedservicedata",
        false);
    this.logger.info("Instantiating client for: " + LinkedServiceDataClient.lisedHost + " " + user);
    if (this.isTraceEnabled) {
      System.out.println("LinkedServiceDataClient Tracing: INITIALIZE " + "host='"
          + LinkedServiceDataClient.lisedHost + "', email='" + user + "', password='" + passwd
          + "'");
    }
    if (this.isCacheEnabled) {
      this.uriLocalCache = new UriCache(user, subDir, maxLife, capacity);
    }
    this.client = new Client(Protocol.HTTP);
    try {
      initSensorBaseClient();
    }
    catch (SensorBaseClientException e) {
      e.printStackTrace();
    }
    setTimeoutFromSystemProperty();
  }

  /**
   * Sets the timeout for this client if the system property linkedservicedata.timeout is set and if
   * it can be parsed to an integer.
   */
  private void setTimeoutFromSystemProperty() {
    String systemTimeout = System.getProperty(LINKEDSERVICEDATACLIENT_TIMEOUT_KEY);
    // if not set, then return immediately.
    if (systemTimeout == null) {
      return;
    }
    // systemTimeout has a value, so set it if we can.
    try {
      int timeout = Integer.parseInt(systemTimeout);
      setTimeout(timeout);
      this.logger.info("LinkedServiceDataClient " + "timeout set to: " + timeout + " milliseconds");
    }
    catch (Exception e) {
      this.logger.warning("linkedservicedataclient.timeout " + "has non integer value: "
          + systemTimeout);
    }
  }

  /**
   * Delete all entries from the local cache of this LinkedServiceDataClient instance. If this
   * LiSeDClient instance does not have caching enabled, then this method has no effect.
   */
  public final synchronized void clearLocalCache() {
    if (this.uriLocalCache != null) {
      this.uriLocalCache.clearAll();
    }
  }

  /**
   * Delete all entries from the local cache associated with the logged user and the specified URI.
   *
   * @param uri URI.
   */
  public final synchronized void clearLocalCache(final String uri) {
    if (this.uriLocalCache != null) {
      this.uriLocalCache.clearGroup(username + uri);
    }
  }

  /**
   * Clears the (front side) LiSeD cache associated with this user on the LinkedServiceData server
   * to which this LinkedServiceDataClient instance is connected.
   *
   * @return True if the command succeeded.
   * @throws LinkedServiceDataClientException If problems occur.
   */
  public final synchronized boolean clearServerCache() throws LinkedServiceDataClientException {
    Date startTime = new Date();
    String uri = "cache";
    Response response = makeRequest(LinkedServiceDataClient.lisedHost, Method.DELETE, uri, null,
        this.username, this.passwd);
    if (!response.getStatus().isSuccess()) {
      logElapsedTime(uri, startTime);
      throw new LinkedServiceDataClientException(response.getStatus());
    }
    logElapsedTime(uri, startTime);
    return true;
  }

  /**
   * Clears the (front side) LiSeD cache entries associated with the specified project and its owner
   * on the LinkedServiceData server to which this LinkedServiceDataClient instance is connected.
   *
   * @param owner The owner of the project whose entries are to be cleared.
   * @param project The project LiSeDs to be cleared on the server.
   * @return True if the command succeeded.
   * @throws LinkedServiceDataClientException If problems occur.
   */
  public final synchronized boolean clearServerCache(final String owner, final String project)
      throws LinkedServiceDataClientException {
    Date startTime = new Date();
    String uri = String.format("cache/%s/%s", owner, project);
    Response response = makeRequest(LinkedServiceDataClient.lisedHost, Method.DELETE, uri, null,
        this.username, this.passwd);
    if (!response.getStatus().isSuccess()) {
      logElapsedTime(uri, startTime);
      throw new LinkedServiceDataClientException(response.getStatus());
    }
    logElapsedTime(uri, startTime);
    return true;
  }

  /**
   * Clears the (front side) LiSeD cache entries associated with the Default project for the
   * specified user on the LinkedServiceData server to which this LinkedServiceDataClient instance
   * is connected.
   *
   * @param owner registered user.
   * @return True if the command succeeded.
   * @throws LinkedServiceDataClientException If problems occur.
   */
  public final synchronized boolean clearServerCache(final String email)
      throws LinkedServiceDataClientException {
    Date startTime = new Date();
    boolean ret = false;
    String uri = String.format("cache/%s", email);
    Response response = makeRequest(LinkedServiceDataClient.lisedHost, Method.DELETE, uri, null,
        this.username, this.passwd);
    logElapsedTime(uri, startTime);
    if (!response.getStatus().isSuccess()) {
      throw new LinkedServiceDataClientException(response.getStatus());
    }
    else {
      ret = true;
    }
    return ret;
  }

  /**
   * Clears the (front side) LiSeD cache entries identified as 'alternative' because not related to
   * any user or project on the LinkedServiceData server to which this LinkedServiceDataClient
   * instance is connected.
   *
   * @return True if the command succeeded.
   * @throws LinkedServiceDataClientException If problems occur.
   */
  public final synchronized boolean clearAlternativeServerCache()
      throws LinkedServiceDataClientException {
    Date startTime = new Date();
    String uri = String.format("cache/" + HackystatConstants.otherResourceID);
    Response response = makeRequest(LinkedServiceDataClient.lisedHost, Method.DELETE, uri, null,
        this.username, this.passwd);
    if (!response.getStatus().isSuccess()) {
      logElapsedTime(uri, startTime);
      throw new LinkedServiceDataClientException(response.getStatus());
    }
    logElapsedTime(uri, startTime);
    return true;
  }

  /**
   * Sets the timeout value for this client.
   *
   * @param milliseconds The number of milliseconds to wait before timing out.
   */
  public final synchronized void setTimeout(final int milliseconds) {
    client.getContext().getParameters().removeAll("connectTimeout");
    client.getContext().getParameters().add("connectTimeout", String.valueOf(milliseconds));
    // For the Apache Commons client.
    client.getContext().getParameters().removeAll("readTimeout");
    client.getContext().getParameters().add("readTimeout", String.valueOf(milliseconds));
    client.getContext().getParameters().removeAll("connectionManagerTimeout");
    client.getContext().getParameters().add("connectionManagerTimeout",
        String.valueOf(milliseconds));
  }

  /**
   * Authenticates this user and password with this LiSeD service, throwing a LiSeDClientException
   * if the user and password associated with this instance are not valid credentials. Note that
   * authentication is performed by checking these credentials with the underlying SensorBase; this
   * service does not keep its own independent set of user-names and passwords.
   *
   * @return LinkedServiceDataClient instance
   * @throws LinkedServiceDataClientException If authentication is not successful.
   */
  public final synchronized LinkedServiceDataClient authenticate()
      throws LinkedServiceDataClientException {
    // Performs authentication by invoking ping with
    // user and password as form params.
    String uri = "ping?user=" + this.username + "&password=" + this.passwd;
    Response response = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET, uri, null,
        this.username, this.passwd);
    if (!response.getStatus().isSuccess()) {
      throw new LinkedServiceDataClientException(response.getStatus());
    }
    String responseString;
    try {
      responseString = response.getEntity().getText();
    }
    catch (Exception e) {
      throw new LinkedServiceDataClientException("Bad response", e);
    }
    if (!"LinkedServiceData authenticated".equals(responseString)) {
      throw new LinkedServiceDataClientException("Authentication failed");
    }
    return this;
  }

  /**
   * Check if the specified user is registered with the specified password on the SensorBase.
   *
   * @param user user.
   * @param passw password.
   * @return true if he's registered, false otherwise.
   */
  public static final boolean isRegistered(String user, String passw) {
    return SensorBaseClient.isRegistered(sensorbaseHost, user, passw);
  }

  /**
   * Logs info to the logger about the elapsed time for this request.
   *
   * @param uri The URI requested.
   * @param startTime The startTime of the call.
   * @param e The exception thrown, or null if no exception.
   */
  private void logElapsedTime(final String uri, final Date startTime, final Exception e) {
    long millis = (new Date()).getTime() - startTime.getTime();
    String msg = millis + " millis: " + uri + ((e == null) ? "" : " " + e);
    this.logger.info(msg);
  }

  /**
   * Logs info to the logger about the elapsed time for this request.
   *
   * @param uri The URI requested.
   * @param startTime The startTime of the call.
   */
  private void logElapsedTime(final String uri, final Date startTime) {
    logElapsedTime(uri, startTime, null);
  }

  /**
   * Creates a string that contains all the prefixes needed by a Sparql query to be executed
   * correctly according to the specified filters that are going to be included within the query.
   *
   * @param filtersList list of Sparql filters.
   * @return the created string.
   */
  private String addSparqlPrefixes(final LinkedList<SparqlFilter> filtersList) {
    String query = "";
    if (filtersList != null) {
      for (SparqlFilter sf : filtersList) {
        if (!query.contains(sf.getPrefix())) {
          query += "PREFIX " + sf.getPrefix() + ": <" + sf.getPredicateNamespace() + ">\n";
        }
      }
      if (!filtersList.isEmpty()) {
        query += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
      }
    }
    return query;
  }

  /**
   * Creates a string that contains all the specified filters using the correct Sparql syntax.
   *
   * @param filtersList list of filters.
   * @return the created string
   */
  private String addSparqlFilters(final LinkedList<SparqlFilter> filtersList) {
    String query = "";
    int count = 0;
    Object val = null;
    if (filtersList != null) {
      for (SparqlFilter sf : filtersList) {
        query += "?uri " + sf.getPrefix() + ":" + sf.getPredicateLocalName() + " ?value" + count
            + " .\n";
        val = sf.getValue();
        if (val instanceof String) {
          query += "FILTER (xsd:string(?value" + count + ") " + sf.getOperator() + " " + val
              + ")\n";
        }
        else {
          query += "FILTER (xsd:double(?value" + count + ") " + sf.getOperator() + " " + val
              + ")\n";
        }
        count++;
      }
    }
    return query;
  }

  /**
   * Add the specified list of server URI/adminPassw to the list of external Hackystat LiSeD servers
   * included in the Hackystat network for the LiSeD server associated with this client.
   *
   * @param list list of pairs.
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public void addNetworkLiSeDServer(HashMap<String, String[]> list)
      throws LinkedServiceDataClientException {
    try {
      Response resp = makeRequest(LinkedServiceDataClient.lisedHost, Method.POST,
          HackystatConstants.NETWORK_RESOURCE_TYPE, LinkedServiceDataResource
              .getStringRepresentation(list, false), this.username, this.passwd);
      if (resp.getStatus().getCode() != 200 && resp.getStatus().getCode() != 201) {
        throw new LinkedServiceDataClientException(resp.getStatus().getCode() + " "
            + resp.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new LinkedServiceDataClientException(e.getMessage());
    }
  }

  /**
   * Return the list of external Hackystat LiSeD servers included in the Hackystat network for the
   * LiSeD server associated with this client.
   *
   * @return list
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public HashMap<String, String[]> getAssociatedLiSeDServers()
      throws LinkedServiceDataClientException {
    HashMap<String, String[]> ret = null;
    try {
      Response resp = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET,
          HackystatConstants.NETWORK_RESOURCE_TYPE, null, this.username, this.passwd);
      if (resp.getStatus().getCode() != 200) {
        throw new LinkedServiceDataClientException(resp.getStatus().getCode() + " "
            + resp.getStatus().getDescription());
      }
      else {
        String entityStr = resp.getEntity().getText();
        ret = LinkedServiceDataResource.makeServersList(entityStr);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new LinkedServiceDataClientException(e.getMessage());
    }
    return ret;
  }

  /**
   * Remove the specified server URI from the list of external Hackystat LiSeD servers included in
   * the Hackystat network for the LiSeD server associated with this client.
   *
   * @param uri
   * @throws LinkedServiceDataClientException
   */
  public void removeAssociatedLiSeDServers(String uri) throws LinkedServiceDataClientException {
    try {
      uri = java.net.URLEncoder.encode(uri, "UTF-8");
      Response resp = makeRequest(LinkedServiceDataClient.lisedHost, Method.DELETE,
          HackystatConstants.NETWORK_RESOURCE_TYPE + uri, null, this.username, this.passwd);
      if (resp.getStatus().getCode() != 200) {
        throw new LinkedServiceDataClientException(resp.getStatus().getCode() + " "
            + resp.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new LinkedServiceDataClientException(e.getMessage());
    }
  }

  /**
   * Add the SELECT Sparql query part for a Project SPARQL endpoint.
   *
   * @param pq object containing all the requested project features.
   * @return SELECT part of a Sparql query.
   */
  private String addProjectSelectQueryComponent(ProjectQuery pq) {
    String query = "SELECT ?uri\n WHERE {\n";
    if (pq.projectname != null) {
      query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.NAME.getLocalName() + " \""
          + pq.projectname + "\" .\n";
    }
    if (pq.owner != null) {
      query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.MAINTAINER.getLocalName() + " \""
          + LinkedServiceDataResource.getUserUri(pq.owner) + "\" .\n";
    }
    if (pq.description != null) {
      query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.DESCRIPTION.getLocalName() + " \""
          + pq.description + "\" .\n";
    }
    if (pq.start != null) {
      query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.CREATED.getLocalName() + " \""
          + pq.start + "\" .\n";
    }
    if (pq.end != null) {
      query += "?uri " + HackystatVocab.PREFIX + ":" + HackystatVocab.ENDED.getLocalName() + " \""
          + pq.end + "\" .\n";
    }
    if (pq.modified != null) {
      query += "?uri " + HackystatVocab.PREFIX + ":" + HackystatVocab.MODIFIED.getLocalName()
          + " \"" + pq.modified + "\" .\n";
    }
    if (pq.rep != null) {
      query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.REPOSITORY.getLocalName() + " \""
          + pq.rep + "\" .\n";
    }
    if (pq.repanon != null) {
      query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.ANON_ROOT.getLocalName() + " \""
          + pq.repanon + "\" .\n";
    }
    if (pq.repwebinterface != null) {
      query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.BROWSE.getLocalName() + " \""
          + pq.repwebinterface + "\" .\n";
    }
    if (pq.progrlangs != null) {
      for (int i = 0; i < pq.progrlangs.length; i++) {
        query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.LANGUAGE.getLocalName() + " \""
            + LinkedServiceDataResource.getProgrammingLanguageUri(pq.progrlangs[i]) + "\" .\n";
      }
    }
    if (pq.osystems != null) {
      for (int i = 0; i < pq.osystems.length; i++) {
        query += "?uri " + HackystatVocab.PREFIX + ":"
            + HackystatVocab.ON_OPERATING_SYSTEM.getLocalName() + " \""
            + LinkedServiceDataResource.getOperatingSystemUri(pq.osystems[i]) + "\" .\n";
      }
    }
    if (pq.bugdatabases != null) {
      for (int i = 0; i < pq.bugdatabases.length; i++) {
        query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.BUG_DATABASE.getLocalName() + " \""
            + pq.bugdatabases[i] + "\" .\n";
      }
    }
    if (pq.mirrors != null) {
      for (int i = 0; i < pq.mirrors.length; i++) {
        query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.DOWNLOAD_MIRROR.getLocalName()
            + " \"" + pq.mirrors[i] + "\" .\n";
      }
    }
    if (pq.wikis != null) {
      for (int i = 0; i < pq.wikis.length; i++) {
        query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.WIKI.getLocalName() + " \""
            + pq.wikis[i] + "\" .\n";
      }
    }
    if (pq.tags != null) {
      for (int i = 0; i < pq.tags.length; i++) {
        query += "?uri " + DoapVocab.PREFIX + ":" + DoapVocab.CATEGORY.getLocalName() + " \""
            + pq.tags[i] + "\" .\n";
      }
    }
    if (pq.tools != null) {
      for (int i = 0; i < pq.tools.length; i++) {
        query += "?uri " + HackystatVocab.PREFIX + ":" + HackystatVocab.USES_TOOL.getLocalName()
            + " \"" + LinkedServiceDataResource.getToolUri(pq.tools[i]) + "\" .\n";
      }
    }
    if (pq.invitations != null) {
      for (int i = 0; i < pq.invitations.length; i++) {
        query += "?uri " + HackystatVocab.PREFIX + ":" + HackystatVocab.INVITATED.getLocalName()
            + " \"" + LinkedServiceDataResource.getUserUri(pq.invitations[i]) + "\" .\n";
      }
    }
    if (pq.spectators != null) {
      for (int i = 0; i < pq.spectators.length; i++) {
        query += "?uri " + HackystatVocab.PREFIX + ":" + HackystatVocab.SPECTATOR.getLocalName()
            + " \"" + LinkedServiceDataResource.getUserUri(pq.spectators[i]) + "\" .\n";
      }
    }
    if (pq.members != null) {
      for (int i = 0; i < pq.members.length; i++) {
        query += "?uri " + HackystatVocab.PREFIX + ":" + HackystatVocab.MEMBER.getLocalName()
            + " \"" + LinkedServiceDataResource.getUserUri(pq.members[i]) + "\" .\n";
      }
    }
    if (pq.usersRoles != null) {
      String[] emails = null;
      for (OntProperty pred : pq.usersRoles.keySet()) {
        emails = pq.usersRoles.get(pred);
        for (int i = 0; i < emails.length; i++) {
          query += "?uri " + DoapVocab.PREFIX + ":" + pred.getLocalName() + " \""
              + LinkedServiceDataResource.getUserUri(emails[i]) + "\" .\n";
        }
      }
    }
    if (pq.seeAlso != null) {
      for (int i = 0; i < pq.seeAlso.length; i++) {
        query += "?uri rdfs:" + RDFS.seeAlso.getLocalName() + " \"" + pq.seeAlso[i] + "\" .\n";
      }
    }
    if (pq.sameAs != null) {
      for (int i = 0; i < pq.sameAs.length; i++) {
        query += "?uri owl:" + OWL.sameAs.getLocalName() + " \"" + pq.sameAs[i] + "\" .\n";
      }
    }
    query += addSparqlFilters(pq.filtersList);
    query += "}";
    return query;
  }

  /**
   * Search for project URIs satisfying the specified project features.
   *
   * @param pq object containing all the requested project features.
   * @return list of project URIs as a string.
   * @throws LinkedServiceDataClientException
   * @throws UnsupportedEncodingException
   */
  public String searchForProjects(ProjectQuery pq) throws LinkedServiceDataClientException,
      UnsupportedEncodingException {
    String ret = null;
    String query = "";
    if (pq.filtersList != null) {
      query += addSparqlPrefixes(pq.filtersList);
    }
    if (pq.projectname != null || pq.owner != null || pq.start != null || pq.description != null
        || pq.progrlangs != null || pq.bugdatabases != null || pq.mirrors != null
        || pq.repwebinterface != null || pq.rep != null || pq.repanon != null || pq.wikis != null
        || pq.tags != null || pq.usersRoles != null && !query.contains(DoapVocab.NS)) {
      query += "PREFIX " + DoapVocab.PREFIX + ": <" + DoapVocab.NS + ">\n";
    }
    if (pq.osystems != null || pq.tools != null || pq.invitations != null || pq.spectators != null
        || pq.members != null || pq.end != null || pq.modified != null
        && !query.contains(HackystatVocab.NS)) {
      query += "PREFIX " + HackystatVocab.PREFIX + ": <" + HackystatVocab.NS + ">\n";
    }
    if (pq.seeAlso != null) {
      query += "PREFIX rdfs: <" + RDFS.getURI() + ">\n";
    }
    if (pq.sameAs != null) {
      query += "PREFIX owl: <" + OWL.getURI() + ">\n";
    }
    query += addProjectSelectQueryComponent(pq);

    query = java.net.URLEncoder.encode(query, "UTF-8");
    String uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.PROJECT_SPARQL_RESOURCE_TYPE + query + media;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String searchResult = (String) this.uriLocalCache.getFromGroup(uriCache, this.username);
      if (searchResult != null)
        return searchResult;
    }
    // Otherwise get it from the LiSeD service.
    Response resp = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET,
        HackystatConstants.PROJECT_SPARQL_RESOURCE_TYPE + query, null, this.username, this.passwd);
    if (resp != null) {
      String respStr = null;
      try {
        respStr = resp.getEntity().getText();
      }
      catch (IOException e) {
        e.printStackTrace();
        throw new LinkedServiceDataClientException("Unable to "
            + "execute a Sparql query over Hackystat users. " + e.getMessage());
      }
      if (respStr != null) {
        ret = respStr;
        if (this.isCacheEnabled) {
          this.uriLocalCache.putInGroup(uriCache, this.username, ret);
        }
      }
    }

    return ret;
  }

  /**
   * Add the SELECT Sparql query part for a User SPARQL endpoint.
   *
   * @param uq object containing all the requested user features.
   * @return SELECT part of a Sparql query.
   */
  private String addUserSelectQueryComponent(UserQuery uq) {
    String query = "SELECT ?uri\n WHERE {\n";
    if (uq.name != null) {
      query += "?uri " + FoafVocab.PREFIX + ":" + FoafVocab.FIRST_NAME.getLocalName() + " \""
          + uq.name + "\" .\n";
    }
    if (uq.surname != null) {
      query += "?uri " + FoafVocab.PREFIX + ":" + FoafVocab.SURNAME.getLocalName() + " \""
          + uq.surname + "\" .\n";
    }
    if (uq.email != null) {
      query += "?uri " + FoafVocab.PREFIX + ":" + FoafVocab.MBOX.getLocalName() + " \"" + uq.email
          + "\" .\n";
    }
    if (uq.homepage != null) {
      query += "?uri " + FoafVocab.PREFIX + ":" + FoafVocab.HOMEPAGE.getLocalName() + " \""
          + uq.homepage + "\" .\n";
    }
    if (uq.blog != null) {
      query += "?uri " + FoafVocab.PREFIX + ":" + FoafVocab.WEBLOG.getLocalName() + " \"" + uq.blog
          + "\" .\n";
    }
    if (uq.nick != null) {
      query += "?uri " + FoafVocab.PREFIX + ":" + FoafVocab.NICK.getLocalName() + " \"" + uq.nick
          + "\" .\n";
    }
    if (uq.seeAlso != null) {
      for (int i = 0; i < uq.seeAlso.length; i++) {
        query += "?uri rdfs:" + RDFS.seeAlso.getLocalName() + " \"" + uq.seeAlso[i] + "\" .\n";
      }
    }
    if (uq.sameAs != null) {
      for (int i = 0; i < uq.sameAs.length; i++) {
        query += "?uri owl:" + OWL.sameAs.getLocalName() + " \"" + uq.sameAs[i] + "\" .\n";
      }
    }
    if (uq.filtersList != null) {
      query += addSparqlFilters(uq.filtersList);
    }
    query += "}";
    return query;

  }

  /**
   * Construct the Sparql query to perform a search on users using the specified parameters, that
   * will be passed while calling the LinkedServiceData's user Sparql end-point.
   *
   * @param uq object containing all the requested user features.
   * @return list of user URIs as a string.
   * @throws LinkedServiceDataClientException
   * @throws UnsupportedEncodingException
   */
  public final String searchForUsers(UserQuery uq) throws LinkedServiceDataClientException,
      UnsupportedEncodingException {
    String ret = null;
    String query = "";
    if (uq.filtersList != null) {
      query = addSparqlPrefixes(uq.filtersList);
    }
    if (uq.name != null || uq.nick != null || uq.surname != null || uq.homepage != null
        || uq.blog != null || uq.email != null && !query.contains(FoafVocab.NS)) {
      query += "PREFIX " + FoafVocab.PREFIX + ": <" + FoafVocab.NS + ">\n";
    }
    if (uq.seeAlso != null) {
      query += "PREFIX rdfs: <" + RDFS.getURI() + ">\n";
    }
    if (uq.sameAs != null) {
      query += "PREFIX owl: <" + OWL.getURI() + ">\n";
    }
    query += addUserSelectQueryComponent(uq);
    query = java.net.URLEncoder.encode(query, "UTF-8");
    String uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.USER_SPARQL_RESOURCE_TYPE + query + "/" + media;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String searchResult = (String) this.uriLocalCache.getFromGroup(uriCache, username);
      if (searchResult != null) {
        return searchResult;
      }
    }
    MediaType tmp = this.media;
    this.media = new MediaType(HackystatConstants.MEDIA_TYPE_SPARQL_RESULTS);
    // Otherwise get it from the LiSeD service.
    Response resp = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET,
        HackystatConstants.USER_SPARQL_RESOURCE_TYPE + query, null, this.username, this.passwd);
    if (resp != null && resp.getStatus().getCode() == 200) {
      String respStr = null;
      try {
        respStr = resp.getEntity().getText();
      }
      catch (IOException e) {
        e.printStackTrace();
        throw new LinkedServiceDataClientException("Unable to "
            + "execute a Sparql query over Hackystat users. " + e.getMessage());
      }
      finally {
        this.media = tmp;
      }
      if (respStr != null) {
        ret = respStr;
        if (this.isCacheEnabled) {
          this.uriLocalCache.putInGroup(uriCache, username, ret);
        }
      }
    }
    else {
      throw new LinkedServiceDataClientException(resp.getStatus().getCode() + " "
          + resp.getStatus().getDescription());
    }

    return ret;
  }

  /**
   * Does the housekeeping for making HTTP requests to the specified Hackystat service using the
   * specified method and asking for the specified media type. It's also possible to send an object
   * entity to host and to log on it using the specified user and password.
   *
   * @param host host.
   * @param method method.
   * @param requestString request.
   * @param entity object entity to send.
   * @param user user to log.
   * @param passw password.
   * @return The Response instance returned from the specified server.
   */
  public final Response makeRequest(final String host, final Method method,
      final String requestString, final Representation entity, final String user, final String passw) {
    Reference reference = new Reference(host + requestString);
    Request request = (entity == null) ? new Request(method, reference) : new Request(method,
        reference, entity);
    request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(this.media));
    ChallengeResponse authentication = new ChallengeResponse(scheme, user, passw);
    request.setChallengeResponse(authentication);
    if (isTraceEnabled) {
      System.out.println("LinkedServiceDataClient " + "Tracing: " + method + " " + reference);
      if (entity != null) {
        try {
          System.out.println(entity.getText());
        }
        catch (Exception e) {
          System.out.println("  Problems with " + "getText() on entity.");
        }
      }
    }
    Response response = client.handle(request);
    if (isTraceEnabled) {
      Status status = response.getStatus();
      System.out.println("  => " + status.getCode() + " " + status.getDescription());
    }
    return response;
  }

  /**
   * Returns true if the passed host is a LinkedServiceData host.
   *
   * @param host The URL of a LinkedServiceData host, "http://localhost:9875/linkedservicedata".
   * @return True if this URL responds as a LinkedServiceData host.
   */
  public static boolean isHost(final String host) {
    // All LinkedServiceData hosts use the HTTP protocol.
    if (!host.startsWith("http://")) {
      return false;
    }
    // Create the host/register URL.
    try {
      String registerUri = host.endsWith("/") ? host + "ping" : host + "/ping";
      Request request = new Request();
      request.setResourceRef(registerUri);
      request.setMethod(Method.GET);
      Client client = new Client(Protocol.HTTP);
      Response response = client.handle(request);
      String pingText = response.getEntity().getText();
      return (response.getStatus().isSuccess() && "LinkedServiceData".equals(pingText));
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns true if the public LinkedServiceData is available.
   *
   * @return True if the public LinkedServiceData can be contacted.
   */
  public final boolean checkHost() {
    return LinkedServiceDataClient.isHost(lisedHost);
  }

  /**
   * Returns true if our user and password are valid credentials for this host.
   *
   * @return True if the user and password are valid.
   */
  public final boolean checkCredentials() {
    return SensorBaseClient.isRegistered(sensorbaseHost, username, passwd);
  }

  /**
   * Return a RDF model which contains the logged user's profile.
   *
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public final Model getUserAsLinkedData() throws LinkedServiceDataClientException {
    return getUserAsLinkedData(this.username);
  }

  /**
   * Creates a model containing a list of URIS of all the locally registered users visible to the
   * logged one.
   *
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public final Model getUsersList() throws LinkedServiceDataClientException {
    Model mreturned = null;
    String uri = null, uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.USER_RESOURCE_TYPE + this.media;
    Date startTime = new Date();
    Response response = null;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String serializedModel = (String) this.uriLocalCache.getFromGroup(uriCache, username);
      if (serializedModel != null) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(serializedModel,
            HackystatConstants.RESOURCE_URI_BASE, getLanguage());
        return mreturned;
      }
    }
    // Otherwise get it from the LiSeD service.
    mreturned = ModelFactory.createDefaultModel();
    try {
      uri = HackystatConstants.USER_RESOURCE_TYPE;
      response = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET, uri, null,
          this.username, this.passwd);
      String responsetext = null;
      responsetext = response.getEntity().getText();
      if (responsetext != null && response.getStatus().getCode() == 200) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(responsetext,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        if (this.isCacheEnabled && mreturned != null) {
          String str = null;
          str = LinkedServiceDataResource.serializeRDFModel(mreturned,
              HackystatConstants.RESOURCE_URI_BASE,
              language.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE
                  : language);
          if (str != null) {
            this.uriLocalCache.putInGroup(uriCache, username, str);
          }
        }
      }
      else {
        throw new LinkedServiceDataClientException(response.getStatus().getCode() + " "
            + response.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      logElapsedTime(uri, startTime, e);
      e.printStackTrace();
      throw new LinkedServiceDataClientException(response.getStatus(), e);
    }

    return mreturned;
  }

  /**
   * Get RDF model representing the specified project URI.
   *
   * @param uriStr project URI.
   * @return RDF model.
   * @throws LinkedServiceDataClientException
   */
  public final Model getUriProjectAsLinkedData(String uriStr)
      throws LinkedServiceDataClientException {
    Model mreturned = null;
    if (uriStr.endsWith("/")) {
      uriStr = uriStr.substring(0, uriStr.length() - 1);
    }
    String tmp = uriStr.substring(0, uriStr.lastIndexOf("/")), projname = uriStr.substring(uriStr
        .lastIndexOf("/") + 1, uriStr.length()), owner = tmp.substring(tmp.lastIndexOf("/") + 1,
        tmp.length());
    mreturned = getProjectAsLinkedData(owner, projname);
    return mreturned;
  }

  /**
   * /** Get RDF model representing the specified user URI.
   *
   * @param uriStr user URI.
   * @return RDF model.
   * @throws LinkedServiceDataClientException
   */
  public final Model getUriUserAsLinkedData(String uriStr) throws LinkedServiceDataClientException {
    Model mreturned = null;
    if (uriStr.endsWith("/")) {
      uriStr = uriStr.substring(0, uriStr.length() - 1);
    }
    String user = uriStr.substring(uriStr.lastIndexOf("/") + 1, uriStr.length());
    mreturned = getUserAsLinkedData(user);
    return mreturned;
  }

  /**
   * Returns a RDF model which contains the specified user's profile.
   *
   * @param user user.
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public final Model getUserAsLinkedData(final String user) throws LinkedServiceDataClientException {
    Model mreturned = null;
    String uri = null, uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.USER_RESOURCE_TYPE + user + "/" + this.media;
    Date startTime = new Date();
    Response response = null;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String serializedModel = (String) this.uriLocalCache.getFromGroup(uriCache, user);
      if (serializedModel != null) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(serializedModel,
            HackystatConstants.RESOURCE_URI_BASE, getLanguage());
        return mreturned;
      }
    }
    // Otherwise get it from the LSD service.
    mreturned = ModelFactory.createDefaultModel();
    try {
      uri = HackystatConstants.USER_RESOURCE_TYPE + user;
      response = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET, uri, null,
          this.username, this.passwd);
      String responsetext = null;
      responsetext = response.getEntity().getText();
      if (responsetext != null && response.getStatus().getCode() == 200) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(responsetext,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        if (this.isCacheEnabled && mreturned != null) {
          String str = null;
          str = LinkedServiceDataResource.serializeRDFModel(mreturned,
              HackystatConstants.RESOURCE_URI_BASE,
              language.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE
                  : language);
          if (str != null) {
            this.uriLocalCache.putInGroup(uriCache, user, str);
          }
        }
      }
      else {
        throw new LinkedServiceDataClientException(response.getStatus().getCode() + " "
            + response.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      logElapsedTime(uri, startTime, e);
      e.printStackTrace();
      throw new LinkedServiceDataClientException(response.getStatus(), e);
    }

    return mreturned;
  }

  /**
   * Serialize a RDF model.
   *
   * @param model RDF model.
   * @return serialized RDF model.
   */
  public String serializeRDFModel(Model model, String rdflang) {
    return LinkedServiceDataResource.serializeRDFModel(model, HackystatConstants.RESOURCE_URI_BASE,
        rdflang.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE : rdflang);
  }

  /**
   * Deserialize a DOM document.
   *
   * @param document DOM document.
   * @return deserialized DOM document.
   */
  public Document deserializeDOMDocument(String document) {
    return LinkedServiceDataResource.deserializeDomDocument(document);
  }

  /**
   * Add changes and/or new data about a registered user.
   *
   * @param user user instance.
   * @throws LinkedServiceDataClientException if something goes wrong while trying to put in the
   * SensorBase this user instance.
   */
  public void addUserData(User user) throws LinkedServiceDataClientException {
    // if (!user.getEmail().equals(this.username)
    // || !user.getPassword().equals(
    // this.passwd)){
    // throw new LinkedServiceDataClientException(
    // "Operation not allowed: "
    // +"You're not the proprietary of data "
    // +"you're asking to modify.");
    // }

    try {
      if (sbclient == null) {
        initSensorBaseClient();
      }
      Response resp = makeRequest(sensorbaseHost, Method.POST, "users/" + user.getEmail(),
          LinkedServiceDataResource.getStringRepresentationForXml(LinkedServiceDataResource
              .makeUser(user.getProperties())), user.getEmail(), user.getPassword());
      if (resp.getStatus().getCode() != 200) {
        throw new LinkedServiceDataClientException(resp.getStatus().getCode() + " "
            + resp.getStatus().getDescription());
      }
      sbclient.clearCache();
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new LinkedServiceDataClientException(e.getMessage());
    }
  }

  /**
   * Add changes and/or new data about a stored issue.
   *
   * @param issue issue instance.
   * @throws LinkedServiceDataClientException if something goes wrong while trying to put in the
   * SensorBase this issue instance.
   */
  public void addIssueData(SensorData issue) throws LinkedServiceDataClientException {
    try {
      sbclient.putSensorData(issue);
    }
    catch (SensorBaseClientException e) {
      e.printStackTrace();
      throw new LinkedServiceDataClientException(e.getMessage());
    }
  }

  /**
   * Add changes and/or new data about a stored project.
   *
   * @param project project instance.
   * @throws LinkedServiceDataClientException if something goes wrong while trying to put in the
   * SensorBase this project instance.
   */
  public void addProjectData(Project project) throws LinkedServiceDataClientException {
    try {
      sbclient.putProject(project);
    }
    catch (SensorBaseClientException e) {
      e.printStackTrace();
      throw new LinkedServiceDataClientException(e.getMessage());
    }
  }

  /**
   * Returns a RDF model which contains the logged user's default project profile.
   *
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong
   */
  public final Model getProjectAsLinkedData() throws LinkedServiceDataClientException {
    return getProjectAsLinkedData(this.username, HackystatConstants.DEFAULT_PROJECT);
  }

  /**
   * Returns a RDF model which contains the specified user's project profile.
   *
   * @param user user.
   * @param project project.
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public final Model getProjectAsLinkedData(final String user, final String project)
      throws LinkedServiceDataClientException {
    Model mreturned = null;
    String uri = null, uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.PROJECT_RESOURCE_TYPE + user + "/" + project + "/" + this.media;
    Date startTime = new Date();
    Response response = null;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String serializedModel = (String) this.uriLocalCache.getFromGroup(uriCache, user);
      if (serializedModel != null) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(serializedModel,
            HackystatConstants.RESOURCE_URI_BASE, getLanguage());
        return mreturned;
      }
    }
    // Otherwise get it from the LSD service.
    mreturned = ModelFactory.createDefaultModel();
    try {
      uri = HackystatConstants.PROJECT_RESOURCE_TYPE + user + "/" + project;
      response = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET, uri, null,
          this.username, this.passwd);
      String responsetext = null;
      responsetext = response.getEntity().getText();
      if (responsetext != null && response.getStatus().getCode() == 200) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(responsetext,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        if (this.isCacheEnabled && mreturned != null) {
          // Add the created model to the cache
          String str = null;
          str = LinkedServiceDataResource.serializeRDFModel(mreturned,
              HackystatConstants.RESOURCE_URI_BASE,
              language.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE
                  : language);
          this.uriLocalCache.putInGroup(uriCache, user, str);
        }
      }
      else {
        throw new LinkedServiceDataClientException(response.getStatus().getCode() + " "
            + response.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      logElapsedTime(uri, startTime, e);
      e.printStackTrace();
      throw new LinkedServiceDataClientException(response.getStatus(), e);
    }

    return mreturned;
  }

  /**
   * Creates a model containing a list of URIS of all the locally stored projects.
   *
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public final Model getProjectsList() throws LinkedServiceDataClientException {
    Model mreturned = null;
    String uri = null, uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.PROJECT_RESOURCE_TYPE + this.media;
    Date startTime = new Date();
    Response response = null;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String serializedModel = (String) this.uriLocalCache.getFromGroup(uriCache, username);
      if (serializedModel != null) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(serializedModel,
            HackystatConstants.RESOURCE_URI_BASE, getLanguage());
        return mreturned;
      }
    }
    // Otherwise get it from the LSD service.
    mreturned = ModelFactory.createDefaultModel();
    try {
      uri = HackystatConstants.PROJECT_RESOURCE_TYPE;
      response = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET, uri, null,
          this.username, this.passwd);
      String responsetext = null;
      responsetext = response.getEntity().getText();
      if (responsetext != null && response.getStatus().getCode() == 200) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(responsetext,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        if (this.isCacheEnabled && mreturned != null) {
          String str = null;
          str = LinkedServiceDataResource.serializeRDFModel(mreturned,
              HackystatConstants.RESOURCE_URI_BASE,
              language.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE
                  : language);
          if (str != null) {
            this.uriLocalCache.putInGroup(uriCache, username, str);
          }
        }
      }
      else {
        throw new LinkedServiceDataClientException(response.getStatus().getCode() + " "
            + response.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      logElapsedTime(uri, startTime, e);
      e.printStackTrace();
      throw new LinkedServiceDataClientException(response.getStatus(), e);
    }

    return mreturned;
  }

  /**
   * Creates a model containing a list of URIS of all the locally stored projects which are
   * associated with the specified user.
   *
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public final Model getUserProjectsList() throws LinkedServiceDataClientException {
    Model mreturned = null;
    String uri = null, uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.PROJECT_RESOURCE_TYPE + username + "/" + this.media;
    Date startTime = new Date();
    Response response = null;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String serializedModel = (String) this.uriLocalCache.getFromGroup(uriCache, username);
      if (serializedModel != null) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(serializedModel,
            HackystatConstants.RESOURCE_URI_BASE, getLanguage());
        LinkedServiceDataResource.printModel(mreturned);
        return mreturned;
      }
    }
    // Otherwise get it from the LSD service.
    mreturned = ModelFactory.createDefaultModel();
    try {
      uri = HackystatConstants.PROJECT_RESOURCE_TYPE + username;
      response = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET, uri, null,
          this.username, this.passwd);
      String responsetext = null;
      responsetext = response.getEntity().getText();
      if (responsetext != null && response.getStatus().getCode() == 200) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(responsetext,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        if (this.isCacheEnabled && mreturned != null) {
          String str = null;
          str = LinkedServiceDataResource.serializeRDFModel(mreturned,
              HackystatConstants.RESOURCE_URI_BASE,
              language.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE
                  : language);
          if (str != null) {
            this.uriLocalCache.putInGroup(uriCache, username, str);
          }
        }
      }
      else {
        throw new LinkedServiceDataClientException(response.getStatus().getCode() + " "
            + response.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      logElapsedTime(uri, startTime, e);
      e.printStackTrace();
      throw new LinkedServiceDataClientException(response.getStatus(), e);
    }

    return mreturned;
  }

  /**
   * Get RDF model representing the specified issue URI.
   *
   * @param uriStr issue URI.
   * @return RDF model.
   * @throws LinkedServiceDataClientException
   */
  public final Model getUriIssueAsLinkedData(String uriStr) throws LinkedServiceDataClientException {
    Model mreturned = null;
    if (uriStr.endsWith("/")) {
      uriStr = uriStr.substring(0, uriStr.length() - 1);
    }
    String id = uriStr.substring(uriStr.lastIndexOf("/") + 1, uriStr.length());
    mreturned = getIssueAsLinkedData(id);
    return mreturned;
  }

  /**
   * Returns a RDF model which contains the specified issue's profile.
   *
   * @param id issue's ID.
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong.
   */
  public final Model getIssueAsLinkedData(final String id) throws LinkedServiceDataClientException {
    Model mreturned = null;
    String uri = null, uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.ISSUE_RESOURCE_TYPE + id + "/" + this.media;
    Date startTime = new Date();
    Response response = null;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String serializedModel = (String) this.uriLocalCache.getFromGroup(uriCache,
          HackystatConstants.otherResourceID);
      if (serializedModel != null) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(serializedModel,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        return mreturned;
      }
    }
    // Otherwise get it from the LSD service.
    mreturned = ModelFactory.createDefaultModel();
    try {
      uri = HackystatConstants.ISSUE_RESOURCE_TYPE + id;
      response = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET, uri, null,
          this.username, this.passwd);
      String responsetext = null;
      responsetext = response.getEntity().getText();
      if (responsetext != null && response.getStatus().getCode() == 200) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(responsetext,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        if (this.isCacheEnabled && mreturned != null) {
          String str = null;
          str = LinkedServiceDataResource.serializeRDFModel(mreturned,
              HackystatConstants.RESOURCE_URI_BASE,
              language.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE
                  : language);
          if (str != null) {
            this.uriLocalCache.putInGroup(uriCache, HackystatConstants.otherResourceID, str);
          }
        }
      }
      else {
        throw new LinkedServiceDataClientException(response.getStatus().getCode() + " "
            + response.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      logElapsedTime(uri, startTime, e);
      e.printStackTrace();
      throw new LinkedServiceDataClientException(response.getStatus(), e);
    }

    return mreturned;
  }

  /**
   * Creates a model containing a list of URIS of all the locally stored issues.
   *
   * @return RDF model.
   * @throws LinkedServiceDataClientException if something goes wrong
   */
  public final Model getIssuesList() throws LinkedServiceDataClientException {
    Model mreturned = null;
    String uri = null, uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.ISSUE_RESOURCE_TYPE + this.media;
    Date startTime = new Date();
    Response response = null;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String serializedModel = (String) this.uriLocalCache.getFromGroup(uriCache,
          HackystatConstants.otherResourceID);
      if (serializedModel != null) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(serializedModel,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        return mreturned;
      }
    }
    // Otherwise get it from the LSD service.
    mreturned = ModelFactory.createDefaultModel();
    try {
      uri = HackystatConstants.ISSUE_RESOURCE_TYPE;
      response = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET, uri, null,
          this.username, this.passwd);
      String responsetext = null;
      responsetext = response.getEntity().getText();
      if (responsetext != null && response.getStatus().getCode() == 200) {
        mreturned = LinkedServiceDataResource.deserializeRDFModel(responsetext,
            HackystatConstants.RESOURCE_URI_BASE, this.language);
        if (this.isCacheEnabled && mreturned != null) {
          String str = null;
          str = LinkedServiceDataResource.serializeRDFModel(mreturned,
              HackystatConstants.RESOURCE_URI_BASE,
              language.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE
                  : language);
          if (str != null) {
            this.uriLocalCache.putInGroup(uriCache, HackystatConstants.otherResourceID, str);
          }
        }
      }
      else {
        throw new LinkedServiceDataClientException(response.getStatus().getCode() + " "
            + response.getStatus().getDescription());
      }
    }
    catch (Exception e) {
      logElapsedTime(uri, startTime, e);
      e.printStackTrace();
      throw new LinkedServiceDataClientException(response.getStatus(), e);
    }

    return mreturned;
  }

  private String addIssueSelectQueryComponent(IssueQuery iq) {
    String query = "SELECT ?uri\n WHERE {\n";
    if (iq.id != null) {
      query += "?uri " + BaetleVocab.PREFIX + ":" + BaetleVocab.NAME.getLocalName() + " \"" + iq.id
          + "\" .\n";
    }
    if (iq.assignedTo != null) {
      query += "?uri " + BaetleVocab.PREFIX + ":" + BaetleVocab.ASSIGNED_TO.getLocalName() + " \""
          + LinkedServiceDataResource.getUserUri(iq.assignedTo) + "\" .\n";
    }
    if (iq.status != null) {
      query += "?uri " + BaetleVocab.PREFIX + ":" + BaetleVocab.STATUS.getLocalName() + " \""
          + iq.status + "\" .\n";
    }
    if (iq.priority != null) {
      query += "?uri " + BaetleVocab.PREFIX + ":" + BaetleVocab.PRIORITY.getLocalName() + " \""
          + iq.priority + "\" .\n";
    }
    if (iq.type != null) {
      String type = null;
      if (iq.type.equals("Defect")) {
        type = BaetleVocab.DEFECT.getURI();
      }
      else if (iq.type.equals("Enhancement")) {
        type = BaetleVocab.ENHANCEMENT.getURI();
      }
      else {
        type = BaetleVocab.ISSUE.getURI();
      }
      query += "?uri rdf:" + RDF.type.getLocalName() + " <" + type + "> .\n";
    }
    if (iq.milestone != null) {
      query += "?uri " + BaetleVocab.PREFIX + ":" + BaetleVocab.TARGET_MILESTONE.getLocalName()
          + " \"" + iq.milestone + "\" .\n";
    }
    if (iq.created != null) {
      query += "?uri " + BaetleVocab.PREFIX + "" + ":" + BaetleVocab.CREATED.getLocalName() + " \""
          + iq.created + "\" .\n";
    }
    if (iq.closed != null) {
      query += "?uri " + HackystatVocab.PREFIX + ":" + HackystatVocab.CLOSED_TIME.getLocalName()
          + " \"" + iq.closed + "\" .\n";
    }
    if (iq.lastmod != null) {
      query += "?uri " + HackystatVocab.PREFIX + ":" + HackystatVocab.MODIFIED_TIME.getLocalName()
          + " \"" + iq.lastmod + "\" .\n";
    }
    if (iq.tags != null) {
      for (int i = 0; i < iq.tags.length; i++) {
        query += "?uri " + CommontagVocab.PREFIX + ":" + CommontagVocab.TAGGED.getLocalName()
            + " \"" + iq.tags[i] + "\" .\n";
      }
    }
    if (iq.duplicates != null) {
      for (int i = 0; i < iq.duplicates.length; i++) {
        query += "OPTIONAL { ?uri owl:" + OWL.sameAs.getLocalName() + " \"" + iq.duplicates[i]
            + "\" } .\n";
      }
      for (int i = 0; i < iq.duplicates.length; i++) {
        query += "OPTIONAL { ?uri " + BaetleVocab.PREFIX + ":"
            + BaetleVocab.DUPLICATE.getLocalName() + " \"" + iq.duplicates[i] + "\" } .\n";
      }
    }
    if (iq.seeAlso != null) {
      for (int i = 0; i < iq.seeAlso.length; i++) {
        query += "?uri rdfs:" + RDFS.seeAlso.getLocalName() + " \"" + iq.seeAlso[i] + "\" .\n";
      }
    }
    if (iq.sameAs != null) {
      for (int i = 0; i < iq.sameAs.length; i++) {
        query += "?uri owl:" + OWL.sameAs.getLocalName() + " \"" + iq.sameAs[i] + "\" .\n";
      }
    }

    query += "}";
    return query;
  }

  /**
   * Construct the Sparql query to perform a search on issues using the specified parameters, that
   * will be passed while calling the LinkedServiceData's user Sparql end-point.
   *
   * @param iq object containing the requested issue features.
   * @throws LinkedServiceDataClientException if something goes wrong.
   * @throws UnsupportedEncodingException if the encoding scheme used to encode the Sparql query is
   * not supported.
   */
  public final String searchForIssues(IssueQuery iq) throws LinkedServiceDataClientException,
      UnsupportedEncodingException {
    String ret = null;
    String query = "";
    if (iq.id != null || iq.assignedTo != null || iq.status != null || iq.priority != null
        || iq.milestone != null || iq.created != null) {
      query += "PREFIX " + BaetleVocab.PREFIX + ": <" + BaetleVocab.NS + ">\n";
    }
    if (iq.tags != null) {
      query += "PREFIX " + CommontagVocab.PREFIX + ": <" + CommontagVocab.NS + ">\n";
    }
    if (iq.closed != null || iq.lastmod != null) {
      query += "PREFIX " + HackystatVocab.PREFIX + ": <" + HackystatVocab.NS + ">\n";
    }
    if (iq.duplicates != null || iq.sameAs != null) {
      query += "PREFIX owl: <" + OWL.getURI() + ">\n";
    }
    if (iq.seeAlso != null) {
      query += "PREFIX rdfs: <" + RDFS.getURI() + ">\n";
    }
    if (iq.type != null) {
      query += "PREFIX rdf: <" + RDF.getURI() + ">\n";
    }
    query += addIssueSelectQueryComponent(iq);
    query = java.net.URLEncoder.encode(query, "UTF-8");
    String uriCache = LinkedServiceDataClient.lisedHost
        + HackystatConstants.ISSUE_SPARQL_RESOURCE_TYPE + query + "/" + media;
    // Check the cache, and return the instance from it if available.
    if (this.isCacheEnabled) {
      String searchResult = (String) this.uriLocalCache.getFromGroup(uriCache,
          HackystatConstants.otherResourceID);
      if (searchResult != null) {
        return searchResult;
      }
    }
    MediaType tmp = this.media;
    this.media = new MediaType(HackystatConstants.MEDIA_TYPE_SPARQL_RESULTS);
    // Otherwise get it from the LiSeD service.
    Response resp = makeRequest(LinkedServiceDataClient.lisedHost, Method.GET,
        HackystatConstants.ISSUE_SPARQL_RESOURCE_TYPE + query, null, this.username, this.passwd);
    if (resp != null && resp.getStatus().getCode() == 200) {
      String respStr = null;
      try {
        respStr = resp.getEntity().getText();
      }
      catch (IOException e) {
        e.printStackTrace();
        throw new LinkedServiceDataClientException("Unable to "
            + "execute a Sparql query over Hackystat issues. " + e.getMessage());
      }
      finally {
        this.media = tmp;
      }
      if (respStr != null) {
        ret = respStr;
        if (this.isCacheEnabled) {
          this.uriLocalCache.putInGroup(uriCache, HackystatConstants.otherResourceID, ret);
        }
      }
    }
    else {
      throw new LinkedServiceDataClientException(resp.getStatus().getCode() + " "
          + resp.getStatus().getDescription());
    }

    return ret;
  }

  /**
   * Initialize the SensorBaseClient instance.
   *
   * @throws SensorBaseClientException if something goes wrong.
   */
  private void initSensorBaseClient() throws SensorBaseClientException {
    if (sbclient == null) {
      sbclient = new SensorBaseClient(sensorbaseHost, username, passwd);
      sbclient.authenticate();
    }
  }

  /**
   * Enables caching in this client.
   *
   * @param cacheName The name of the cache.
   * @param subDir1 The subdirectory in which the cache backend store is saved.
   * @param max The default expiration time for cached objects in days.
   * @param capacity1 The maximum number of instances to be held in-memory.
   */
  public final synchronized void enableCaching(final String cacheName, final String subDir1,
      final Double max, final Long capacity1) {
    this.uriLocalCache = new UriCache(cacheName, subDir1, max, capacity);
    this.isCacheEnabled = true;
  }

  /**
   * Set the preferred RDF serialization language.
   *
   * @param lang language.
   */
  public final void setLanguage(final String serializationLanguage) {
    this.language = serializationLanguage;
    if (serializationLanguage.equals(HackystatConstants.LANG_N3)) {
      this.media = MediaType.TEXT_RDF_N3;
    }
    else if (serializationLanguage.equals(HackystatConstants.LANG_NTRIPLE)) {
      this.media = new MediaType(HackystatConstants.MEDIA_TYPE_NTRIPLES);
    }
    else if (serializationLanguage.equals(HackystatConstants.LANG_RDFXML)
        || serializationLanguage.equals(HackystatConstants.LANG_RDFXML_ABBREV)) {
      this.media = MediaType.APPLICATION_RDF_XML;
    }
    else if (serializationLanguage.equals(HackystatConstants.LANG_TURTLE)) {
      this.media = new MediaType(HackystatConstants.MEDIA_TYPE_TURTLE);
    }
    System.out.println("The preferred RDF serialization " + "language is now "
        + serializationLanguage);
  }

  /**
   * Returns the preferred RDF serialization language.
   *
   * @return name of the preferred RDF serialization language.
   */
  public final String getLanguage() {
    return language;
  }
}

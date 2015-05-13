package org.hackystat.linkedservicedata.resource.linkedservicedata;

import static org.hackystat.linkedservicedata.server.Authenticator.AUTHENTICATOR_DPD_CLIENTS_KEY;
import static org.hackystat.linkedservicedata.server.Authenticator.AUTHENTICATOR_SENSORBASE_CLIENTS_KEY;
import static org.hackystat.linkedservicedata.server.Authenticator.AUTHENTICATOR_TELEMETRY_CLIENTS_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.DAILYPROJECTDATA_FULLHOST_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.SENSORBASE_FULLHOST_KEY;
import static org.hackystat.linkedservicedata.server.ServerProperties.TELEMETRY_FULLHOST_KEY;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.linkedservicedata.externalDatasets.HackystatLiSeDManager;
import org.hackystat.linkedservicedata.resource.network.LiSeDNetworkResource;
import org.hackystat.linkedservicedata.resource.user.LiSeDUserResource;
import org.hackystat.linkedservicedata.server.ServerProperties;
import org.hackystat.linkedservicedata.vocabularies.BaetleVocab;
import org.hackystat.linkedservicedata.vocabularies.CommontagVocab;
import org.hackystat.linkedservicedata.vocabularies.DoapVocab;
import org.hackystat.linkedservicedata.vocabularies.FoafVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.linkedservicedata.vocabularies.Helios_btVocab;
import org.hackystat.linkedservicedata.vocabularies.IswcVocab;
import org.hackystat.linkedservicedata.vocabularies.ProcessVocab;
import org.hackystat.linkedservicedata.vocabularies.SecVocab;
import org.hackystat.linkedservicedata.vocabularies.SiocVocab;
import org.hackystat.linkedservicedata.vocabularies.VoIDVocab;
import org.hackystat.linkedservicedata.vocabularies.VomVocab;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.Properties;
import org.hackystat.sensorbase.resource.projects.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.client.TelemetryClientException;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryChartData;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryPoint;
import org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream;
import org.hackystat.utilities.tstamp.Tstamp;
import org.restlet.Context;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * An abstract superclass for all LinkedServiceData resources that supplies common initialization
 * processing. This includes:
 * <ul>
 * <li>Extracting the authenticated user identifier (when authentication available)
 * <li>Extracting the URI elements and parameters.
 * <li>Declares all the supported representational variant.
 * </ul>
 *
 * @author Myriam Leggieri.
 *
 */
public abstract class LinkedServiceDataResource extends Resource {

  /** To be retrieved from the URL as the 'email' template parameter, or null. */
  protected String uriUser = null;

  /** To be retrieved from the URL as the 'project' template parameter, or null. */
  protected String projectName = null;

  /** To be retrieved from the URL as the 'sensordatatype' template parameter, or null. */
  protected String sensordatatypename = null;

  // ------------------------for requests to be forwarded to the dpd service------------
  /** To be retrieved from the URL as the 'timestamp' template parameter, or null. */
  protected String timestamp = null;

  protected String uriString = null;

  // ----------for requests to be forwarded to the telemetry service--------------------
  /** To be retrieved from the URL as the 'chart' template parameter, or null. */
  protected String chart = null;

  /** To be retrieved from the URL as the 'granularity' template parameter, or null. */
  protected String granularity = null;

  /** To be retrieved from the URL as the 'start' template parameter, or null. */
  protected String start = null;

  /** To be retrieved from the URL as the 'end' template parameter, or null. */
  protected String end = null;

  /** To be retrieved from the URL as the 'params' template parameter, or null. */
  protected String params = null;

  // ----------------------------------------------------------------------------

  /** The authenticated user, retrieved from the ChallengeResponse, or null. */
  protected String authUser = null;

  /** This server (linkedservicedata). */
  protected org.hackystat.linkedservicedata.server.Server lsdServer;

  /** The sensorbase host (for authentication). */
  protected String sensorBaseHost;

  /** The dailyprojectdata host (for analysis). */
  protected String dpdHost;

  /** The telemtry host (for analysis). */
  protected String telemetryHost;

  /** The standard error message returned from invalid authentication. */
  protected String responseMsg = "Permission denied.";

  /** Records the time at which each HTTP request was initiated. */
  protected long requestStartTime = new Date().getTime();

  protected boolean additionalCachedInfo = false;

  protected String fullPath = null;

  protected String queryStr = null;

  protected boolean queryPropagation = false;

  protected String entityFound = null;

  /**
   * I take a xml element and the tag name, look for the tag and get the text content i.e for
   * <employee><name>John</name></employee> xml snippet if the Element points to employee node and
   * tagName is 'name' I will return John
   */
  private static String getTextValue(Element ele, String tagName) {
    String textVal = null;
    NodeList nl = ele.getElementsByTagName(tagName);
    if (nl != null && nl.getLength() > 0) {
      Element el = (Element) nl.item(0);
      textVal = el.getFirstChild().getNodeValue();
    }

    return textVal;
  }

  /**
   *
   * @param doc
   * @return
   */
  public static String[] sparqlResultToArray(Document doc) {
    String[] ret = null;
    String bindingStr = "binding";
    NodeList bindingElems = doc.getElementsByTagName(bindingStr);
    Element bindingElem = null;
    ret = new String[bindingElems.getLength()];
    // Node child = null;
    for (int i = 0; i < bindingElems.getLength(); i++) {
      bindingElem = (Element) bindingElems.item(i);
      // child=bindingElem.getFirstChild();
      String text = getTextValue(bindingElem, "uri");
      if (text == null) {
        text = getTextValue(bindingElem, "literal");
      }
      if (text != null) {
        ret[i] = bindingElem.getAttribute("name") + HackystatConstants.SEPARATOR1_ID + text;
      }
    }
    return ret;
  }

  /**
   * Provides the following representational variants: TEXT_RDF_N3.
   *
   * @param context The context.
   * @param request The request object.
   * @param response The response object.
   */
  public LinkedServiceDataResource(Context context, Request request, Response response) {
    super(context, request, response);
    if (request.getChallengeResponse() != null) {
      this.authUser = request.getChallengeResponse().getIdentifier();
    }
    this.lsdServer = (org.hackystat.linkedservicedata.server.Server) getContext().getAttributes()
        .get("LinkedServiceDataServer");
    org.hackystat.linkedservicedata.server.ServerProperties properties = this.lsdServer
        .getServerProperties();
    this.dpdHost = properties.get(DAILYPROJECTDATA_FULLHOST_KEY);
    this.telemetryHost = properties.get(TELEMETRY_FULLHOST_KEY);
    this.sensorBaseHost = properties.get(SENSORBASE_FULLHOST_KEY);
    this.chart = (String) request.getAttributes().get("chart");
    this.uriUser = (String) request.getAttributes().get("user");
    this.projectName = (String) request.getAttributes().get("projectname");
    this.fullPath = ((String) request.getAttributes().get("fullPath"));
    this.granularity = (String) request.getAttributes().get("granularity");
    this.start = (String) request.getAttributes().get("start");
    this.end = (String) request.getAttributes().get("end");
    this.params = (String) request.getAttributes().get("params");
    this.timestamp = (String) request.getAttributes().get("timestamp");
    this.uriString = this.getRequest().getResourceRef().toString();
    this.sensordatatypename = (String) request.getAttributes().get("sensordatatypename");

    /**
     * I detect the query string (queryStr) in the following unusual way just because it used to not
     * be included into request's attributes when it was too long (and it's easy to get a long query
     * to process).
     */
    String urlOrig = request.getOriginalRef().toString(), searchedStr = "sparql?query=";
    if (urlOrig.contains(searchedStr)) {
      queryStr = urlOrig.substring(urlOrig.indexOf("sparql?query=") + searchedStr.length(), urlOrig
          .length());
    }
    if (queryStr != null) {
      try {
        queryStr = java.net.URLDecoder.decode(queryStr, "UTF-8");
      }
      catch (UnsupportedEncodingException e) {
        setStatusError("Error creating LiSeD. "
            + "The URI has been encoded using an unsupported encoding scheme.", e);
        return;
      }
    }
    if (projectName == null) {
      projectName = HackystatConstants.DEFAULT_PROJECT;
    }
    if (uriUser == null) {
      uriUser = authUser;
    }
    if (fullPath != null) {
      fullPath = fullPath.replaceAll(HackystatConstants.SEPARATOR2_ID, "/");
    }
    getVariants().clear(); // copied from BookmarksResource.java, not sure why needed.
    getVariants().add(new Variant(MediaType.TEXT_RDF_N3));
    getVariants().add(new Variant(MediaType.TEXT_XML));
    getVariants().add(new Variant(MediaType.TEXT_ALL));
    getVariants().add(new Variant(MediaType.APPLICATION_RDF_XML));
    getVariants().add(new Variant(MediaType.APPLICATION_ALL));
    getVariants().add(new Variant(new MediaType(HackystatConstants.MEDIA_TYPE_NTRIPLES)));
    getVariants().add(new Variant(new MediaType(HackystatConstants.MEDIA_TYPE_TURTLE)));
    getVariants().add(new Variant(new MediaType(HackystatConstants.MEDIA_TYPE_SPARQL_RESULTS)));

    // [1]ensure there won't be a loop of calls between this server and
    // the calling one
    String callingHostUri = request.getHostRef().getHostIdentifier() + "/linkedservicedata/";

    HackystatConstants.noLoop_toPropagate = new LinkedList<String>();
    HackystatConstants.noLoop_toPropagate.add(callingHostUri);
    // [2]ensure there won't be a loop of calls between the servers called
    // by this one and the servers that have already been called
    // by the calling server and the calling server of the calling server
    // and so on.
    // [2.1]if the calling server has sent the list of servers that have already
    // been called, remove their availability from the local servers list
    // At the same time update the list that will be propagated by this
    // server with servers already called before
    Representation representation = request.getEntity();
    if (representation != null) {
      try {
        String entity = representation.getText();
        if (!entity.trim().equals("")
            && !urlOrig.contains(HackystatConstants.NETWORK_RESOURCE_TYPE)) {
          queryPropagation = true;
          String[] servers = entity.split(HackystatConstants.SEPARATOR1_ID);
          for (int i = 0; i < servers.length; i++) {
            if (!HackystatConstants.noLoop_toPropagate.contains(servers[i])) {
              HackystatConstants.noLoop_toPropagate.add(servers[i]);
            }
          }
        }
        else if (urlOrig.contains(HackystatConstants.NETWORK_RESOURCE_TYPE)) {
          this.entityFound = entity;
        }
      }
      catch (Exception e) {
        System.out.println("  Problems with getText() on entity.");
      }
    }
    try {
      // DEBUG start--------------------------
      if (HackystatConstants.hackystatLisedServers_list.isEmpty()) {
        LiSeDNetworkResource.addExternalHackystatLiSeDServer(
            "http://localhost:9875/linkedservicedata", "myrpandemon@yahoo.it", "polpetta");
      }
      // end--------------------------
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   *
   * @param rdfData
   * @param endpoint
   * @return
   * @throws UnsupportedEncodingException
   */
  @SuppressWarnings("unchecked")
  protected Document performSearchLocallyAndPropagate(Model rdfData, String endpoint)
      throws UnsupportedEncodingException {
    LinkedServiceDataResource.printModel(rdfData);
    Object[] resp = getResourcesMatchingSparqlQuery(queryStr, rdfData);
    Document doc = (Document) resp[0];
    LinkedList<String> values = (LinkedList<String>) resp[1];

    /**
     * [1]Propagate the search over the associated Hackystat servers and over Ohloh (only if this
     * server has not been invoked as a result of a previous propagation, because in this case
     * another Hackystat server has already performed this search over Ohloh)
     */

    /**
     * [1.1]propagate this search over the associated Hackystat servers
     */
    HackystatLiSeDManager hackman = new HackystatLiSeDManager();
    String[] externalHackystatProjects = hackman.getSearchResults(queryStr, false, endpoint);
    System.out.println("");
    if (externalHackystatProjects != null) {
      for (int i = 0; i < externalHackystatProjects.length; i++) {
        externalHackystatProjects[i] = externalHackystatProjects[i]
            .split(HackystatConstants.SEPARATOR1_ID)[1];
      }
    }
    if (externalHackystatProjects != null) {
      boolean newUrisDiscovered = false;
      for (int i = 0; i < externalHackystatProjects.length; i++) {
        if (!values.contains(externalHackystatProjects[i])) {
          newUrisDiscovered = true;
        }
      }
      if (newUrisDiscovered) {
        doc = addSparqlResultsToDomDocument(doc, externalHackystatProjects);
      }
    }
    return doc;
  }

  /**
   * Creates and returns a new Restlet StringRepresentation built from rdfData. The rdfData will be
   * prefixed with a processing instruction indicating UTF-8 and version 1.0.
   *
   * @param rdfData The rdf data as a string.
   * @return A StringRepresentation of that rdfdata.
   */
  public static StringRepresentation getStringRepresentationFromRdf(String rdfData, MediaType media) {
    return new StringRepresentation(rdfData, media, Language.ALL, CharacterSet.UTF_8);
  }

  /**
   * Creates and returns a new Restlet StringRepresentation built from a list of strings.
   *
   * @param data LinkedList<String>.
   * @return A StringRepresentation of that LinkedList<String>.
   */
  public static StringRepresentation getStringRepresentation(LinkedList<String> data) {
    StringBuilder builder = new StringBuilder(500);
    for (String str : data) {
      builder.append(str + HackystatConstants.SEPARATOR1_ID);
    }
    return new StringRepresentation(builder, MediaType.TEXT_PLAIN, Language.ALL, CharacterSet.UTF_8);
  }

  /**
   * Creates and returns a new Restlet StringRepresentation built from a list of key-value pairs as
   * string-array of strings
   *
   * @param data LinkedList<String>.
   * @param hideTheLastValue hide or not the last element of each map value (which is an array of
   * strings).
   * @return A StringRepresentation of that LinkedList<String>.
   * @throws UnsupportedEncodingException
   */
  public static StringRepresentation getStringRepresentation(HashMap<String, String[]> data,
      boolean hideTheLastValue) throws UnsupportedEncodingException {
    StringBuilder builder = new StringBuilder(500);
    for (String str : data.keySet()) {
      builder.append(java.net.URLEncoder.encode(str, "UTF-8") + ",");
      for (int i = 0; i < data.get(str).length; i++) {
        if (hideTheLastValue && (i + 1) == data.get(str).length) {
          builder.append("****" + HackystatConstants.SEPARATOR2_ID);
        }
        else {
          builder.append(data.get(str)[i] + HackystatConstants.SEPARATOR2_ID);
        }
      }
      builder.append(HackystatConstants.SEPARATOR1_ID);
    }
    return new StringRepresentation(builder, MediaType.TEXT_PLAIN, Language.ALL, CharacterSet.UTF_8);
  }

  /**
   * Add a list of servers uri-[admin,password] pairs to the list of servers received by the REST
   * Api as a string entity.
   *
   * @param entity string representation of the list.
   * @return A StringRepresentation of that LinkedList<String>.
   * @throws IOException
   */
  public static void addServersList(String entity) throws IOException {
    String[] split = entity.split(HackystatConstants.SEPARATOR1_ID), subsplit1 = null, subsplit2 = null;
    for (int i = 0; i < split.length; i++) {
      subsplit1 = split[i].split(",");
      if (subsplit1.length == 2) {
        subsplit2 = subsplit1[1].split(HackystatConstants.SEPARATOR2_ID);
        if (subsplit2.length == 2) {
          LiSeDNetworkResource.addExternalHackystatLiSeDServer(java.net.URLDecoder.decode(
              subsplit1[0], "UTF-8"), subsplit2[0], subsplit2[1]);
        }
      }
    }
  }

  /**
   * Make a list of servers uri-[admin,password] pairs from a string representation of it.
   *
   * @param entity string representation of the list.
   * @return The map represented in the given string.
   * @throws IOException
   */
  public static HashMap<String, String[]> makeServersList(String entity) throws IOException {
    HashMap<String, String[]> ret = new HashMap<String, String[]>();
    String[] split = entity.split(HackystatConstants.SEPARATOR1_ID), subsplit1 = null, subsplit2 = null;
    for (int i = 0; i < split.length; i++) {
      subsplit1 = split[i].split(",");
      if (subsplit1.length == 2) {
        subsplit2 = subsplit1[1].split(HackystatConstants.SEPARATOR2_ID);
        if (subsplit2.length == 2) {
          ret.put(java.net.URLDecoder.decode(subsplit1[0], "UTF-8"), new String[] { subsplit2[0],
              subsplit2[1] });
        }
      }
    }
    return ret;
  }

  /**
   * Extract predicates from a Sparql query.
   *
   * @return
   * @throws Exception
   */
  protected LinkedList<String> extractPredicatesFromQuery() throws Exception {
    // to enhance performance the model will contain only those predicates
    // useful to determine whether a resource match the query
    String where = "{", queryDecoded = java.net.URLDecoder.decode(queryStr, "UTF-8");
    LinkedList<String> predicates = new LinkedList<String>();
    int ind = queryDecoded.indexOf(where), ind1 = queryDecoded.indexOf("}");
    if (ind != -1 && ind1 != -1) {
      String wheresub = queryDecoded.substring(ind + where.length(), ind1);
      if (wheresub.trim().equals("")) {
        return predicates;
      }
      if (!wheresub.contains("(") && !wheresub.contains(")") && wheresub.contains("FILTER")) {
        throw new Exception("Wrong syntax: you have to insert "
            + "the filter's argument in brackets");
      }
      String[] spaces = null, pred = null;
      LinkedList<String> filters = new LinkedList<String>();
      String tmp = "", curr = null, fullstop = ".", space = " ";
      StringTokenizer tokenizer = new StringTokenizer(wheresub);
      boolean filterTokens = false, bracket = false;
      while (tokenizer.hasMoreTokens()) {
        curr = tokenizer.nextToken();
        if (curr.equals("FILTER")) {
          // not consider the next chars contained within brackets
          filterTokens = true;
        }
        if (!filterTokens && !curr.equals(fullstop)) {
          if (!tmp.trim().equals("")) {
            tmp += space;
          }
          tmp += curr;
        }
        else if (!filterTokens) {
          filters.add(tmp);
          tmp = "";
        }
        else if (filterTokens && curr.endsWith(")")) {
          bracket = true;
        }
        else if (filterTokens && bracket && (curr.equals(fullstop) || curr.startsWith("?"))) {
          filterTokens = false;
          bracket = false;
          tmp += curr;
        }
      }
      filters.add(tmp);
      for (String filter : filters) {
        spaces = filter.split(" ");
        if (spaces.length == 3) {
          pred = spaces[1].split(":");
          if (pred.length == 2) {
            predicates.add(pred[1]);
          }
        }
      }
    }
    return predicates;
  }

  /**
   *
   * @param p
   * @param start
   * @param end
   * @param client
   * @param chartname
   * @param beforeResultParam
   * @param afterResultParam
   * @param succResult
   * @param failResult
   * @param gran
   * @return
   * @throws TelemetryClientException
   */
  protected static HashMap<XMLGregorianCalendar, Double> getSuccFailRatiosPerTimePeriod(Project p,
      XMLGregorianCalendar start, XMLGregorianCalendar end, TelemetryClient client,
      String chartname, String beforeResultParam, String afterResultParam, String succResult,
      String failResult, String gran) throws TelemetryClientException {
    HashMap<XMLGregorianCalendar, Double> ret = new HashMap<XMLGregorianCalendar, Double>();
    HashMap<XMLGregorianCalendar, Integer> successes = getIntegerTimeValuesPerTimePeriod(client,
        start, end, p.getOwner(), p.getName(), chartname, beforeResultParam + succResult
            + afterResultParam, gran);
    HashMap<XMLGregorianCalendar, Integer> failures = getIntegerTimeValuesPerTimePeriod(client,
        start, end, p.getOwner(), p.getName(), chartname, beforeResultParam + failResult
            + afterResultParam, gran);
    Iterator<XMLGregorianCalendar> itsucc = null, itfail = null;
    itfail = failures.keySet().iterator();
    double ratio = 0.0;
    XMLGregorianCalendar timesucc = null, timefail = null;
    while (itfail.hasNext()) {
      timefail = itfail.next();
      ratio = 0.0;
      if (successes.containsKey(timefail)) {
        if (failures.get(timefail) != 0) {
          ratio = successes.get(timefail) / failures.get(timefail);
        }
        else {
          ratio = successes.get(timefail) / 1;
        }
      }
      else {
        if (failures.get(timefail) != 0) {
          ratio = 1 / failures.get(timefail);
        }
      }
      if (ratio != 0.0) {
        ret.put(timefail, ratio);
      }
    }
    itsucc = successes.keySet().iterator();
    while (itsucc.hasNext()) {
      timesucc = itsucc.next();
      if (!ret.containsKey(timesucc)) {
        ratio = 0.0;
        if (failures.containsKey(timesucc)) {
          if (failures.get(timesucc) != 0) {
            ratio = successes.get(timesucc) / failures.get(timesucc);
          }
          else {
            ratio = successes.get(timesucc) / 1;
          }
        }
        else {
          ratio = successes.get(timesucc) / 1;
        }
        if (ratio != 0.0) {
          ret.put(timesucc, ratio);
        }
      }
    }
    return ret;
  }

  /**
   *
   * @param client
   * @param start
   * @param end
   * @param owner
   * @param project
   * @param chartname
   * @param params
   * @param gran
   * @return
   * @throws TelemetryClientException
   */
  protected static HashMap<XMLGregorianCalendar, Double> getDoubleTimeValuesPerTimePeriod(
      TelemetryClient client, XMLGregorianCalendar start, XMLGregorianCalendar end, String owner,
      String project, String chartname, String params, String gran) throws TelemetryClientException {
    HashMap<XMLGregorianCalendar, Double> ret = new HashMap<XMLGregorianCalendar, Double>();
    TelemetryChartData tmtchartdata = null;
    tmtchartdata = client.getChart(chartname, owner, project, gran, start, end, params);
    if (tmtchartdata != null) {
      List<org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream> tstreamlist = tmtchartdata
          .getTelemetryStream();
      List<TelemetryPoint> tpointlist = null;
      XMLGregorianCalendar time = null;
      String value = null;
      if (tstreamlist != null) {
        Iterator<TelemetryStream> itstream = tstreamlist.iterator();
        TelemetryStream stream = null;
        while (itstream.hasNext()) {
          stream = itstream.next();
          tpointlist = stream.getTelemetryPoint();
          if (tpointlist != null) {
            for (TelemetryPoint point : tpointlist) {
              value = point.getValue();
              if (value != null) {
                time = point.getTime();
                if (time != null) {
                  ret.put(time, Double.parseDouble(value));
                }
              }
            }
          }
        }
      }

    }
    return ret;
  }

  /**
   *
   * @param client
   * @param start
   * @param end
   * @param owner
   * @param project
   * @param chartname
   * @param params
   * @param timePeriod
   * @return
   * @throws TelemetryClientException
   */
  protected static HashMap<XMLGregorianCalendar, Integer> getIntegerTimeValuesPerTimePeriod(
      TelemetryClient client, XMLGregorianCalendar start, XMLGregorianCalendar end, String owner,
      String project, String chartname, String params, String timePeriod)
      throws TelemetryClientException {
    HashMap<XMLGregorianCalendar, Integer> ret = new HashMap<XMLGregorianCalendar, Integer>();
    TelemetryChartData tmtchartdata = null;
    tmtchartdata = client.getChart(chartname, owner, project, timePeriod, start, end, params);
    if (tmtchartdata != null) {
      List<org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream> tstreamlist = tmtchartdata
          .getTelemetryStream();
      List<TelemetryPoint> tpointlist = null;
      XMLGregorianCalendar time = null;
      String value = null;
      if (tstreamlist != null) {
        Iterator<TelemetryStream> itstream = tstreamlist.iterator();
        TelemetryStream stream = null;
        while (itstream.hasNext()) {
          stream = itstream.next();
          tpointlist = stream.getTelemetryPoint();
          if (tpointlist != null) {
            for (TelemetryPoint point : tpointlist) {
              value = point.getValue();
              if (value != null) {
                time = point.getTime();
                if (time != null) {
                  ret.put(time, Integer.parseInt(value));
                }
              }
            }
          }
        }
      }

    }
    return ret;
  }

  /**
   *
   * @param client
   * @param chartName
   * @param params
   * @param startEndsOfAccessibleProjects
   * @return
   * @throws TelemetryClientException
   */
  protected double telemetryValuesAverage(TelemetryClient client, String chartName, String params,
      LinkedList<XMLGregorianCalendar[]> startEndsOfAccessibleProjects)
      throws TelemetryClientException {
    TelemetryChartData tmtchartdata = null;
    List<org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream> tmtstreamlist = null;
    List<TelemetryPoint> tmtpoint = null;
    double tot = 0.0, ret = 0.0;
    int count = 0;
    for (XMLGregorianCalendar[] arr : startEndsOfAccessibleProjects) {
      // calculate the average development time, waiting for other measures to come
      tmtchartdata = client.getChart(chartName, uriUser, projectName, HackystatConstants.MONTH,
          arr[0], arr[1], params);
      tmtstreamlist = tmtchartdata.getTelemetryStream();
      if (tmtstreamlist != null) {
        for (org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream tmtstream : tmtstreamlist) {
          tmtpoint = tmtstream.getTelemetryPoint();
          if (tmtpoint != null) {
            for (TelemetryPoint point : tmtpoint) {
              if (point.getValue() != null) {
                tot += Double.parseDouble(point.getValue());
                count++;
              }
            }
          }
        }
      }
    }
    if (count != 0) {
      ret = tot / count;
    }
    return ret;
  }

  /**
   *
   * @param client
   * @param chartName
   * @param params
   * @param startEndsOfAccessibleProjects
   * @return
   * @throws TelemetryClientException
   */
  protected int telemetryValuesCount(TelemetryClient client, String chartName, String params,
      LinkedList<XMLGregorianCalendar[]> startEndsOfAccessibleProjects)
      throws TelemetryClientException {
    TelemetryChartData tmtchartdata = null;
    List<org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream> tmtstreamlist = null;
    List<TelemetryPoint> tmtpoint = null;
    int tot = 0;
    for (XMLGregorianCalendar[] arr : startEndsOfAccessibleProjects) {
      // calculate the average development time, waiting for other measures to come
      tmtchartdata = client.getChart(chartName, uriUser, projectName, HackystatConstants.MONTH,
          arr[0], arr[1], params);
      tmtstreamlist = tmtchartdata.getTelemetryStream();
      if (tmtstreamlist != null) {
        for (org.hackystat.telemetry.service.resource.chart.jaxb.TelemetryStream tmtstream : tmtstreamlist) {
          tmtpoint = tmtstream.getTelemetryPoint();
          if (tmtpoint != null) {
            for (TelemetryPoint point : tmtpoint) {
              if (point.getValue() != null) {
                tot += Integer.parseInt(point.getValue());
              }
            }
          }
        }
      }
    }
    return tot;
  }

  /**
   *
   * @param doc
   * @param uris
   * @return
   */
  protected Document addSparqlResultsToDomDocument(Document doc, String[] uris) {
    // search for result vars
    NodeList nlVariables = doc.getElementsByTagName("variable");
    String[] vars = null;
    if (nlVariables != null) {
      vars = new String[nlVariables.getLength()];
      for (int i = 0; i < nlVariables.getLength(); i++) {
        vars[i] = ((Element) nlVariables.item(i)).getAttribute("name");
      }
      NodeList nl = doc.getElementsByTagName("results");
      if (nl != null) {
        Element results = (Element) nl.item(0), result = null, binding = null, uri = null;
        if (results != null) {
          for (int j = 0; j < uris.length; j++) {
            result = doc.createElement("result");
            for (int i = 0; i < vars.length; i++) {
              binding = doc.createElement("binding");
              binding.setAttribute("name", vars[i]);
              uri = doc.createElement("uri");
              if (j < uris.length) {
                uri.appendChild(doc.createTextNode(uris[j]));
                j++;
              }
              binding.appendChild(uri);
              result.appendChild(binding);
            }
            if ((j - 1) < uris.length) {
              // j was been incremented before exiting the last
              // "for" cycle
              j--;
            }
            results.appendChild(result);
          }
        }
      }
    }
    return doc;
  }

  /**
   *
   * @param queryStr
   * @param completeModel
   * @return
   * @throws UnsupportedEncodingException
   */
  @SuppressWarnings("unchecked")
  protected Object[] getResourcesMatchingSparqlQuery(String queryStr, Model completeModel)
      throws UnsupportedEncodingException {
    queryStr = java.net.URLDecoder.decode(queryStr, "UTF-8");
    Query query = QueryFactory.create(queryStr);
    QueryExecution qe = QueryExecutionFactory.create(query, completeModel);
    Document doc = null;
    LinkedList<String> values = null;
    try {
      ResultSet results = null;
      if (queryStr.contains("SELECT")) {
        results = qe.execSelect();
      }
      else {
        setStatusError("Error creating LiSeD.", new Exception("This Sparql endpoint only accepts "
            + "SELECT query"));
        return null;
      }
      /**
       * The Sparql XML result format is the only W3C recommandation regarding Sparql result formats
       * http://www.w3.org/TR/rdf-sparql-XMLres/ even if someone (such as Dbpedia) provides Sparql
       * results also in RDF/n3 anticipating the W3C verdict about this new format proposed
       **/
      Object[] resp = createSparqlResult(results);
      doc = (Document) resp[0];
      values = (LinkedList<String>) resp[1];
      // Output query results
      ResultSetFormatter.out(System.out, results, query);
      System.out.println(queryStr);
    }
    catch (Exception e) {
      setStatusError("Error creating LiSeD.", e);
      e.printStackTrace();
      return null;
    }
    finally {
      // Important - free up resources used running the query
      qe.close();
    }
    return new Object[] { doc, values };
  }

  /**
   *
   * @return
   */
  private static Document createDomDocument() {
    try {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.newDocument();
      return doc;
    }
    catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   *
   * @param results
   * @return
   */
  private Object[] createSparqlResult(ResultSet results) {
    Object[] ret = null;
    LinkedList<String> values = new LinkedList<String>();
    String head = "head", variable = "variable", variable_attribute = "name", resultsStr = "results", resultStr = "result", binding = "binding";
    Document doc = createDomDocument();

    // Insert the root element node
    Element root = doc.createElement("sparql"), headElem = null, subheadElem = null, resultsElem = null, resultElem = null, bindingElem = null;
    root.setAttribute("xmlns", "\"http://www.w3.org/2005/sparql-results#\"");
    doc.appendChild(root);

    // Insert the head section containing all the variables
    headElem = doc.createElement(head);
    for (String str : results.getResultVars()) {
      subheadElem = doc.createElement(variable);
      subheadElem.setAttribute(variable_attribute, str);
      headElem.appendChild(subheadElem);
    }
    root.appendChild(headElem);

    // Insert the result section containing all the result variables binded to values
    /**
     * The element that contains values should be: RDF URI Reference U
     * <binding><uri>U</uri></binding> RDF Literal S <binding><literal>S</literal></binding> RDF
     * Literal S with language L <binding><literal xml:lang="L">S</literal></binding> RDF Typed
     * Literal S with datatype URI D <binding><literal datatype="D">S</literal></binding> Blank Node
     * label I <binding><bnode>I</bnode></binding>
     */
    resultsElem = doc.createElement(resultsStr);
    RDFNode x = null;
    com.hp.hpl.jena.rdf.model.Resource r = null;
    Element valueElem = null;
    QuerySolution soln = null;
    for (; results.hasNext();) {
      soln = results.nextSolution();
      resultElem = doc.createElement(resultStr);
      // Get a result variable by name.
      for (String str : results.getResultVars()) {
        x = soln.get(str);
        bindingElem = doc.createElement(binding);
        bindingElem.setAttribute(variable_attribute, str);
        if (x.isLiteral()) {
          valueElem = doc.createElement("literal");
          valueElem.appendChild(doc.createTextNode(((Literal) x).getLexicalForm()));
          values.add(((Literal) x).getLexicalForm());
        }
        else if (x.isResource()) {
          r = (com.hp.hpl.jena.rdf.model.Resource) x;
          if (!r.isAnon()) {
            valueElem = doc.createElement("uri");
            valueElem.appendChild(doc.createTextNode(r.getURI()));
            values.add(r.getURI());
          }
        }
        bindingElem.appendChild(valueElem);
        resultElem.appendChild(bindingElem);
      }
      resultsElem.appendChild(resultElem);
    }
    root.appendChild(resultsElem);
    ret = new Object[] { doc, values };
    return ret;
  }

  /**
   * The Restlet getRepresentation method which must be overridden by all concrete Resources.
   *
   * @param variant The variant requested.
   * @return The Representation.
   */
  @Override
  public abstract Representation represent(Variant variant);

  /**
   * Creates and returns a new Restlet StringRepresentation built from xmlData. The xmlData will be
   * prefixed with a processing instruction indicating UTF-8 and version 1.0.
   *
   * @param xmlData The xml data as a string.
   * @return A StringRepresentation of that xmldata.
   */
  public static StringRepresentation getStringRepresentationForSparqlResults(String xmlData) {
    return new StringRepresentation(xmlData, new MediaType(
        HackystatConstants.MEDIA_TYPE_SPARQL_RESULTS), Language.ALL, CharacterSet.UTF_8);
  }

  /**
   * Creates and returns a new Restlet StringRepresentation built from xmlData. The xmlData will be
   * prefixed with a processing instruction indicating UTF-8 and version 1.0.
   *
   * @param xmlData The xml data as a string.
   * @return A StringRepresentation of that xmldata.
   */
  public static StringRepresentation getStringRepresentationForXml(String xmlData) {
    return new StringRepresentation(xmlData, MediaType.TEXT_XML, Language.ALL, CharacterSet.UTF_8);
  }

  /**
   *
   * @param props
   * @return
   * @throws Exception
   */
  public static String makeUser(org.hackystat.sensorbase.resource.users.jaxb.Properties props)
      throws Exception {
    Document doc = createDomDocument();
    String ret = null;
    // Insert the root element node
    Element root = doc.createElement("Properties"), prop = null, key = null, value = null;
    doc.appendChild(root);
    List<org.hackystat.sensorbase.resource.users.jaxb.Property> list = props.getProperty();

    try {
      for (org.hackystat.sensorbase.resource.users.jaxb.Property p : list) {
        prop = doc.createElement("Property");
        key = doc.createElement("Key");
        value = doc.createElement("Value");
        key.appendChild(doc.createTextNode(p.getKey()));
        value.appendChild(doc.createTextNode((p.getValue())));
        prop.appendChild(key);
        prop.appendChild(value);
        root.appendChild(prop);
      }
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      StreamResult result = new StreamResult(new StringWriter());
      DOMSource source = new DOMSource(doc);
      transformer.transform(source, result);
      ret = result.getWriter().toString();
    }
    catch (TransformerException ex) {
      ex.printStackTrace();
      return null;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }

    return ret;
  }

  /**
   * Create the RDF model that describes this Hackystat published dataset using the voID vocabulary.
   */
  public static Model initVoIDModel() {
    Model model = ModelFactory.createDefaultModel();
    com.hp.hpl.jena.rdf.model.Resource dataset = model.createResource(HackystatVocab.NS);
    dataset.addProperty(RDF.type, VoIDVocab.DATASET);
    dataset.addProperty(DC.title, "Hackystat_LinkedServiceData");
    dataset.addProperty(DC.description,
        "Data about users, projects and issues stored in the Hackystat SensorBase "
            + "and/or further handled by the Hackystat Telemetry service "
            + "published as Linked Data.");
    dataset.addProperty(VoIDVocab.URI_REGEX_PATTERN, ".*users.*");
    dataset.addProperty(VoIDVocab.URI_REGEX_PATTERN, ".*projects.*");
    dataset.addProperty(VoIDVocab.URI_REGEX_PATTERN, ".*issues.*");
    dataset.addProperty(VoIDVocab.URI_REGEX_PATTERN, ".*users/sparql?query=.*");
    dataset.addProperty(VoIDVocab.URI_REGEX_PATTERN, ".*projects/sparql?query=.*");
    dataset.addProperty(VoIDVocab.URI_REGEX_PATTERN, ".*issues/sparql?query=.*");
    dataset.addProperty(DC.creator,
        "http://foafbuilder.qdos.com/people/myriamleggieri.wordpress.com/foaf.rdf#iammyr");
    dataset
        .addProperty(
            DC.publisher,
            model
                .createResource()
                .addProperty(RDF.type, FoafVocab.ORGANIZATION)
                .addProperty(
                    RDFS.label,
                    "Hackystat - Collaborative Software Development Laboratory - Department of Information and Computer Sciences - University of Hawaii at Manoa")
                .addProperty(FoafVocab.HOMEPAGE, "http://csdl.ics.hawaii.edu/research/hackystat"));
    /** The following subject URIs come from the UMBEL dataset (based upon OpenCyc). */
    dataset.addProperty(DC.subject, "http://umbel.org/umbel/sc/SoftwareProject");
    dataset.addProperty(DC.subject, "http://umbel.org/umbel/sc/SoftwareDeveloper");
    dataset.addProperty(DC.subject, "http://umbel.org/umbel/sc/SoftwareIssue");
    dataset.addProperty(DCTerms.accessRights, "http://www.gnu.org/copyleft/fdl.html");
    dataset.addProperty(VoIDVocab.SPARQL_ENDPOINT,
        "http://dasha.ics.hawaii.edu:9875/linkedservicedata/users/sparql?query=");
    dataset.addProperty(VoIDVocab.SPARQL_ENDPOINT,
        "http://dasha.ics.hawaii.edu:9875/linkedservicedata/projects/sparql?query=");
    dataset.addProperty(VoIDVocab.SPARQL_ENDPOINT,
        "http://dasha.ics.hawaii.edu:9875/linkedservicedata/issues/sparql?query=");
    dataset.addProperty(VoIDVocab.VOCABULARY, BaetleVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, DoapVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, FoafVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, HackystatVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, Helios_btVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, IswcVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, ProcessVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, SecVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, SiocVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, VoIDVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, VomVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, DC.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, OWL.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, DCTerms.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, CommontagVocab.NS);
    dataset.addProperty(VoIDVocab.VOCABULARY, "http://umbel.org/umbel/sc/");
    return model;
  }

  /**
   *
   * @param doc
   * @return
   */
  public static String serializeDomDocument(Document doc)
  // throws IOException, TransformerException
  {
    String ret = null;
    // set up a transformer
    try {
      TransformerFactory transfac = TransformerFactory.newInstance();
      Transformer trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      trans.setOutputProperty(OutputKeys.INDENT, "yes");

      // create string from xml tree
      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      DOMSource source = new DOMSource(doc);
      trans.transform(source, result);
      ret = sw.toString();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   *
   * @param content
   * @return
   */
  public static Document deserializeDomDocument(String content)
  // throws IOException, ParserConfigurationException, SAXException
  {
    Document ret = null;
    try {
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(content));
      ret = db.parse(is);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  /**
   * Initialize the specified model with the Hackystat ontology. In this way in future, inferences
   * based on that ontology will be allowed.
   *
   * @param model model.
   */
  protected Model initModel(Model model) {
    if (model == null) {
      String schemapath = HackystatVocab.class.getResource("hackystat.rdf").getPath();
      model = ModelFactory.createDefaultModel();
      // use the FileManager to find the input file
      java.io.InputStream in = FileManager.get().open(schemapath);
      if (in == null) {
        throw new IllegalArgumentException("File: " + schemapath + " not found");
      }
      // read the RDF/XML file
      model.read(in, null);
    }
    return model;
  }

  /**
   *
   * @param model
   * @return
   * @throws Exception
   */
  protected Model addAllAccessibleCachedData(Model model) throws Exception {
    String serializedModel = null;
    Model additional_model = null;
    for (Object uri_key : this.lsdServer.getFrontSideCache().getAllInstances(uriUser)) {
      serializedModel = this.lsdServer.getFrontSideCache().get(String.valueOf(uri_key), uriUser);
      if (serializedModel != null) {
        additional_model = deserializeRDFModel(serializedModel, null, HackystatConstants.LANG_N3);
        model = model.union(additional_model);
      }
    }
    return model;
  }

  /**
   * Creates and returns a string representation of a given RDF model, using the specified RDF
   * serialization (N3, RDF/XML, etc.)
   *
   * @param model
   * @param relativeUriBase
   * @param lang
   * @return
   */
  public static String serializeRDFModel(Model model, String relativeUriBase, String lang) {
    String ret = null;
    java.io.OutputStream os = null;
    // Serialize over an outputStream
    os = new ByteArrayOutputStream();
    model.write(os, lang, relativeUriBase);
    ret = os.toString();
    try {
      os.close();
    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return ret;
  }

  /**
   * Creates and returns a RDF model from a given string representation of it
   *
   * @param modelAsString
   * @param relativeURIBase
   * @param lang
   * @return
   */
  public static Model deserializeRDFModel(String modelAsString, String relativeURIBase, String lang) {
    Model m_returned = ModelFactory.createDefaultModel();
    InputStream is = null;
    try {
      RDFReader reader = m_returned.getReader(lang);
      // if (! (reader instanceof TurtleReader ))
      // return null;
      is = new ByteArrayInputStream(modelAsString.getBytes("UTF-8"));
      System.out.println(modelAsString);
      reader.read(m_returned, is, relativeURIBase);
      // m_returned.read(is, lang, relativeURIBase);
      is.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println("language=" + lang);
      return null;
    }
    return m_returned;
  }

  /**
   * Returns a DailyProjectDataClient instance associated with the User in this request.
   *
   * @return The DailyProjectDataClient instance.
   */
  @SuppressWarnings("unchecked")
  public DailyProjectDataClient getDailyProjectDataClient() {
    Map<String, DailyProjectDataClient> userClientMap = (Map<String, DailyProjectDataClient>) this.lsdServer
        .getContext().getAttributes().get(AUTHENTICATOR_DPD_CLIENTS_KEY);
    return userClientMap.get(this.authUser);
  }

  /**
   * Returns a TelemetryClient instance associated with the User in this request.
   *
   * @return The TelemtryClient instance.
   */
  @SuppressWarnings("unchecked")
  public TelemetryClient getTelemetryClient() {
    Map<String, TelemetryClient> userClientMap = (Map<String, TelemetryClient>) this.lsdServer
        .getContext().getAttributes().get(AUTHENTICATOR_TELEMETRY_CLIENTS_KEY);
    return userClientMap.get(this.authUser);
  }

  /**
   * Returns a SensorBaseClient instance associated with the User in this request.
   *
   * @return The SensorBaseClient instance.
   */
  @SuppressWarnings("unchecked")
  public SensorBaseClient getSensorBaseClient() {
    Map<String, SensorBaseClient> userClientMap = (Map<String, SensorBaseClient>) this.lsdServer
        .getContext().getAttributes().get(AUTHENTICATOR_SENSORBASE_CLIENTS_KEY);
    return userClientMap.get(this.authUser);
  }

  /**
   * Generates a log message indicating the type of request, the elapsed time required, the user who
   * requested the data, and the day.
   *
   * @param requestType The type of LSD request, such as "User", "Project", etc.
   */
  protected void logRequest(String requestType) {
    logRequest(requestType, "");
  }

  /**
   * Generates a log message indicating the type of request, the elapsed time required, the user who
   * requested the data, and the day.
   *
   * @param requestType The type of LSD request, such as "User", "Project", etc.
   * @param optionalParams Any additional parameters to the request.
   */
  protected void logRequest(String requestType, String... optionalParams) {
    long elapsed = new Date().getTime() - requestStartTime;
    String sp = HackystatConstants.SEPARATOR1_ID;
    StringBuffer msg = new StringBuffer(20);
    msg.append(elapsed).append(" ms: ").append(requestType).append(sp).append(uriUser).append(sp);
    msg.append(projectName).append(sp).append(timestamp);
    for (String param : optionalParams) {
      msg.append(sp).append(param);
    }
    lsdServer.getLogger().info(msg.toString());
  }

  /**
   * Called when an error resulting from an exception is caught during processing.
   *
   * @param msg A description of the error.
   * @param e A chained exception.
   */
  protected void setStatusError(String msg, Exception e) {
    String responseMsg = String.format("%s:%n  Request: %s %s%n  Caused by: %s", msg, this
        .getRequest().getMethod().getName(), this.getRequest().getResourceRef().toString(), e
        .getMessage());
    this.getLogger().info(responseMsg);
    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, removeNewLines(responseMsg));
  }

  /**
   * Called when an error occurs during processing.
   *
   * @param msg A description of the error.
   */
  protected void setStatusError(String msg) {
    String responseMsg = String.format("%s:%n  Request: %s %s", msg, this.getRequest().getMethod()
        .getName(), this.getRequest().getResourceRef().toString());
    this.getLogger().info(responseMsg);
    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, removeNewLines(responseMsg));
  }

  /**
   * Called when an internal error occurs during processing.
   *
   * @param msg A description of the error.
   */
  protected void setStatusInternalError(String msg) {
    String responseMsg = String.format("%s:%n  Request: %s %s", msg, this.getRequest().getMethod()
        .getName(), this.getRequest().getResourceRef().toString());
    this.getLogger().info(responseMsg);
    getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, removeNewLines(responseMsg));
  }

  /**
   * Returns true if the authorized user is the admin or user in the URI string. Otherwise sets the
   * Response status and returns false.
   *
   * @return True if the authorized user is the admin or the URI user.
   */
  protected boolean validateUserIsAdmin() {
    try {
      if (this.authUser.equals(System.getProperty(ServerProperties.ADMIN_EMAIL_KEY))) {
        return true;
      }
      else {
        return false;
      }
    }
    catch (RuntimeException e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, removeNewLines(this.responseMsg));
    }
    return false;
  }

  /**
   * Returns true if the authorized user is the user in the URI string. Otherwise sets the Response
   * status and returns false.
   *
   * @return True if the authorized user is the admin or the URI user.
   */
  protected boolean validateUserIsUriUser() {
    try {
      if (this.authUser.equals(this.uriUser)) {
        return true;
      }
      else {
        return false;
      }
    }
    catch (RuntimeException e) {
      getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, removeNewLines(this.responseMsg));
    }
    return false;
  }

  /**
   * Helper function that removes any newline characters from the supplied string and replaces them
   * with a blank line.
   *
   * @param msg The msg whose newlines are to be removed.
   * @return The string without newlines.
   */
  private String removeNewLines(String msg) {
    return msg.replace(System.getProperty("line.separator"), HackystatConstants.SEPARATOR1_ID);
  }

  /**
   *
   * @param location
   * @return
   * @throws UnsupportedEncodingException
   */
  public static String getRepositoryUri(String location) throws UnsupportedEncodingException {
    location = java.net.URLEncoder.encode(location, "UTF-8");
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.REPOSITORY_RESOURCE_TYPE
        + location;
    return uri;
  }

  /**
   *
   * @return
   */
  public static String getAllUsersUri() {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.USER_RESOURCE_TYPE;
    return uri;
  }

  /**
   *
   * @return
   */
  public static String getAllProjectsUri() {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.PROJECT_RESOURCE_TYPE;
    return uri;
  }

  /**
   *
   * @param owner
   * @param name
   * @return
   */
  public static String getProjectUri(String owner, String name) {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.PROJECT_RESOURCE_TYPE
        + owner + HackystatConstants.SLASH + name;
    return uri;
  }

  /**
   *
   * @param email
   * @return
   */
  public static String getUserUri(String email) {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.USER_RESOURCE_TYPE
        + email;
    return uri;
  }

  /**
   *
   * @param email
   * @return
   */
  public static String getUserProjectsUri(String email) {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.PROJECT_RESOURCE_TYPE
        + email.toLowerCase();
    return uri;
  }

  /**
   *
   * @param email
   * @param projectname
   * @param phaseid
   * @return
   */
  public static String getDevPhaseUri(String email, String projectname, String phaseid) {
    String uri = HackystatConstants.RESOURCE_URI_BASE
        + HackystatConstants.SWDEVELOPMENTPHASE_RESOURCE_TYPE + email.toLowerCase()
        + HackystatConstants.SLASH + projectname + HackystatConstants.SLASH + phaseid;
    return uri;
  }

  /**
   *
   * @param name
   * @return
   */
  public static String getToolUri(String name) {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.TOOL_RESOURCE_TYPE
        + name;
    return uri;
  }

  /**
   *
   * @param email
   * @param name
   * @return
   */
  public static String getCommandUri(String email, String name) {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.COMMAND_RESOURCE_TYPE
        + email + HackystatConstants.SLASH + name.toLowerCase();
    return uri;
  }

  /**
   *
   * @param name
   * @return
   */
  public static String getProgrammingLanguageUri(String name) {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.LANGUAGE_RESOURCE_TYPE
        + name.toLowerCase();
    return uri;
  }

  /**
   *
   * @param name
   * @return
   */
  public static String getMachineUri(String name) {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.MACHINE_RESOURCE_TYPE
        + name.toLowerCase();
    return uri;
  }

  /**
   *
   * @param name
   * @return
   */
  public static String getOperatingSystemUri(String name) {
    String uri = HackystatConstants.RESOURCE_URI_BASE
        + HackystatConstants.OPERATINGSYSTEM_RESOURCE_TYPE + name.toLowerCase();
    return uri;
  }

  /**
   *
   * @param id
   * @return
   */
  public static String getIssueUri(String id) {
    String uri = HackystatConstants.RESOURCE_URI_BASE + HackystatConstants.ISSUE_RESOURCE_TYPE + id;
    return uri;
  }

  /**
   *
   * @param project_resource
   * @param model
   * @param owner
   * @param projectName
   * @return
   */
  public static com.hp.hpl.jena.rdf.model.Resource createProjectResource(
      com.hp.hpl.jena.rdf.model.Resource project_resource, Model model, String owner,
      String projectName) {
    project_resource = model.createResource(getProjectUri(owner, projectName));
    project_resource.addProperty(RDF.type, DoapVocab.PROJECT);
    return project_resource;
  }

  /**
   *
   * @param model
   * @param value
   * @return
   */
  public static com.hp.hpl.jena.rdf.model.Resource createLanguageResource(Model model, String value) {
    com.hp.hpl.jena.rdf.model.Resource language_resource = model
        .createResource(getProgrammingLanguageUri(value));
    language_resource.addProperty(RDF.type, HackystatVocab.PROGRAMMING_LANGUAGE);
    return language_resource;
  }

  /**
   *
   * @param model
   * @param value
   * @return
   */
  public static com.hp.hpl.jena.rdf.model.Resource createMachineResource(Model model, String value) {
    com.hp.hpl.jena.rdf.model.Resource resource = model.createResource(getMachineUri(value));
    resource.addProperty(RDF.type, HackystatVocab.MACHINE);
    return resource;
  }

  /**
   *
   * @param model
   * @param value
   * @return
   */
  public static com.hp.hpl.jena.rdf.model.Resource createOperatingSystemResource(Model model,
      String value) {
    com.hp.hpl.jena.rdf.model.Resource resource = model
        .createResource(getOperatingSystemUri(value));
    resource.addProperty(RDF.type, HackystatVocab.OPERATING_SYSTEM);
    return resource;
  }

  /**
   *
   * @param resource
   * @param model
   * @param email
   * @param command
   * @return
   */
  public static com.hp.hpl.jena.rdf.model.Resource createCommandResource(
      com.hp.hpl.jena.rdf.model.Resource resource, Model model, String email, String command) {
    resource = model.createResource(getCommandUri(email, command));
    resource.addProperty(RDF.type, HackystatVocab.COMMAND);
    return resource;
  }

  /**
   *
   * @param model
   * @param id
   * @param type
   * @return
   */
  public static com.hp.hpl.jena.rdf.model.Resource createIssueResource(Model model, String id,
      String type) {
    com.hp.hpl.jena.rdf.model.Resource issue = model.createResource(getIssueUri(id));
    if (type.equals("Defect")) {
      issue.addProperty(RDF.type, BaetleVocab.DEFECT);
    }
    else if (type.equals("Enhancement")) {
      issue.addProperty(RDF.type, BaetleVocab.ENHANCEMENT);
    }
    else {
      issue.addProperty(RDF.type, BaetleVocab.ISSUE);
    }
    return issue;
  }

  /**
   *
   * @param model
   * @param value
   * @return
   */
  public static com.hp.hpl.jena.rdf.model.Resource createToolResource(Model model, String value) {
    com.hp.hpl.jena.rdf.model.Resource resource = model.createResource(getToolUri(value));
    resource.addProperty(RDF.type, HackystatVocab.TOOL);
    return resource;
  }

  /**
   *
   * @param model
   * @param location
   * @param type
   * @return
   * @throws UnsupportedEncodingException
   */
  public static com.hp.hpl.jena.rdf.model.Resource createRepositoryResource(Model model,
      String location, String type) throws UnsupportedEncodingException {
    com.hp.hpl.jena.rdf.model.Resource resource = model.createResource(getRepositoryUri(location));
    if (type == null) {
      resource.addProperty(RDF.type, DoapVocab.REPOSITORY_CLASS);
    }
    else {
      if (type.equalsIgnoreCase("cvs")) {
        resource.addProperty(RDF.type, DoapVocab.CVSREPOSITORY);
      }
      else if (type.equalsIgnoreCase("svn") || type.equalsIgnoreCase("subversion")) {
        resource.addProperty(RDF.type, DoapVocab.SVNREPOSITORY);
      }
      else if (type.equalsIgnoreCase("bk") || type.equalsIgnoreCase("BitKeeper")) {
        resource.addProperty(RDF.type, DoapVocab.BKREPOSITORY);
      }
      else if (type.equalsIgnoreCase("arc")) {
        resource.addProperty(RDF.type, DoapVocab.ARCH_REPOSITORY);
      }
      else if (type.equalsIgnoreCase("bazaarbranch")) {
        resource.addProperty(RDF.type, DoapVocab.BAZAAR_BRANCH);
      }
      else if (type.equalsIgnoreCase("hg") || type.equalsIgnoreCase("mercurial")) {
        resource.addProperty(RDF.type, DoapVocab.HG_REPOSITORY);
      }
      else if (type.equalsIgnoreCase("git")) {
        resource.addProperty(RDF.type, DoapVocab.GIT_REPOSITORY);
      }
      else if (type.equalsIgnoreCase("darcs")) {
        resource.addProperty(RDF.type, DoapVocab.DARCS_REPOSITORY);
      }
      else {
        resource.addProperty(RDF.type, DoapVocab.REPOSITORY_CLASS);
      }
    }
    return resource;
  }

  /**
   *
   * @param model
   * @param phaseid
   * @param owner
   * @param projectname
   * @return
   */
  public static com.hp.hpl.jena.rdf.model.Resource createSWDevelopmentPhaseResource(Model model,
      String phaseid, String owner, String projectname) {
    com.hp.hpl.jena.rdf.model.Resource revphase = model.createResource(getDevPhaseUri(owner,
        projectname, phaseid));
    revphase.addProperty(RDF.type, HackystatVocab.DEVELOPMENT_PHASE);
    return revphase;
  }

  /**
   *
   * @param m_returned
   * @param email
   * @param client
   * @return
   * @throws SensorBaseClientException
   */
  public static com.hp.hpl.jena.rdf.model.Resource createUserResource(Model m_returned,
      String email, SensorBaseClient client) throws SensorBaseClientException {
    com.hp.hpl.jena.rdf.model.Resource resource = m_returned.createResource(getUserUri(email));
    User u = client.getUser(email);
    if (u != null) {
      resource = LiSeDUserResource.addUserType(resource, u.getRole());
    }
    return resource;
  }

  /**
   * If a statement that you wish to add to a model, already exists, you can add it only if it's
   * been collected at a more recent time. This method check if you can add a statement
   *
   * @param resource RDF resource
   * @param prop RDF property
   * @param model RDF model
   * @param lasttime time at which the last inserted statement has been collected
   * @param currenttime time at which the statement to add has been collected
   * @return 1 if the statement doesn't already exists in that model 2 if the statement that you
   * wish to add is more recent than the last inserted one; 0 otherwise.
   */
  public static int addableStatement(com.hp.hpl.jena.rdf.model.Resource resource, OntProperty prop,
      Model model, XMLGregorianCalendar lasttime, XMLGregorianCalendar currenttime) {
    int ret = 0;
    boolean exists = model.contains(resource, prop, (RDFNode) null);
    if (resource != null && prop != null) {
      if (lasttime == null && !exists) {
        ret = 1;
      }
      else if (lasttime != null) {
        if (exists) {
          if (currenttime.compare(lasttime) == DatatypeConstants.GREATER) {
            ret = 2;
          }
        }
        else {
          ret = 1;
        }
      }
    }
    return ret;
  }

  /**
   * If a statement that you wish to add to a model, already exists, you can add it only if it's
   * been collected at a more recent time. This method check if you can add a statement
   *
   * @param resource RDF resource
   * @param prop RDF property
   * @param model RDF model
   * @param lasttime time at which the last inserted statement has been collected
   * @param currenttime time at which the statement to add has been collected
   * @return 1 if the statement doesn't already exists in that model 2 if the statement that you
   * wish to add is more recent than the last inserted one; 0 otherwise.
   */
  public static int addableStatement(com.hp.hpl.jena.rdf.model.Resource resource, OntProperty prop,
      RDFNode obj, Model model, XMLGregorianCalendar lasttime, XMLGregorianCalendar currenttime) {
    int ret = 0;
    boolean exists = model.contains(resource, prop, obj);
    if (resource != null && prop != null) {
      if (lasttime == null && !exists) {
        ret = 1;
      }
      else if (lasttime != null) {
        if (exists) {
          if (currenttime.compare(lasttime) == DatatypeConstants.GREATER) {
            ret = 2;
          }
        }
        else {
          ret = 1;
        }
      }
    }
    return ret;
  }

  /**
   * If a statement that you wish to add to a model, already exists, you can add it only if it's
   * been collected at a more recent time. This method check if you can add a statement
   *
   * @param resource RDF resource
   * @param prop RDF property
   * @param model RDF model
   * @param lasttime time at which the last inserted statement has been collected
   * @param currenttime time at which the statement to add has been collected
   * @return 1 if the statement doesn't already exists in that model 2 if the statement that you
   * wish to add is more recent than the last inserted one; 0 otherwise.
   */
  public static int addableStatement(com.hp.hpl.jena.rdf.model.Resource resource, OntProperty prop,
      String obj, Model model, XMLGregorianCalendar lasttime, XMLGregorianCalendar currenttime) {
    int ret = 0;
    boolean exists = model.contains(resource, prop, obj);
    if (resource != null && prop != null) {
      if (lasttime == null && !exists) {
        ret = 1;
      }
      else if (lasttime != null) {
        if (exists) {
          if (currenttime.compare(lasttime) == DatatypeConstants.GREATER) {
            ret = 2;
          }
        }
        else {
          ret = 1;
        }
      }
    }
    return ret;
  }

  /**
   * Return the last day of the specified month and year.
   *
   * @param month
   * @param year
   * @return
   */
  private static int lastDayOfTheMonth(int month, int year) {
    int last = 0;
    if (month == 11 || month == 4 || month == 6 || month == 9) {
      last = 30;
    }
    else if (month == 2) {
      if (year % 4 == 0) {
        last = 28;
      }
      else {
        last = 29;
      }
    }
    else {
      last = 31;
    }
    return last;
  }

  /**
   * Adjust start and end dates in such a way to avoid Telemetry exception claiming for granularity
   * troubles.
   *
   * @param startProj
   * @param endProj
   * @param granularity
   * @return
   */
  protected static XMLGregorianCalendar[] adjustStartEndDates(XMLGregorianCalendar startProj,
      XMLGregorianCalendar endProj, String granularity) {
    XMLGregorianCalendar[] arr = null;
    XMLGregorianCalendar now = Tstamp.makeTimestamp(), end = null;

    if (Tstamp.lessThan(startProj, now) || Tstamp.equal(startProj, now)) {

      if (Tstamp.greaterThan(endProj, now)) {
        end = now;
      }
      else {
        end = endProj;
      }
      if (granularity.equals(HackystatConstants.MONTH)) {
        if (startProj.getDay() != 1) {
          startProj.setDay(1);
          if (startProj.getMonth() == 12) {
            startProj.setMonth(1);
            startProj.setYear(startProj.getYear() + 1);
          }
          else {
            startProj.setMonth(startProj.getMonth() + 1);
          }
          // System.out.println("");
        }
        int lastDay = lastDayOfTheMonth(end.getMonth(), end.getYear());
        if (end.getDay() != lastDay) {
          end.setMonth(end.getMonth() - 1);
          end.setDay(lastDayOfTheMonth(end.getMonth(), end.getYear()));
        }
      }
      arr = new XMLGregorianCalendar[2];
      // after setting the start and end dates at a month whose days are "all" (from the 1st to the
      // last day)
      // included in the project existing period
      // if the start date is greater than the end one means that there's no full month between the
      // start and
      // the end period. Then the granularity should not be 'Month'
      if (Tstamp.greaterThan(startProj, end)) {
        return null;
      }
      arr[0] = startProj;
      arr[1] = end;
    }
    else {
      return null;
    }
    return arr;
  }

  /**
   * Currently there's no way to check if a bug has been set to be visible by anyone or not. In the
   * future it could be useful to hide some too critical security-related bugs, and this method will
   * allow to check if a bug description asked by a user, is accessible or not.
   *
   * @param sd
   * @return
   */
  protected boolean isBugAccessible(SensorData sd) {
    return true;
  }

  /**
   * Check if a project profile is accessible or not.
   *
   * @param proj
   * @return
   */
  protected boolean isProjectProfileAccessible(Project proj) {
    boolean ret = false;
    Properties props = proj.getProperties();
    if (props != null) {
      List<Property> listprop = props.getProperty();
      if (listprop != null) {
        String key = null, value = null;
        Property prop = null;
        Iterator<Property> it = listprop.iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
          prop = it.next();
          key = prop.getKey();
          value = prop.getValue();
          if (key.equals(HackystatConstants.PROFILE_VISIBILITY)) {
            found = true;
            // if (value.equals("2")){
            // visible to anyone; not allowed by the SensorBase
            // ret=true;
            // }else
            if (value.equals("1")) {
              // visible only to hackystat registered users
              ret = true;
            }
          }
        }
      }
    }
    if (!ret) {
      // default setting: visible only to the specified user if involeved in
      // this pproject or to the admin
      if (validateUserIsAdmin()
          || (getSensorBaseClient().inProject(uriUser, projectName) && validateUserIsUriUser())) {
        ret = true;
      }
    }
    return ret;
  }

  /**
   * Check if a user profile is accessible or not.
   *
   * @param user
   * @return
   */
  protected boolean isUserProfileAccessible(User user) {
    boolean ret = false;
    org.hackystat.sensorbase.resource.users.jaxb.Properties props = user.getProperties();
    if (props != null) {
      List<org.hackystat.sensorbase.resource.users.jaxb.Property> listprop = props.getProperty();
      if (listprop != null) {
        String key = null, value = null;
        org.hackystat.sensorbase.resource.users.jaxb.Property prop = null;
        Iterator<org.hackystat.sensorbase.resource.users.jaxb.Property> it = listprop.iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
          prop = it.next();
          key = prop.getKey();
          value = prop.getValue();
          if (key.equals(HackystatConstants.PROFILE_VISIBILITY)) {
            found = true;
            // if (value.equals("2")){
            // visible to anyone; not allowed by the SensorBase
            // ret=true;
            // }else
            if (value.equals("1")) {
              // visible only to hackystat registered users
              ret = true;
            }
          }
        }
      }
    }
    if (!ret) {
      // default setting: visible only to the specified user or admin
      if (validateUserIsAdmin() || validateUserIsUriUser()) {
        ret = true;
      }
    }
    return ret;
  }

  /**
   * Check if the SensorData associated with the specified user are accessible by the logged user.
   *
   * @param user
   * @return
   */
  protected boolean isSDAccessible(User user) {
    boolean ret = false;
    org.hackystat.sensorbase.resource.users.jaxb.Properties props = user.getProperties();
    if (props != null) {
      List<org.hackystat.sensorbase.resource.users.jaxb.Property> listprop = props.getProperty();
      if (listprop != null) {
        String key = null, value = null;
        org.hackystat.sensorbase.resource.users.jaxb.Property prop = null;
        Iterator<org.hackystat.sensorbase.resource.users.jaxb.Property> it = listprop.iterator();
        boolean found = false;
        while (it.hasNext() && !found) {
          prop = it.next();
          key = prop.getKey();
          value = prop.getValue();
          if (key.equals(HackystatConstants.SENSORDATA_VISIBILITY)) {
            found = true;
            // if (value.equals("2")){
            // visible to anyone; not allowed by the SensorBase
            // ret=true;
            // }else
            if (value.equals("1")) {
              // visible only to hackystat registered users
              ret = true;
            }
          }
        }
      }
    }
    if (!ret) {
      // default setting: visible only to the specified user or admin
      if (validateUserIsAdmin() || validateUserIsUriUser()) {
        ret = true;
      }
    }
    return ret;
  }

  /**
   * Returns the Property instance with the specified property name. If no property exists,
   * defaultValue is returned.
   *
   * @param propertyName the property name to search for.
   * @param defaultValue The string to return if the property does not exist.
   * @return The property with the specified name or defaultValue.
   */
  public org.hackystat.sensorbase.resource.projects.jaxb.Property getProjectProperty(
      String propertyName, String defaultValue, Project data) {
    if (data.getProperties() != null) {
      List<org.hackystat.sensorbase.resource.projects.jaxb.Property> propertyList = data
          .getProperties().getProperty();
      if (propertyList != null) {
        ListIterator<org.hackystat.sensorbase.resource.projects.jaxb.Property> it = propertyList
            .listIterator(propertyList.size());
        org.hackystat.sensorbase.resource.projects.jaxb.Property property = null;
        while (it.hasPrevious()) {
          property = it.previous();
          if (propertyName.equals(property.getKey())) {
            return property;
          }
        }
      }
    }
    // Make a new Property instance.
    org.hackystat.sensorbase.resource.projects.jaxb.Property property = new Property();
    property.setKey(propertyName);
    property.setValue(defaultValue);
    return property;
  }

  /**
   * Returns the Property instance with the specified property name. If no property exists,
   * defaultValue is returned.
   *
   * @param propertyName the property name to search for.
   * @return The property with the specified name or null.
   */
  public org.hackystat.sensorbase.resource.projects.jaxb.Property getProjectProperty(
      String propertyName, Project data) {
    List<org.hackystat.sensorbase.resource.projects.jaxb.Property> propertyList = data
        .getProperties().getProperty();
    if (propertyList != null) {
      ListIterator<org.hackystat.sensorbase.resource.projects.jaxb.Property> it = propertyList
          .listIterator(propertyList.size());
      org.hackystat.sensorbase.resource.projects.jaxb.Property property = null;
      while (it.hasPrevious()) {
        property = it.previous();
        if (propertyName.equals(property.getKey())) {
          return property;
        }
      }
    }
    return null;
  }

  /**
   * Returns the Property instance with the specified property name. If no property exists,
   * defaultValue is returned.
   *
   * @param propertyName the property name to search for.
   * @param defaultValue The string to return if the property does not exist.
   * @return The property with the specified name or defaultValue.
   */
  public org.hackystat.sensorbase.resource.users.jaxb.Property getUserProperty(String propertyName,
      String defaultValue, User data) {
    org.hackystat.sensorbase.resource.users.jaxb.Property prop = null;
    if (data.getProperties() != null) {
      List<org.hackystat.sensorbase.resource.users.jaxb.Property> propertyList = data
          .getProperties().getProperty();
      if (propertyList != null) {
        ListIterator<org.hackystat.sensorbase.resource.users.jaxb.Property> it = propertyList
            .listIterator(propertyList.size());
        org.hackystat.sensorbase.resource.users.jaxb.Property property = null;
        while (it.hasPrevious()) {
          property = it.previous();
          if (propertyName.equals(property.getKey())) {
            return property;
          }
        }
      }
    }
    // Make a new Property instance.
    prop = new org.hackystat.sensorbase.resource.users.jaxb.Property();
    prop.setKey(propertyName);
    prop.setValue(defaultValue);
    return prop;
  }

  /**
   * Returns the Property instance with the specified property name. If no property exists,
   * defaultValue is returned.
   *
   * @param propertyName the property name to search for.
   * @return The property with the specified name or null.
   */
  public org.hackystat.sensorbase.resource.users.jaxb.Property getUserProperty(String propertyName,
      User data) {
    if (data.getProperties() != null) {
      List<org.hackystat.sensorbase.resource.users.jaxb.Property> propertyList = data
          .getProperties().getProperty();
      if (propertyList != null) {
        ListIterator<org.hackystat.sensorbase.resource.users.jaxb.Property> it = propertyList
            .listIterator(propertyList.size());
        org.hackystat.sensorbase.resource.users.jaxb.Property property = null;
        while (it.hasPrevious()) {
          property = it.previous();
          if (propertyName.equals(property.getKey())) {
            return property;
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns the Property instance with the specified property name. If no property exists,
   * defaultValue is returned.
   *
   * @param propertyName the property name to search for.
   * @param defaultValue The string to return if the property does not exist.
   * @return The property with the specified name or defaultValue.
   */
  public org.hackystat.sensorbase.resource.sensordata.jaxb.Property getSensorDataProperty(
      String propertyName, String defaultValue, SensorData data) {
    if (data.getProperties() != null) {
      List<org.hackystat.sensorbase.resource.sensordata.jaxb.Property> propertyList = data
          .getProperties().getProperty();
      if (propertyList != null) {
        ListIterator<org.hackystat.sensorbase.resource.sensordata.jaxb.Property> it = propertyList
            .listIterator(propertyList.size());
        org.hackystat.sensorbase.resource.sensordata.jaxb.Property property = null;
        while (it.hasPrevious()) {
          property = it.previous();
          if (propertyName.equals(property.getKey())) {
            return property;
          }
        }
      }
    }
    // Make a new Property instance.
    org.hackystat.sensorbase.resource.sensordata.jaxb.Property property = new org.hackystat.sensorbase.resource.sensordata.jaxb.Property();
    property.setKey(propertyName);
    property.setValue(defaultValue);
    return property;
  }

  /**
   * Returns the Property instance with the specified property name. If no property exists,
   * defaultValue is returned.
   *
   * @param propertyName the property name to search for.
   * @return The property with the specified name or null.
   */
  public org.hackystat.sensorbase.resource.sensordata.jaxb.Property getSensorDataProperty(
      String propertyName, SensorData data) {
    if (data.getProperties() != null) {
      List<org.hackystat.sensorbase.resource.sensordata.jaxb.Property> propertyList = data
          .getProperties().getProperty();
      if (propertyList != null) {
        ListIterator<org.hackystat.sensorbase.resource.sensordata.jaxb.Property> it = propertyList
            .listIterator(propertyList.size());
        org.hackystat.sensorbase.resource.sensordata.jaxb.Property property = null;
        while (it.hasPrevious()) {
          property = it.previous();
          if (propertyName.equals(property.getKey())) {
            return property;
          }
        }
      }
    }
    return null;
  }

  /**
   * Utility to print a RDF model on the standard output.
   *
   * @param rdf
   */
  public static void printModel(Model rdf) {
    StmtIterator it = rdf.listStatements();
    Statement stat = null;
    System.out.println("*****************______"
        + "************RDF MODEL PRINTING UTILITY*****************______" + "************");
    while (it.hasNext()) {
      stat = it.next();
      System.out.println("<" + stat.getSubject().getURI() + ">\n");
      System.out.println("\t" + stat.getPredicate().getURI() + "\n");
      System.out.println("\t\t"
          + (stat.getObject().isLiteral() ? ((Literal) stat.getObject()).getLexicalForm() : (stat
              .getObject()).isAnon() ? "" : (stat.getObject()).asNode().getURI() + " ; \n"));
    }
  }

}

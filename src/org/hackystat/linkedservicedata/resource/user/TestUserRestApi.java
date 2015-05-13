package org.hackystat.linkedservicedata.resource.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.LinkedList;
import org.hackystat.linkedservicedata.client.LinkedServiceDataClient;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.sparql.SparqlFilter;
import org.hackystat.linkedservicedata.sparql.UserQuery;
import org.hackystat.linkedservicedata.test.LinkedServiceDataTestHelper;
import org.hackystat.linkedservicedata.vocabularies.DoapVocab;
import org.hackystat.linkedservicedata.vocabularies.FoafVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.UriPatterns;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Test for the user REST Api.
 *
 * @author Myriam Leggieri.
 *
 */
public class TestUserRestApi extends LinkedServiceDataTestHelper {

  /** The user for this test case. */
  private static final String user = "holatest@hackystat.org";
  private static final String passwd = "holatest@hackystat.org";
  private static final String name = "myriam";
  private static final String surname = "leggieri";
  private static final String nickname = "iammyr";
  private static final String homepage = "http://myr.altervista.org";
  private static final String blog = "http://myr.netsons.org/wordpress";
  private static final String projectName = "Holatest_Project";

  private LinkedServiceDataClient lisedClient = null;
  private SensorBaseClient sbClient = null;

  private static final String eclipse_tool = "Eclipse";
  private static final String emacs_tool = "Emacs";
  private static final String ant_tool = "Ant";
  private static final String netbeans_tool = "Netbeans";
  private static final String bash_tool = "Bash";

  private static final String repository = "https://hackystat-linked-sensor-data.googlecode.com/svn";
  private static final String file = "/home/myrtill/Hackystat_linkedData/mysqlProva/"
      + "workspace/hackystat-linked-service-data/src/org/hackystat/"
      + "linkedservicedata/client/LinkedServiceDataClient.java";

  /** Change the following attributes to test different RDF serialization languages **/
  private String lang = HackystatConstants.LANG_NTRIPLE;
  private MediaType media =
  // MediaType.APPLICATION_RDF_XML;
  new MediaType(HackystatConstants.MEDIA_TYPE_NTRIPLES);

  private static String modelAsString = null;

  /**
   * Create data and send to server if we haven't done it already.
   *
   * @throws Exception If problems occur.
   */
  @Before
  public void setUp() throws Exception {
    // Connect to the LiSeD server and make the client.
    SensorBaseClient.registerUser(getSensorBaseHostName(), user);

    lisedClient = new LinkedServiceDataClient(user, passwd, lang, media, true, true);
    lisedClient.authenticate();
    lisedClient.clearServerCache();
    lisedClient.clearLocalCache();

    sbClient = new SensorBaseClient(getSensorBaseHostName(), user, passwd);
    sbClient.authenticate();
    sbClient.clearCache();

    User u = sbClient.getUser(user);
    u.setRole(HackystatConstants.ROLE_TRANSLATOR);
    u.addProperty(HackystatConstants.PROFILE_VISIBILITY, "1");
    u.addProperty(HackystatConstants.SENSORDATA_VISIBILITY, "1");
    u.addProperty(HackystatConstants.NICKNAME, nickname);
    u.addProperty(HackystatConstants.SURNAME, surname);
    u.addProperty(HackystatConstants.HOMEPAGE, homepage);
    u.addProperty(HackystatConstants.WEBLOG, blog);
    u.addProperty(HackystatConstants.NAME, name);
    lisedClient.makeRequest(getSensorBaseHostName(), Method.POST, "users/" + user,
        LinkedServiceDataResource.getStringRepresentationForXml(LinkedServiceDataResource
            .makeUser(u.getProperties())), user, passwd);
    // send proper sensor data to the sensorbase to avoid they miss at all.
    SensorData sd = new SensorData();
    for (int i = 0; i < HackystatConstants.sensorDataType_list.length; i++) {
      for (int j = 0; j < 5; j++) {
        sd = new SensorData();
        sd.setOwner(user);
        sd.setTimestamp(Tstamp.makeTimestamp());
        sd.setResource(file);
        sd.setSensorDataType(HackystatConstants.sensorDataType_list[i]);
        switch (i) {
        case 9:
          sd.setTool(bash_tool);
          if (j % 2 == 0) {
            sd.addProperty(HackystatConstants.MACHINE, "i386");
            sd.addProperty(HackystatConstants.OPERATING_SYSTEM, "Debian");
            sd.addProperty(HackystatConstants.COMMAND, "awk");

          }
          else {
            sd.addProperty(HackystatConstants.MACHINE, "sparc");
            sd.addProperty(HackystatConstants.OPERATING_SYSTEM, "Leon");
            sd.addProperty(HackystatConstants.COMMAND, "octave-config");

          }
          break;
        case 2:
          if (j % 2 == 0) {
            sd.setTool(eclipse_tool);
          }
          else {
            sd.setTool(netbeans_tool);
          }
          break;
        default:
          if (j % 2 == 0) {
            sd.setTool(ant_tool);
            sd.addProperty(HackystatConstants.LANGUAGE, "C");
          }
          else {
            sd.setTool(emacs_tool);
            sd.addProperty(HackystatConstants.LANGUAGE, "Java");
          }
        }
        sbClient.putSensorData(sd);
      }
    }
    // send proper project data to the sensorbase to avoid they miss at all.
    Project project = new Project();
    project.setOwner(user);
    project.setName(projectName);
    project.setStartTime(Tstamp.makeTimestamp(Day.getInstance(2006, 9, 12)));
    project.setEndTime(Tstamp.makeTimestamp(Day.getInstance(2009, 5, 20)));
    project.addProperty(HackystatConstants.PROJECT_MAINTAINERS, user);
    project.addProperty(HackystatConstants.REPOSITORY_LOCATION, repository);
    project.addProperty(HackystatConstants.REPOSITORY_TYPE, "svn");
    UriPatterns patt = new UriPatterns();
    patt.getUriPattern().add("*/workspace/derbyProva/test/*");
    project.setUriPatterns(patt);
    sbClient.putProject(project);
  }

  /**
   * Test that GET {host}/users/{user} works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testUserRDFModel() {
    try {
      // Test query
      Model userModel = lisedClient.getUserAsLinkedData(user);
      // The user resource in that model should be of foaf:Agent type and having
      // the same foaf:mbox as the user's e-mail stored in the sensorbase DB
      if (userModel == null) {
        assertTrue(false);
        return;
      }

      modelAsString = LinkedServiceDataResource.serializeRDFModel(userModel,
          HackystatConstants.RESOURCE_URI_BASE,
          lang.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE : lang);

      StmtIterator it = userModel.listStatements(null, FoafVocab.MBOX, (RDFNode) null);
      Statement st = null;

      Reasoner rdfsReasoner = ReasonerRegistry.getRDFSReasoner();
      Reasoner hackReasoner = rdfsReasoner.bindSchema(userModel);
      InfModel infmodel = ModelFactory.createInfModel(hackReasoner, userModel);

      assertTrue(it.hasNext());
      if (it.hasNext()) {
        st = it.next();
        assertEquals(st.getObject().asNode().getLiteralLexicalForm(), user);
      }

      // LinkedServiceDataResource.printModel(userModel);

      it = infmodel.listStatements(null, RDF.type, FoafVocab.AGENT);
      assertTrue(it.hasNext());
      if (it.hasNext()) {
        st = it.next();
        assertEquals(st.getSubject().getURI(), LinkedServiceDataResource.getUserUri(user));
      }

      it = userModel.listStatements(null, FoafVocab.FIRST_NAME, (RDFNode) null);
      assertTrue(it.hasNext());
      if (it.hasNext()) {
        st = it.next();
        assertEquals(st.getObject().asNode().getLiteralLexicalForm(), name);
      }

      it = userModel.listStatements(null, FoafVocab.SURNAME, (RDFNode) null);
      assertTrue(it.hasNext());
      if (it.hasNext()) {
        st = it.next();
        assertEquals(st.getObject().asNode().getLiteralLexicalForm(), surname);
      }

      it = userModel.listStatements(null, DoapVocab.MAINTAINER, (RDFNode) null);
      assertTrue(it.hasNext());
      if (it.hasNext()) {
        st = it.next();
        assertEquals(st.getObject().asNode().getLiteralLexicalForm(), LinkedServiceDataResource
            .getProjectUri(user, projectName));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      close();
    }
  }

  /**
   * Test that Jena RDF model serialization/deserialization works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testSerializationDeserialization() {
    try {
      if (modelAsString != null) {
        // serialize Turtle; deserialize N3
        Model model = LinkedServiceDataResource.deserializeRDFModel(modelAsString,
            HackystatConstants.RESOURCE_URI_BASE, lang);
        assertEquals(modelAsString, LinkedServiceDataResource.serializeRDFModel(model,
            HackystatConstants.RESOURCE_URI_BASE,
            lang.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE : lang));
        // serialize Turtle; deserialize Turtle
        model = LinkedServiceDataResource.deserializeRDFModel(modelAsString,
            HackystatConstants.RESOURCE_URI_BASE,
            lang.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE : lang);
        assertEquals(modelAsString, LinkedServiceDataResource.serializeRDFModel(model,
            HackystatConstants.RESOURCE_URI_BASE,
            lang.equals(HackystatConstants.LANG_N3) ? HackystatConstants.LANG_TURTLE : lang));
      }
      else
        assertTrue(false);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      close();
    }
  }

  /**
   * The following test case has been commented because it requires the admin's e-mail and password
   * to be known for the chosen SensorBase host, in order to get the complete list of registered
   * users. However it's been already successfully run over a test-dedicated machine, then it's
   * granted that GET {host}/users works properly
   **/
  /**
   * Test that GET {host}/users works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testUsersListRDFModel() {
    try {
      /**
       * To get a complete list of users the SensorBase authenticated user must be the administer
       */

      LinkedServiceDataClient lisedClient1 = new LinkedServiceDataClient(admin, admin_password, lang, media,
          true, true);
      lisedClient1.authenticate();
      lisedClient1.clearServerCache();
      lisedClient1.clearLocalCache();

      SensorBaseClient sbClient1 = new SensorBaseClient(getSensorBaseHostName(), admin, admin_password);
      sbClient1.authenticate();
      sbClient1.clearCache();

      // Test query
      Model userModel = lisedClient1.getUsersList();
      // The users list should contain at least the uri of user just registered
      if (userModel == null) {
        assertTrue(false);
        return;
      }
      // LinkedServiceDataResource.printModel(userModel);
      assertTrue(userModel.containsLiteral(null, null, LinkedServiceDataResource.getUserUri(user)));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      close();
    }
  }

  /**
   * The following test case has been commented because it requires the admin's e-mail and password
   * to be known for the chosen SensorBase host, in order to get the complete list of registered
   * users. However it's been already successfully run over a test-dedicated machine, then it's
   * granted that GET {host}/users/sparlq/?query={query} works properly
   **/
  /**
   * Test that GET {host}/users/sparlq/?query={query} works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testUserSparqlEndpoint() {
    try {
      /**
       * To get a complete list of users the SensorBase authenticated user must be the administer
       */
      LinkedServiceDataClient lisedClient1 = new LinkedServiceDataClient(admin, admin_password, lang, media,
          true, true);
      lisedClient1.authenticate();
      lisedClient1.clearServerCache();
      lisedClient1.clearLocalCache();

      SensorBaseClient sbClient1 = new SensorBaseClient(getSensorBaseHostName(), admin, admin_password);
      sbClient1.authenticate();
      sbClient1.clearCache();

      LinkedList<SparqlFilter> filtersList = new LinkedList<SparqlFilter>();
      ObjectProperty predicate = HackystatVocab.AMOUNT_OF_CODE_ISSUES;
      SparqlFilter sf = new SparqlFilter(predicate, SparqlFilter.OP_LESS_THAN, (Double) 3.0);
      filtersList.add(sf);

      predicate = HackystatVocab.BUILD_QUALITY;
      sf = new SparqlFilter(predicate, SparqlFilter.OP_EQUALS_TO, (Double) 0.0);
      filtersList.add(sf);

      predicate = HackystatVocab.COMMIT_FREQUENCY;
      sf = new SparqlFilter(predicate, SparqlFilter.OP_EQUALS_TO, (Double) 0.0);
      filtersList.add(sf);

      UserQuery uq = new UserQuery();
      uq.name = name;
      uq.surname = surname;
      uq.nick = nickname;
      uq.email = user;
      uq.homepage = homepage;
      uq.blog = blog;
      uq.filtersList = filtersList;
      String ret = lisedClient1.searchForUsers(uq);
      System.out.println(ret);
      assertTrue(ret.contains(LinkedServiceDataResource.getUserUri(user)));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      close();
    }
  }

  private void close() {
    try {
      sbClient.deleteSensorData(user);
      sbClient.deleteProject(user, projectName);
      sbClient.deleteUser(user);
    }
    catch (SensorBaseClientException e) {
      e.printStackTrace();
    }
  }
}

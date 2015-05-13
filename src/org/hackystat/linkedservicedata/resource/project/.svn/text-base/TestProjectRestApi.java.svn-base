package org.hackystat.linkedservicedata.resource.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.linkedservicedata.client.LinkedServiceDataClient;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.sparql.ProjectQuery;
import org.hackystat.linkedservicedata.sparql.SparqlFilter;
import org.hackystat.linkedservicedata.test.LinkedServiceDataTestHelper;
import org.hackystat.linkedservicedata.vocabularies.DoapVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.projects.jaxb.Invitations;
import org.hackystat.sensorbase.resource.projects.jaxb.Members;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.Spectators;
import org.hackystat.sensorbase.resource.projects.jaxb.UriPatterns;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Test for the project REST Api-
 *
 * @author Myriam Leggieri.
 *
 */
public class TestProjectRestApi extends LinkedServiceDataTestHelper {

  /** The user for this test case. */
  private static final String user = "holatest@hackystat.org";
  private static final String passwd = "holatest@hackystat.org";
  private static final String[] projectNames = new String[] { "Holatest0", "Holatest1",
      "Holatest2", "Holatest3" };

  private LinkedServiceDataClient lisedClient = null;
  private SensorBaseClient sbClient = null;
  private static final String emacs_tool = "Emacs";
  private static final String ant_tool = "Ant";
  private static final String repository = "https://hackystat-linked-sensor-data.googlecode.com/svn";

  /** Change the following attributes to test different RDF serialization languages **/
  private String lang = HackystatConstants.LANG_NTRIPLE;
  private MediaType media =
  // MediaType.APPLICATION_RDF_XML;
  new MediaType(HackystatConstants.MEDIA_TYPE_NTRIPLES);

  private XMLGregorianCalendar start = Tstamp.makeTimestamp(Day.getInstance(2006, 9, 12));
  private XMLGregorianCalendar end = Tstamp.makeTimestamp(Day.getInstance(2009, 5, 20));
  private XMLGregorianCalendar modified = Tstamp.makeTimestamp(Day.getInstance(2009, 4, 7));

  private String[] progrlangs = new String[] { "Java", "Python" };
  private String[] osystems = new String[] { "Unix", "Windows" };
  private String[] downloadmirrors = new String[] { "http://code.google.com/p/hackystat-linked-sensor-data/downloads/list" };
  private String[] tags = new String[] { "browser", "web" };
  private String[] tools = new String[] { "gate", "wordnet" };
  private String[] bugdatabases = new String[] { "http://pear.php.net/" };
  private String repwebinterface = "http://code.google.com/p/hackystat-linked-sensor-data/source/browse/";
  private String[] wikis = new String[] { "http://code.google.com/p/hackystat-linked-sensor-data/wiki" };
  private String[] invitations = new String[] { "invitatedTest1@hackystat.org",
      "invitatedTest2@hackystat.org", "invitatedTest3@hackystat.org" };
  private String[] spectators = new String[] { "spectatorsTest4@hackystat.org",
      "spectatorsTest5@hackystat.org", "spectatorsTest6@hackystat.org" };
  private String[] members = new String[] { "memberTest7@hackystat.org",
      "memberTest8@hackystat.org", "memberTest9@hackystat.org", user };
  private HashMap<OntProperty, String[]> usersRoles = new HashMap<OntProperty, String[]>();

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

    String pattern = "*/workspace/derbyProva/test/*";
    // send proper project data to the sensorbase to avoid they miss at all.
    Project project = null;
    for (int i = 0; i < projectNames.length; i++) {
      project = new Project();
      project.setOwner(user);
      project.setName(projectNames[i]);
      project.setStartTime(start);
      project.setEndTime(end);
      project.addProperty(HackystatConstants.REPOSITORY_LOCATION, repository);
      project.addProperty(HackystatConstants.REPOSITORY_TYPE, "svn");
      project.addProperty(HackystatConstants.PROJECT_DEVELOPERS, user
          + HackystatConstants.SEPARATOR1_ID + "myriam.leggieri@gmail.com");
      project.addProperty(HackystatConstants.PROJECT_TAGS, tags[0]
          + HackystatConstants.SEPARATOR1_ID + tags[1]);
      project.addProperty(HackystatConstants.PROJECT_TOOLS, tools[0]
          + HackystatConstants.SEPARATOR1_ID + tools[1]);
      project.addProperty(HackystatConstants.PROJECT_WIKI, wikis[0]);
      project.addProperty(HackystatConstants.PROJECT_DOWLOAD_MIRRORS, downloadmirrors[0]);
      project.addProperty(HackystatConstants.LANGUAGE, progrlangs[0]
          + HackystatConstants.SEPARATOR1_ID + progrlangs[1]);
      project.addProperty(HackystatConstants.OPERATING_SYSTEM, osystems[0]
          + HackystatConstants.SEPARATOR1_ID + osystems[1]);
      project.addProperty(HackystatConstants.PROJECT_BUG_DB, bugdatabases[0]);
      project.addProperty(HackystatConstants.REPOSITORY_WEB_INTERFACE, repwebinterface);

      Spectators spects = null;
      List<String> list = null;
      for (int j = 0; j < spectators.length; j++) {
        spects = project.getSpectators();
        if (spects == null)
          spects = new Spectators();
        list = spects.getSpectator();
        if (list == null)
          list = new LinkedList<String>();
        list.add(spectators[j]);
      }
      Invitations invits = null;
      for (int j = 0; j < invitations.length; j++) {
        invits = project.getInvitations();
        if (invits == null)
          invits = new Invitations();
        list = invits.getInvitation();
        if (list == null)
          list = new LinkedList<String>();
        list.add(invitations[j]);
      }
      Members membs = null;
      for (int j = 0; j < members.length; j++) {
        membs = project.getMembers();
        if (membs == null) {
          membs = new Members();
        }
        list = membs.getMember();
        list = invits.getInvitation();
        if (list == null)
          list = new LinkedList<String>();
        list.add(members[j]);
      }

      usersRoles.put(DoapVocab.TESTER, spectators);
      usersRoles.put(DoapVocab.DOCUMENTER, invitations);
      usersRoles.put(DoapVocab.HELPER, members);
      String[] emails = null;
      String proper = null;
      for (OntProperty pred : usersRoles.keySet()) {
        emails = usersRoles.get(pred);
        if (pred.equals(DoapVocab.TRANSLATOR)) {
          proper = "";
          for (int j = 0; j < emails.length; j++) {
            proper += emails[j] + HackystatConstants.SEPARATOR1_ID;
          }
          project.addProperty(HackystatConstants.PROJECT_TRANSLATORS, proper);
        }
        else if (pred.equals(DoapVocab.DOCUMENTER)) {
          proper = "";
          for (int j = 0; j < emails.length; j++) {
            proper += emails[j] + HackystatConstants.SEPARATOR1_ID;
          }
          project.addProperty(HackystatConstants.PROJECT_DOCUMENTERS, proper);
        }
        else if (pred.equals(DoapVocab.DEVELOPER)) {
          proper = "";
          for (int j = 0; j < emails.length; j++) {
            proper += emails[j] + HackystatConstants.SEPARATOR1_ID;
          }
          project.addProperty(HackystatConstants.PROJECT_DEVELOPERS, proper);
        }
        else if (pred.equals(DoapVocab.TESTER)) {
          proper = "";
          for (int j = 0; j < emails.length; j++) {
            proper += emails[j] + HackystatConstants.SEPARATOR1_ID;
          }
          project.addProperty(HackystatConstants.PROJECT_TESTERS, proper);
        }
        else if (pred.equals(DoapVocab.HELPER)) {
          proper = "";
          for (int j = 0; j < emails.length; j++) {
            proper += emails[j] + HackystatConstants.SEPARATOR1_ID;
          }
          project.addProperty(HackystatConstants.PROJECT_HELPERS, proper);
        }
        else if (pred.equals(DoapVocab.MAINTAINER)) {
          proper = "";
          for (int j = 0; j < emails.length; j++) {
            proper += emails[j] + HackystatConstants.SEPARATOR1_ID;
          }
          project.addProperty(HackystatConstants.PROJECT_MAINTAINERS, proper);
        }
      }

      UriPatterns patt = new UriPatterns();
      patt.getUriPattern().add(pattern);
      project.setUriPatterns(patt);
      sbClient.putProject(project);
    }

    // send proper sensor data to the sensorbase to avoid they miss at all.
    SensorData sd = new SensorData();
    for (int i = 0; i < HackystatConstants.sensorDataType_list.length; i++) {
      for (int j = 0; j < 5; j++) {
        sd = new SensorData();
        sd.setOwner(user);
        sd.setTimestamp(modified);
        sd.setResource(pattern);
        sd.setSensorDataType(HackystatConstants.sensorDataType_list[i]);
        switch (i) {
        case 0:
          break;
        case 5:
          break;
        case 10:
          break;
        case 11:
          break;
        case 12:
          break;
        case 14:
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

  }

  /**
   * Test that GET {host}/projects/{owner}/{project} works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testProjectRDFModel() {
    try {
      Model userModel = null;
      for (int i = 0; i < projectNames.length; i++) {
        userModel = lisedClient.getProjectAsLinkedData(user, projectNames[i]);
        if (userModel == null) {
          assertTrue(false);
          return;
        }
        StmtIterator it = userModel.listStatements(null, DoapVocab.MAINTAINER, (RDFNode) null);
        Statement st = null;
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(st.getObject().asNode().getLiteralLexicalForm(), LinkedServiceDataResource
              .getUserUri(user));
        }
        it = userModel.listStatements(null, DoapVocab.NAME, (RDFNode) null);
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(st.getObject().asNode().getLiteralLexicalForm(), projectNames[i]);
        }
        it = userModel.listStatements(null, DoapVocab.CREATED, (RDFNode) null);
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(st.getObject().asNode().getLiteralLexicalForm(), start.toXMLFormat());
        }
        it = userModel.listStatements(null, DoapVocab.LANGUAGE, (RDFNode) null);
        assertTrue(it.hasNext());
        boolean found = false;
        while (it.hasNext()) {
          st = it.next();
          found = false;
          for (int j = 0; j < progrlangs.length; j++) {
            if (LinkedServiceDataResource.getProgrammingLanguageUri(progrlangs[j]).equals(
                st.getObject().asNode().getLiteralLexicalForm())) {
              found = true;
            }
          }
          assertTrue(found);
        }
        it = userModel.listStatements(null, DoapVocab.CATEGORY, (RDFNode) null);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
          st = it.next();
          found = false;
          for (int j = 0; j < tags.length; j++) {
            if (tags[j].equals(st.getObject().asNode().getLiteralLexicalForm())) {
              found = true;
            }
          }
          assertTrue(found);
        }
        it = userModel.listStatements(null, DoapVocab.BUG_DATABASE, (RDFNode) null);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
          st = it.next();
          found = false;
          for (int j = 0; j < bugdatabases.length; j++) {
            if (bugdatabases[j].equals(st.getObject().asNode().getLiteralLexicalForm())) {
              found = true;
            }
          }
          assertTrue(found);
        }
        it = userModel.listStatements(null, DoapVocab.DOWNLOAD_MIRROR, (RDFNode) null);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
          st = it.next();
          found = false;
          for (int j = 0; j < downloadmirrors.length; j++) {
            if (downloadmirrors[j].equals(st.getObject().asNode().getLiteralLexicalForm())) {
              found = true;
            }
          }
          assertTrue(found);
        }
        it = userModel.listStatements(null, HackystatVocab.ON_OPERATING_SYSTEM, (RDFNode) null);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
          st = it.next();
          found = false;
          for (int j = 0; j < osystems.length; j++) {
            if (LinkedServiceDataResource.getOperatingSystemUri(osystems[j]).equals(
                st.getObject().asNode().getLiteralLexicalForm())) {
              found = true;
            }
          }
          assertTrue(found);
        }
        it = userModel.listStatements(null, HackystatVocab.USES_TOOL, (RDFNode) null);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
          st = it.next();
          found = false;
          for (int j = 0; j < tools.length; j++) {
            if (LinkedServiceDataResource.getToolUri(tools[j]).equals(
                st.getObject().asNode().getLiteralLexicalForm())) {
              found = true;
            }
          }
          assertTrue(found);
        }
        /**
         * I've not yet understood how I could set a list of projects' spectators, members and
         * invitations, then the following checks have been commented. it =
         * userModel.listStatements(null, HackystatVocab.SPECTATOR, (RDFNode)null);
         * assertTrue(it.hasNext()); while (it.hasNext()){ st = it.next(); found=false; for (int
         * j=0;j<spectators.length;j++){ if (LinkedServiceDataResource.getUserUri(spectators[j])
         * .equals(st.getObject().asNode().getLiteralLexicalForm())){ found=true; } }
         * assertTrue(found); } it = userModel.listStatements(null, HackystatVocab.MEMBER,
         * (RDFNode)null); assertTrue(it.hasNext()); while (it.hasNext()){ st = it.next();
         * found=false; for (int j=0;j<members.length;j++){ if
         * (LinkedServiceDataResource.getUserUri(members[j])
         * .equals(st.getObject().asNode().getLiteralLexicalForm())){ found=true; } }
         * assertTrue(found); } it = userModel.listStatements(null, HackystatVocab.INVITATED,
         * (RDFNode)null); assertTrue(it.hasNext()); while (it.hasNext()){ st = it.next();
         * found=false; for (int j=0;j<invitations.length;j++){ if
         * (LinkedServiceDataResource.getUserUri(invitations[j])
         * .equals(st.getObject().asNode().getLiteralLexicalForm())){ found=true; } }
         * assertTrue(found); }
         **/
        it = userModel.listStatements(null, DoapVocab.BROWSE, (RDFNode) null);
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(repwebinterface, st.getObject().asNode().getLiteralLexicalForm());
        }
        it = userModel.listStatements(null, DoapVocab.WIKI, (RDFNode) null);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
          st = it.next();
          found = false;
          for (int j = 0; j < wikis.length; j++) {
            if (wikis[j].equals(st.getObject().asNode().getLiteralLexicalForm())) {
              found = true;
            }
          }
          assertTrue(found);
        }
        String[] emails = null;
        for (OntProperty pred : usersRoles.keySet()) {
          emails = usersRoles.get(pred);
          it = userModel.listStatements(null, pred, (RDFNode) null);
          assertTrue(it.hasNext());
          while (it.hasNext()) {
            st = it.next();
            found = false;
            for (int j = 0; j < emails.length && !found; j++) {
              if (LinkedServiceDataResource.getUserUri(emails[j]).equals(
                  st.getObject().asNode().getLiteralLexicalForm())) {
                found = true;
              }
            }
            assertTrue(found);
          }
        }

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
   * The following test case has been commented because it requires the admin's e-mail and password
   * to be known for the chosen SensorBase host, in order to get the complete list of registered
   * users. However it's been already successfully run over a test-dedicated machine, then it's
   * granted that GET {host}/projects works properly
   **/
  /**
   * Test that GET {host}/projects works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testProjectsListRDFModel() {
    try {
      /**
       * To get a complete list of projects the SensorBase authenticated user must be the administer
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
      Model userModel = lisedClient1.getProjectsList();
      // The users list should contain at least the uri of user just registered
      if (userModel == null) {
        assertTrue(false);
        return;
      }
      // LinkedServiceDataResource.printModel(userModel);
      for (int i = 0; i < projectNames.length; i++) {
        assertTrue(userModel.containsLiteral(null, null, LinkedServiceDataResource.getProjectUri(
            user, projectNames[i])));
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
   * Test that GET {host}/projects/{owner} works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testUserProjectsListRDFModel() {
    try {
      // Test query
      Model userModel = lisedClient.getUserProjectsList();
      // The users list should contain at least the uri of user just registered
      if (userModel == null) {
        assertTrue(false);
        return;
      }
      // LinkedServiceDataResource.printModel(userModel);
      for (int i = 0; i < projectNames.length; i++) {
        assertTrue(userModel.containsLiteral(null, null, LinkedServiceDataResource.getProjectUri(
            user, projectNames[i])));
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
   * The following test case has been commented because it requires the admin's e-mail and password
   * to be known for the chosen SensorBase host, in order to get the complete list of registered
   * users. However it's been already successfully run over a test-dedicated machine, then it's
   * granted that GET {host}/users/sparlq/?query={query} works properly
   **/
  /**
   * Test that GET {host}/projects/sparql/?query={query} works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testProjectSparqlEndpoint() {
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
      SparqlFilter sf = new SparqlFilter(predicate, SparqlFilter.OP_LESS_THAN, (Double) 2.5);
      filtersList.add(sf);

      predicate = HackystatVocab.BUILD_QUALITY;
      sf = new SparqlFilter(predicate, SparqlFilter.OP_EQUALS_TO, (Double) 0.0);
      filtersList.add(sf);

      predicate = HackystatVocab.COMMIT_FREQUENCY;
      sf = new SparqlFilter(predicate, SparqlFilter.OP_EQUALS_TO, (Double) 0.0);
      filtersList.add(sf);

      ProjectQuery pq = null;
      for (int i = 0; i < projectNames.length; i++) {
        pq = new ProjectQuery();
        pq.projectname = projectNames[i];
        pq.owner = user;
        pq.start = start.toXMLFormat();
        pq.end = end.toXMLFormat();
        pq.progrlangs = progrlangs;
        pq.osystems = osystems;
        pq.bugdatabases = bugdatabases;
        pq.mirrors = downloadmirrors;
        pq.repwebinterface = repwebinterface;
        pq.wikis = wikis;
        pq.tags = tags;
        pq.tools = tools;
        pq.usersRoles = usersRoles;
        String ret = lisedClient1.searchForProjects(pq);
        assertTrue(ret != null);
        System.out.println(ret);
        assertTrue(ret.contains(LinkedServiceDataResource.getProjectUri(user, projectNames[i])));
      }
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
      for (int i = 0; i < projectNames.length; i++) {
        sbClient.deleteProject(user, projectNames[i]);
      }
      sbClient.deleteUser(user);
    }
    catch (SensorBaseClientException e) {
      e.printStackTrace();
    }
  }
}

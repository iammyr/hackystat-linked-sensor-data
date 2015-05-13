package org.hackystat.linkedservicedata.resource.issue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.linkedservicedata.client.LinkedServiceDataClient;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.sparql.IssueQuery;
import org.hackystat.linkedservicedata.test.LinkedServiceDataTestHelper;
import org.hackystat.linkedservicedata.vocabularies.BaetleVocab;
import org.hackystat.linkedservicedata.vocabularies.CommontagVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.MediaType;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Test the Issue REST Api.
 *
 * @author Myriam Leggieri.
 *
 */
public class TestIssueRestApi extends LinkedServiceDataTestHelper {

  /** The user for this test case. */
  private static final String user = "holatest@hackystat.org";
  private static final String passwd = "holatest@hackystat.org";
  private static final String projectName = "Holatest_Project";

  private LinkedServiceDataClient lisedClient = null;
  private SensorBaseClient sbClient = null;
  private static final String file = "/home/myrtill/Bugzilla/"
      + "linkedservicedata/client/LinkedServiceDataClient.java";

  /** Change the following attributes to test different RDF serialization languages **/
  private String lang = HackystatConstants.LANG_NTRIPLE;
  private MediaType media =
  // MediaType.APPLICATION_RDF_XML;
  new MediaType(HackystatConstants.MEDIA_TYPE_NTRIPLES);

  private String[] tools = new String[] { "Bugzilla", "Jira" };
  private String[] ids = new String[] { "101", "112" };
  private String[] milestones = new String[] { "milestone1", "milestone2" };
  private String[] priorities = new String[] { "high", "low" };
  private String[] status = new String[] { "checked", "closed" };
  private String[] tags = new String[] { "mysql", "gui", "browser" };

  private XMLGregorianCalendar start = Tstamp.makeTimestamp(Day.getInstance(2006, 9, 12));
  private XMLGregorianCalendar end = Tstamp.makeTimestamp(Day.getInstance(2009, 5, 20));
  private XMLGregorianCalendar modified = Tstamp.makeTimestamp(Day.getInstance(2009, 4, 7));

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
    lisedClient.clearAlternativeServerCache();

    sbClient = new SensorBaseClient(getSensorBaseHostName(), user, passwd);
    sbClient.authenticate();
    sbClient.clearCache();

    SensorData sd = null;
    for (int j = 0; j < ids.length; j++) {
      sd = new SensorData();
      sd.setTimestamp(Tstamp.makeTimestamp());
      sd.setOwner(user);
      sd.setResource(file);
      sd.setSensorDataType(HackystatConstants.ISSUE);
      sd.setTool(tools[j]);
      sd.addProperty(HackystatConstants.ISSUE_ID_PROPERTY_KEY, ids[j]);
      sd.addProperty(HackystatConstants.ISSUE_OWNER_PROPERTY_KEY, user);
      sd.addProperty(HackystatConstants.ISSUE_MILESTONE_PROPERTY_KEY, milestones[j]);
      sd.addProperty(HackystatConstants.ISSUE_PRIORITY_PROPERTY_KEY, priorities[j]);
      sd.addProperty(HackystatConstants.ISSUE_STATUS_PROPERTY_KEY, status[j]);
      sd.addProperty(HackystatConstants.ISSUE_TYPE_PROPERTY_KEY, "Defect");
      sd.addProperty(HackystatConstants.ISSUE_OPENED_TIME_PROPERTY_KEY, start.toXMLFormat());
      sd.addProperty(HackystatConstants.ISSUE_CLOSED_TIME_PROPERTY_KEY, end.toXMLFormat());
      sd.addProperty(HackystatConstants.ISSUE_MODIFIED_TIME_PROPERTY_KEY, modified.toXMLFormat());
      String str = "";
      for (int i = 0; i < tags.length; i++) {
        str += tags[i] + HackystatConstants.SEPARATOR1_ID;
      }
      sd.addProperty(HackystatConstants.ISSUE_TAGS_LIST, str);
      str = "";
      for (int i = j + 1; i < ids.length; i++) {
        str += ids[i] + HackystatConstants.SEPARATOR1_ID;
      }
      sd.addProperty(HackystatConstants.ISSUE_DUPLICATES_LIST, str);
      sbClient.putSensorData(sd);
    }
  }

  /**
   * The following test case has been commented because it requires the admin's e-mail and password
   * to be known for the chosen SensorBase host, in order to get the complete list of registered
   * users. However it's been already successfully run over a test-dedicated machine, then it's
   * granted that GET {host}/users works properly
   **/
  /**
   * Test that GET {host}/issues/{issueId} works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testIssueRDFModel() {
    try {

      LinkedServiceDataClient lisedClient1 = new LinkedServiceDataClient(admin, admin_password,
          lang, media, true, true);
      lisedClient1.authenticate();
      lisedClient1.clearServerCache();
      lisedClient1.clearLocalCache();

      SensorBaseClient sbClient1 = new SensorBaseClient(getSensorBaseHostName(), admin,
          admin_password);
      sbClient1.authenticate();
      sbClient1.clearCache();

      // Test query
      Model userModel = null;
      StmtIterator it = null;
      String userUri = LinkedServiceDataResource.getUserUri(user), obj = null;
      Statement st = null;
      boolean found = false;
      for (int j = 0; j < ids.length; j++) {
        userModel = lisedClient1.getIssueAsLinkedData(ids[j]);
        // The user resource in that model should be of foaf:Agent type and having
        // the same foaf:mbox as the user's e-mail stored in the sensorbase DB
        if (userModel == null) {
          assertTrue(false);
          return;
        }
        it = userModel.listStatements(null, BaetleVocab.ASSIGNED_TO, (RDFNode) null);
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(st.getObject().asNode().getLiteralLexicalForm(), userUri);
        }
        it = userModel.listStatements(null, BaetleVocab.TARGET_MILESTONE, (RDFNode) null);
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(st.getObject().asNode().getLiteralLexicalForm(), milestones[j]);
        }
        it = userModel.listStatements(null, BaetleVocab.PRIORITY, (RDFNode) null);
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(st.getObject().asNode().getLiteralLexicalForm(), priorities[j]);
        }
        it = userModel.listStatements(null, BaetleVocab.NAME, (RDFNode) null);
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(st.getObject().asNode().getLiteralLexicalForm(), ids[j]);
        }
        it = userModel.listStatements(null, BaetleVocab.STATUS, (RDFNode) null);
        assertTrue(it.hasNext());
        if (it.hasNext()) {
          st = it.next();
          assertEquals(st.getObject().asNode().getLiteralLexicalForm(), status[j]);
        }
        it = userModel.listStatements(null, CommontagVocab.TAGGED, (RDFNode) null);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
          st = it.next();
          found = false;
          obj = st.getObject().asNode().getLiteralLexicalForm();
          for (int i = 0; i < tags.length && !found; i++) {
            if (tags[i].equals(obj)) {
              found = true;
            }
          }
          assertTrue(found);
        }
        it = userModel.listStatements(null, BaetleVocab.DUPLICATE, (RDFNode) null);
        assertTrue(it.hasNext());
        while (it.hasNext()) {
          st = it.next();
          found = false;
          obj = st.getObject().asNode().getLiteralLexicalForm();
          if (!((j + 1) < ids.length)) {
            found = true;
          }
          for (int i = j + 1; i < ids.length && !found; i++) {
            if (LinkedServiceDataResource.getIssueUri(ids[i]).equals(obj)) {
              found = true;
            }
          }
          assertTrue(found);
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
   * granted that GET {host}/users/sparlq/?query={query} works properly
   **/
  /**
   * Test that GET {host}/issues/sparlq?query={query} works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testIssueSparqlEndpoint() {
    try {
      /**
       * To get a complete list of users the SensorBase authenticated user must be the administer
       */
      LinkedServiceDataClient lisedClient1 = new LinkedServiceDataClient(admin, admin_password,
          lang, media, true, true);
      lisedClient1.authenticate();
      lisedClient1.clearServerCache();
      lisedClient1.clearLocalCache();

      SensorBaseClient sbClient1 = new SensorBaseClient(getSensorBaseHostName(), admin,
          admin_password);
      sbClient1.authenticate();
      sbClient1.clearCache();

      IssueQuery iq = new IssueQuery();
      for (int j = 0; j < ids.length; j++) {
        iq.tags = tags;
        iq.id = ids[j];
        iq.type = "Defect";
        iq.created = start.toXMLFormat();
        iq.closed = end.toXMLFormat();
        iq.lastmod = modified.toXMLFormat();
        iq.assignedTo = user;
        iq.status = status[j];
        iq.priority = priorities[j];
        iq.milestone = milestones[j];
        String ret = lisedClient1.searchForIssues(iq);
        System.out.println(ret);
        assertTrue(ret.contains(LinkedServiceDataResource.getIssueUri(ids[j])));
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
      sbClient.deleteProject(user, projectName);
      sbClient.deleteSensorData(user);
      sbClient.deleteUser(user);
    }
    catch (SensorBaseClientException e) {
      e.printStackTrace();
    }
  }

  /**
   * The following test case has been commented because it requires the admin's e-mail and password
   * to be known for the chosen SensorBase host, in order to get the complete list of registered
   * users. However it's been already successfully run over a test-dedicated machine, then it's
   * granted that GET {host}/users works properly
   **/
  /**
   * Test that GET {host}/issues works properly
   *
   * @throws Exception If problems occur.
   */
  @Test
  public void testIssuesListRDFModel() {
    try {
      /**
       * To get a complete list of users the SensorBase authenticated user must be the administer
       */

      LinkedServiceDataClient lisedClient1 = new LinkedServiceDataClient(admin, admin_password,
          lang, media, true, true);
      lisedClient1.authenticate();
      lisedClient1.clearServerCache();
      lisedClient1.clearLocalCache();

      SensorBaseClient sbClient1 = new SensorBaseClient(getSensorBaseHostName(), admin,
          admin_password);
      sbClient1.authenticate();
      sbClient1.clearCache();

      // Test query
      Model userModel = lisedClient1.getIssuesList();
      // The users list should contain at least the uri of user just registered
      if (userModel == null) {
        assertTrue(false);
        return;
      }
      // LinkedServiceDataResource.printModel(userModel);
      for (int j = 0; j < ids.length; j++) {
        assertTrue(userModel.containsLiteral(null, null, LinkedServiceDataResource
            .getIssueUri(ids[j])));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      close();
    }
  }
}

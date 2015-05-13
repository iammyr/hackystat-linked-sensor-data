package org.hackystat.linkedservicedata.resource.user;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.linkedservicedata.externalDatasets.FoafSearchManager;
import org.hackystat.linkedservicedata.externalDatasets.HackystatLiSeDManager;
import org.hackystat.linkedservicedata.externalDatasets.OhlohApiManager;
import org.hackystat.linkedservicedata.externalDatasets.SindiceApiManager;
import org.hackystat.linkedservicedata.externalDatasets.SioclogManager;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.DoapVocab;
import org.hackystat.linkedservicedata.vocabularies.FoafVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.linkedservicedata.vocabularies.SiocVocab;
import org.hackystat.sensorbase.client.SensorBaseClient;
import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.ProjectIndex;
import org.hackystat.sensorbase.resource.projects.jaxb.ProjectRef;
import org.hackystat.sensorbase.resource.projects.jaxb.Properties;
import org.hackystat.sensorbase.resource.projects.jaxb.Property;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorData;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataIndex;
import org.hackystat.sensorbase.resource.sensordata.jaxb.SensorDataRef;
import org.hackystat.sensorbase.resource.users.jaxb.User;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.client.TelemetryClientException;
import org.hackystat.utilities.tstamp.Tstamp;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Construct a user resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LiSeDUserResource extends LinkedServiceDataResource {
  private LinkedList<XMLGregorianCalendar[]> startEndsOfAccessibleProjects = null;
  private static final String CUMULATIVE_PARAM = ",false";
  private String member = uriUser;
  private String nickname = null;
  private String homePage = null;
  private String weBlog = null;
  private String name = null;
  private String surname = null;
  private double averageDevTime = -1;
  private double commitFreq = -1;
  private double buildRatio = -1;
  private double issueAverage = -1;
  private double unitTestRatio = -1;
  private int numPosts = -1;

  public LiSeDUserResource(Context context, Request request, Response response) {
    super(context, request, response);
  }

  @Override
  public Representation represent(Variant variant) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Creates and returns a RDF Model instance.
   *
   * @param userObj the User instance containing user data to be added to the model
   * @param additionalCachedInfo state if the user wants to retrieve all the cached data in addition
   * to the required user data
   * @return The RDF Model.
   * @throws Exception
   */
  protected Resource makeUserData(Model m_returned, User userObj, SensorBaseClient client,
      String memberCommitment, LinkedList<String> predicates) throws Exception {
    this.member = memberCommitment;
    Resource user_resource = createUserResource(m_returned, userObj.getEmail(), client);
    user_resource = addUserData(user_resource, userObj, client, m_returned, predicates);
    if (predicates.contains(RDFS.seeAlso.getLocalName())
        || predicates.contains(OWL.sameAs.getLocalName()) || predicates.isEmpty()) {
      user_resource = addUserLinkedData(user_resource, userObj);
    }
    user_resource.addProperty(m_returned
        .createProperty("http://dublincore.org/documents/dcmi-terms/#terms-isPartOf"),
        HackystatConstants.voIDURI);
    return user_resource;
  }

  /**
   *
   * @param resource
   * @param userObj
   * @return
   */
  public Resource addUserLinkedData(Resource resource, User userObj) {
    String[] similarUsers = null;

    // Ohloh
    similarUsers = OhlohApiManager.getOhlohUserDataByEmail(userObj.getEmail());
    if (similarUsers != null) {
      for (int i = 0; i < similarUsers.length; i++) {
        resource
            .addProperty(OWL.sameAs, HackystatConstants.RDFOHLOH_USER_PATTERN + similarUsers[i]);
      }
    }
    else // because searching for possible 'sameAs' Ohloh users has no
    // sense if the sameAs Ohloh user has already been found
    if (nickname != null) {
      similarUsers = OhlohApiManager.getOhlohUserDataByNickname(OhlohApiManager.OHLOH_API_KEY,
          nickname);
      if (similarUsers != null) {
        for (int i = 0; i < similarUsers.length; i++) {
          resource.addProperty(RDFS.seeAlso, HackystatConstants.RDFOHLOH_USER_PATTERN
              + similarUsers[i]);
        }
      }
    }
    // User Info from SiocLog
    if (nickname != null) {
      String siocLogAccount = SioclogManager.getLinkableUri(nickname);
      if (siocLogAccount != null) {
        resource.addProperty(RDFS.seeAlso, siocLogAccount);
      }
    }
    // FOAF-QDOS
    String foafUri = FoafSearchManager.findFoafOnQdosByIFP(userObj.getEmail());
    if (foafUri == null) {
      if (homePage != null)
        foafUri = FoafSearchManager.findFoafOnQdosByIFP(homePage);
      if (foafUri == null && weBlog != null) {
        foafUri = FoafSearchManager.findFoafOnQdosByIFP(weBlog);
      }
    }
    if (foafUri != null) {
      resource.addProperty(OWL.sameAs, foafUri);
    }
    else {
      // FOAF-SINDICE
      foafUri = SindiceApiManager.findFoafOnSindice(userObj.getEmail(), homePage, weBlog, name,
          surname);
      if (foafUri != null) {
        resource.addProperty(RDFS.seeAlso, foafUri);
      }
    }
    // Hackystat servers
    HackystatLiSeDManager hackman = new HackystatLiSeDManager();
    similarUsers = hackman.getSeeAlsoLinkableUsers(userObj.getEmail(), homePage, weBlog, name,
        surname);
    if (similarUsers != null) {
      for (int i = 0; i < similarUsers.length; i++) {
        resource.addProperty(RDFS.seeAlso, similarUsers[i]);
      }
    }
    return resource;
  }

  /**
   *
   * @param user_resource
   * @param userObj
   * @param client
   * @param model
   * @param predicates
   * @return
   * @throws SensorBaseClientException
   * @throws TelemetryClientException
   */
  @SuppressWarnings("unchecked")
  public Resource addUserData(Resource user_resource, User userObj, SensorBaseClient client,
      Model model, LinkedList<String> predicates) throws SensorBaseClientException,
      TelemetryClientException {
    String email = userObj.getEmail();
    if (predicates.contains(FoafVocab.MBOX.getLocalName()) || predicates.isEmpty()) {
      user_resource.addProperty(FoafVocab.MBOX, email);
    }
    /** ALWAYS EXECUTED **/
    org.hackystat.sensorbase.resource.users.jaxb.Property prop = getUserProperty(
        HackystatConstants.NICKNAME, userObj);
    if (prop != null) {
      nickname = prop.getValue();
      if (predicates.contains(FoafVocab.NICK.getLocalName()) || predicates.isEmpty()) {
        user_resource.addProperty(FoafVocab.NICK, nickname);
      }
    }
    prop = getUserProperty(HackystatConstants.HOMEPAGE, userObj);
    if (prop != null) {
      homePage = prop.getValue();
      if (predicates.contains(FoafVocab.HOMEPAGE.getLocalName()) || predicates.isEmpty()) {
        user_resource.addProperty(FoafVocab.HOMEPAGE, homePage);
      }
    }
    prop = getUserProperty(HackystatConstants.WEBLOG, userObj);
    if (prop != null) {
      weBlog = prop.getValue();
      if (predicates.contains(FoafVocab.WEBLOG.getLocalName()) || predicates.isEmpty()) {
        user_resource.addProperty(FoafVocab.WEBLOG, weBlog);
      }
    }
    prop = getUserProperty(HackystatConstants.NAME, userObj);
    if (prop != null) {
      name = prop.getValue();
      if (predicates.contains(FoafVocab.FIRST_NAME.getLocalName()) || predicates.isEmpty()) {
        user_resource.addProperty(FoafVocab.FIRST_NAME, name);
      }
    }
    prop = getUserProperty(HackystatConstants.SURNAME, userObj);
    if (prop != null) {
      surname = prop.getValue();
      if (predicates.contains(FoafVocab.SURNAME.getLocalName()) || predicates.isEmpty()) {
        user_resource.addProperty(FoafVocab.SURNAME, surname);
      }
    }
    /** ----END---- **/

    if (predicates.contains(DoapVocab.MAINTAINER.getLocalName()) || predicates.isEmpty()) {
      // links to projects in which the user is actively involved
      // checking always if the project to be link is accessible accordingly to the privacy setting
      user_resource = addRelatedProjectsLinks(user_resource, email);
    }


    Resource res = null;
    if (nickname != null) {
      Object[] resp = SioclogManager.getUserCommunicationData(nickname);
      if (resp != null && resp.length == 2) {
        if (resp[0] != null) {
          if (predicates.contains(SiocVocab.SUBSCRIBER_OF.getLocalName()) || predicates.isEmpty()) {
            LinkedList<String> channels = (LinkedList<String>) resp[0];
            for (String str : channels) {
              res = model.createResource(str);
              user_resource.addProperty(SiocVocab.SUBSCRIBER_OF, res);
            }
          }
        }
        if (resp[1] != null) {
          if (predicates.contains(HackystatVocab.COMMUNICATION_EFFORT.getLocalName())
              || predicates.isEmpty()) {
            numPosts = Integer.valueOf(String.valueOf(resp[1]));
            /**
             * THERE SHOULD BE SOME CALCULUS OVER POSTS SENT TO SEVERAL COMMUNICATION TOOLS IN ORDER
             * TO GET A QUALITY VALUE
             **/
            user_resource
                .addProperty(HackystatVocab.COMMUNICATION_EFFORT, String.valueOf(numPosts));
          }
        }
      }
      if (isSDAccessible(userObj)) {
        if (predicates.contains(HackystatVocab.KNOWS.getLocalName()) || predicates.isEmpty()) {
          // links to tools and objects related to knowledge
          /**
           * THE ADDKNOWLEDGELINKS CALL HAS BEEN COMMENTED BECAUSE IT TAKES TOO LONG -> fix(work in
           * progress): Substitute Derby DB with CouchDB
           */
          // user_resource=addKnowledgeLinks(user_resource, email);
        }

        // add programming skills values
        user_resource = addProgrammingSkills(user_resource, email, predicates);
      }
      if (predicates.contains(HackystatVocab.PRIVACY_LEVEL.getLocalName()) || predicates.isEmpty()) {
        prop = getUserProperty(HackystatConstants.PROFILE_VISIBILITY, userObj);
        if (prop != null) {
          String visibility = prop.getValue();
          if (predicates.contains(HackystatVocab.PRIVACY_LEVEL.getLocalName())
              || predicates.isEmpty()) {
            user_resource.addProperty(HackystatVocab.PRIVACY_LEVEL, visibility);
          }
        }
      }
    }
    return user_resource;
  }

  /**
   *
   * @param user_resource
   * @param email
   * @param predicates
   * @return
   * @throws TelemetryClientException
   * @throws SensorBaseClientException
   */
  private Resource addProgrammingSkills(Resource user_resource, String email,
      LinkedList<String> predicates) throws TelemetryClientException, SensorBaseClientException {
    TelemetryClient client = getTelemetryClient();
    if (client != null) {
      if (predicates.contains(HackystatVocab.COMMIT_FREQUENCY.getLocalName())
          || predicates.contains(HackystatVocab.DEVELOPMENT_TIME.getLocalName())
          || predicates.isEmpty()) {
        // commitment values
        user_resource = addCommitmentQuantity(user_resource, email, client);
      }
      if (predicates.contains(HackystatVocab.BUILD_QUALITY.getLocalName()) || predicates.isEmpty()) {
        // build success ratio
        user_resource = addBuildRatio(user_resource, email, client);
      }
      if (predicates.contains(HackystatVocab.AMOUNT_OF_CODE_ISSUES.getLocalName())
          || predicates.isEmpty()) {
        // code issue
        user_resource = addCodeIssueQuantity(user_resource, email, client);
      }
      if (predicates.contains(HackystatVocab.UNIT_TEST_QUALITY.getLocalName())
          || predicates.isEmpty()) {
        // unit test success ratio
        user_resource = addUnitTestRatio(user_resource, email, client);
      }
      if (predicates.contains(HackystatVocab.KARMA.getLocalName()) || predicates.isEmpty()) {
        user_resource.addProperty(HackystatVocab.KARMA, calculateKarma());
      }
    }
    return user_resource;
  }

  private String calculateKarma() {
    double karma = 0.0;
    if (this.averageDevTime != -1) {
      karma += averageDevTime;
    }
    if (this.buildRatio != -1) {
      karma += buildRatio;
    }
    if (this.commitFreq != -1) {
      karma += commitFreq;
    }
    if (this.unitTestRatio != -1) {
      karma += unitTestRatio;
    }
    if (this.issueAverage != -1){
      karma += issueAverage;
    }
    if (this.numPosts != -1) {
      karma += numPosts;
    }
    return String.valueOf(karma);
  }

  /**
   *
   * @param user_resource
   * @param email
   * @param client
   * @return
   * @throws SensorBaseClientException
   * @throws TelemetryClientException
   */
  private Resource addBuildRatio(Resource user_resource, String email, TelemetryClient client)
      throws SensorBaseClientException, TelemetryClientException {
    if (client == null) {
      return user_resource;
    }
    if (startEndsOfAccessibleProjects == null || startEndsOfAccessibleProjects.isEmpty()) {
      initStartEndDates(email);
    }
    int success = telemetryValuesCount(client, HackystatConstants.BUILD, member + ",Success,*"
        + CUMULATIVE_PARAM, startEndsOfAccessibleProjects), failure = telemetryValuesCount(client,
        HackystatConstants.BUILD, member + ",Failure,*" + CUMULATIVE_PARAM,
        startEndsOfAccessibleProjects);
    if (failure != 0) {
      buildRatio = success / failure;
    }
    else {
      buildRatio = 0.0;
    }
    user_resource.addProperty(HackystatVocab.BUILD_QUALITY, String.valueOf(buildRatio));
    return user_resource;
  }

  /**
   *
   * @param user_resource
   * @param email
   * @param client
   * @return
   * @throws SensorBaseClientException
   * @throws TelemetryClientException
   */
  private Resource addUnitTestRatio(Resource user_resource, String email, TelemetryClient client)
      throws SensorBaseClientException, TelemetryClientException {
    if (client == null) {
      return user_resource;
    }
    if (startEndsOfAccessibleProjects == null || startEndsOfAccessibleProjects.isEmpty()) {
      initStartEndDates(email);
    }
    int success = telemetryValuesCount(client, HackystatConstants.UNIT_TEST, "SuccessCount,"
        + member + CUMULATIVE_PARAM, startEndsOfAccessibleProjects), failure = telemetryValuesCount(
        client, HackystatConstants.UNIT_TEST, "FailureCount," + member + CUMULATIVE_PARAM,
        startEndsOfAccessibleProjects);
    if (failure != 0) {
      unitTestRatio = success / failure;
    }
    else {
      unitTestRatio = 0.0;
    }
    user_resource.addProperty(HackystatVocab.UNIT_TEST_QUALITY, String.valueOf(unitTestRatio));
    return user_resource;
  }

  /**
   *
   * @param user_resource
   * @param email
   * @param client
   * @return
   * @throws SensorBaseClientException
   * @throws TelemetryClientException
   */
  private Resource addCodeIssueQuantity(Resource user_resource, String email, TelemetryClient client)
      throws SensorBaseClientException, TelemetryClientException {
    if (client == null) {
      return user_resource;
    }
    if (startEndsOfAccessibleProjects == null || startEndsOfAccessibleProjects.isEmpty()) {
      initStartEndDates(email);
    }
    issueAverage = telemetryValuesAverage(client, HackystatConstants.CODE_ISSUE, "*,*",
        startEndsOfAccessibleProjects);
    user_resource.addProperty(HackystatVocab.AMOUNT_OF_CODE_ISSUES, String.valueOf(issueAverage));

    return user_resource;
  }

  /**
   *
   * @param user_resource
   * @param email
   * @param client
   * @return
   * @throws SensorBaseClientException
   * @throws TelemetryClientException
   */
  private Resource addCommitmentQuantity(Resource user_resource, String email,
      TelemetryClient client) throws SensorBaseClientException, TelemetryClientException {
    if (client == null) {
      return user_resource;
    }
    if (startEndsOfAccessibleProjects == null || startEndsOfAccessibleProjects.isEmpty()) {
      initStartEndDates(email);
    }
    // development time
    averageDevTime = telemetryValuesAverage(client, HackystatConstants.DEVTIME, member
        + CUMULATIVE_PARAM, startEndsOfAccessibleProjects);
    user_resource.addProperty(HackystatVocab.DEVELOPMENT_TIME, String.valueOf(averageDevTime));
    // commit
    int tot = 0, count = 0;
    HashMap<XMLGregorianCalendar, Integer> intValues = null;
    for (XMLGregorianCalendar[] dates : startEndsOfAccessibleProjects) {
      intValues = getIntegerTimeValuesPerTimePeriod(client, dates[0], dates[1], uriUser,
          projectName, HackystatConstants.COMMIT, member + CUMULATIVE_PARAM,
          HackystatConstants.MONTH);
      for (XMLGregorianCalendar month : intValues.keySet()) {
        tot += intValues.get(month);
      }
      count += intValues.size();
    }
    if (count != 0) {
      commitFreq = tot / count;
    }
    else {
      commitFreq = 0.0;
    }
    user_resource.addProperty(HackystatVocab.COMMIT_FREQUENCY, String.valueOf(commitFreq));

    return user_resource;
  }

  /**
   * Get the period within the last year which is included between the projects start and end dates.
   *
   * @param email user's email.
   * @throws SensorBaseClientException if something goes wrong.
   */
  private void initStartEndDates(String email) throws SensorBaseClientException {
    SensorBaseClient client = getSensorBaseClient();
    ProjectIndex pind = client.getProjectIndex(email);
    List<ProjectRef> listpref = pind.getProjectRef();
    if (startEndsOfAccessibleProjects == null) {
      startEndsOfAccessibleProjects = new LinkedList<XMLGregorianCalendar[]>();
    }
    XMLGregorianCalendar now = Tstamp.makeTimestamp();
    if (listpref != null) {
      Project proj = null;
      XMLGregorianCalendar[] startEndDates = null;
      for (ProjectRef pref : listpref) {
        proj = client.getProject(pref);
        if (isProjectProfileAccessible(proj)) {
          // if the user is 'actively' involved in
          if (proj.getMembers().getMember().contains(email) || proj.getOwner().equals(email)) {
            if (granularity == null) {
              granularity = HackystatConstants.MONTH;
            }

            startEndDates = adjustStartEndDates((XMLGregorianCalendar) proj.getStartTime().clone(),
                (XMLGregorianCalendar) proj.getEndTime().clone(), granularity);
            if (startEndDates != null && startEndDates.length == 2 && startEndDates[0] != null
                && startEndDates[1] != null) {
              if (startEndDates[0].getYear() <= now.getYear() - 1
                  && (startEndDates[1].getYear() == now.getYear() || startEndDates[1].getYear() == now
                      .getYear() - 1)) {
                startEndDates[0].setDay(1);
                startEndDates[0].setMonth(1);
                startEndDates[0].setYear(now.getYear() - 1);
                startEndsOfAccessibleProjects.add(startEndDates);
              }
            }
          }
        }
      }
    }
  }

  /**
   *
   * @param user_resource
   * @param email
   * @return
   * @throws SensorBaseClientException
   */
  @SuppressWarnings("unused")
  private Resource addKnowledgeLinks(Resource user_resource, String email)
      throws SensorBaseClientException {
    SensorBaseClient client = getSensorBaseClient();
    SensorDataIndex sdind = client.getSensorDataIndex(email);
    List<SensorDataRef> listsref = sdind.getSensorDataRef();
    List<String> knowledgeUriList = new LinkedList<String>();
    List<String> machineUriList = new LinkedList<String>();
    List<String> osUriList = new LinkedList<String>();
    List<String> commandUriList = new LinkedList<String>();
    List<String> languageUriList = new LinkedList<String>();
    if (listsref != null) {
      Iterator<SensorDataRef> it = listsref.iterator();
      SensorDataRef sdref = null;
      SensorData sd = null;
      org.hackystat.sensorbase.resource.sensordata.jaxb.Properties props = null;
      List<org.hackystat.sensorbase.resource.sensordata.jaxb.Property> proplist = null;
      Iterator<org.hackystat.sensorbase.resource.sensordata.jaxb.Property> itprop = null;
      org.hackystat.sensorbase.resource.sensordata.jaxb.Property prop = null;
      String key = null, value = null;
      String tmp = null;
      while (it.hasNext()) {
        sdref = it.next();
        sd = client.getSensorData(sdref);
        if (!knowledgeUriList.contains(sd.getTool())) {
          knowledgeUriList.add(sd.getTool());
        }

        props = sd.getProperties();
        if (props != null) {
          proplist = props.getProperty();
          if (proplist != null) {
            itprop = proplist.iterator();
            while (itprop.hasNext()) {
              prop = itprop.next();
              key = prop.getKey();
              value = prop.getValue();
              if (key.equals(HackystatConstants.MACHINE)) {
                tmp = getMachineUri(value);
                if (!machineUriList.contains(tmp)) {
                  machineUriList.add(tmp);
                }
              }
              else if (key.equals(HackystatConstants.OPERATING_SYSTEM)) {
                tmp = getOperatingSystemUri(value);
                if (!osUriList.contains(tmp)) {
                  osUriList.add(tmp);
                }
              }
              else if (key.equals(HackystatConstants.COMMAND)) {
                tmp = getCommandUri(sd.getOwner(), value);
                if (!commandUriList.contains(tmp)) {
                  commandUriList.add(tmp);
                }
              }
              else if (key.equals(HackystatConstants.LANGUAGE)) {
                tmp = getProgrammingLanguageUri(value);
                if (!languageUriList.contains(tmp)) {
                  languageUriList.add(tmp);
                }
              }
            }
          }
        }
      }
    }
    for (String tmp : knowledgeUriList) {
      user_resource.addProperty(HackystatVocab.KNOWS, getToolUri(tmp));
    }
    for (String tmp : commandUriList) {
      user_resource.addProperty(HackystatVocab.KNOWS, getCommandUri(tmp, email));
    }
    for (String tmp : languageUriList) {
      user_resource.addProperty(HackystatVocab.KNOWS, getProgrammingLanguageUri(tmp));
    }
    for (String tmp : osUriList) {
      user_resource.addProperty(HackystatVocab.KNOWS, getOperatingSystemUri(tmp));
    }
    for (String tmp : machineUriList) {
      user_resource.addProperty(HackystatVocab.KNOWS, getMachineUri(tmp));
    }
    return user_resource;
  }

  /**
   *
   * @param user_resource
   * @param email
   * @return
   * @throws SensorBaseClientException
   */
  private Resource addRelatedProjectsLinks(Resource user_resource, String email)
      throws SensorBaseClientException {
    SensorBaseClient client = getSensorBaseClient();
    ProjectIndex pind = client.getProjectIndex(email);
    List<ProjectRef> listpref = pind.getProjectRef();
    List<Property> proplist = null;
    Properties props = null;
    Iterator<Property> itprop = null;
    String key = null, value = null;
    Property prop = null;
    String[] elems = null, subelems = null;
    boolean found = false;
    if (startEndsOfAccessibleProjects == null) {
      startEndsOfAccessibleProjects = new LinkedList<XMLGregorianCalendar[]>();
    }
    if (listpref != null) {
      Project proj = null;
      String project_uri = null;
      XMLGregorianCalendar[] arr = null;
      for (ProjectRef pref : listpref) {
        proj = client.getProject(pref);
        if (isProjectProfileAccessible(proj)
            && !proj.getName().equals(HackystatConstants.DEFAULT_PROJECT)) {
          // if the user is 'actively' involved in
          if (proj.getMembers().getMember().contains(email) || proj.getOwner().equals(email)) {
            project_uri = getProjectUri(proj.getOwner(), proj.getName());
            // find which kind of relationship link the user to this project
            if (proj.getOwner().equals(email)) {
              user_resource.addProperty(DoapVocab.MAINTAINER, getProjectUri(proj.getOwner(), proj
                  .getName()));
            }
            else {
              user_resource = addUserProjectRelationship(project_uri, user_resource, proj, email);
            }
            // find tools used in this project to state the user knows them
            props = proj.getProperties();
            if (props != null) {
              proplist = props.getProperty();
              if (proplist != null) {
                itprop = proplist.iterator();
                found = false;
                while (itprop.hasNext() && !found) {
                  prop = itprop.next();
                  key = prop.getKey();
                  value = prop.getValue();
                  if (key.equals(HackystatConstants.PROJECT_BUG_DB)) {
                    elems = value.split(HackystatConstants.SEPARATOR1_ID);
                    for (int i = 0; i < elems.length; i++) {
                      subelems = elems[i].split(HackystatConstants.SEPARATOR2_ID);
                      if (subelems.length == 2) {
                        user_resource.addProperty(HackystatVocab.KNOWS, getToolUri(subelems[0]));
                      }
                    }
                    found = true;
                  }
                }
              }
            }
            // include this project to the list of project in which the user is
            // or has been involved (ensuring project dates are correct)
            if (granularity == null) {
              granularity = HackystatConstants.MONTH;
            }
            arr = adjustStartEndDates((XMLGregorianCalendar) proj.getStartTime().clone(),
                (XMLGregorianCalendar) proj.getEndTime().clone(), granularity);
            XMLGregorianCalendar now = Tstamp.makeTimestamp();
            if (arr != null && arr.length == 2 && arr[0] != null && arr[1] != null) {
              if (arr[0].getYear() <= now.getYear() - 1
                  && (arr[1].getYear() == now.getYear() || arr[1].getYear() == now.getYear() - 1)) {
                arr[0].setDay(1);
                arr[0].setMonth(1);
                arr[0].setYear(now.getYear() - 1);
                startEndsOfAccessibleProjects.add(arr);
              }
            }
          }
        }
      }
    }
    return user_resource;
  }

  /**
   *
   * @param project_uri
   * @param user_resource
   * @param proj
   * @param useremail
   * @return
   */
  public static Resource addUserProjectRelationship(String project_uri, Resource user_resource,
      Project proj, String useremail) {
    Properties props = proj.getProperties();
    boolean found = false;
    if (props != null) {
      List<Property> listprop = props.getProperty();
      if (listprop != null) {
        String key = null, value = null;
        Property prop = null;
        Iterator<Property> it = listprop.iterator();
        while (it.hasNext()) {
          prop = it.next();
          key = prop.getKey();
          value = prop.getValue();
          if (key.equals(HackystatConstants.PROJECT_DEVELOPERS)) {
            if (value.contains(useremail)) {
              found = true;
              user_resource = user_resource.addProperty(HackystatVocab.DEVELOPER, project_uri);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_MAINTAINERS)) {
            if (value.contains(useremail)) {
              found = true;
              user_resource = user_resource.addProperty(HackystatVocab.MAINTAINER, project_uri);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_TESTERS)) {
            if (value.contains(useremail)) {
              found = true;
              user_resource = user_resource.addProperty(HackystatVocab.TESTER, project_uri);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_HELPERS)) {
            if (value.contains(useremail)) {
              found = true;
              user_resource = user_resource.addProperty(HackystatVocab.HELPER, project_uri);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_DOCUMENTERS)) {
            if (value.contains(useremail)) {
              found = true;
              user_resource = user_resource.addProperty(HackystatVocab.DOCUMENTER, project_uri);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_TRANSLATORS)) {
            if (value.contains(useremail)) {
              found = true;
              user_resource = user_resource.addProperty(HackystatVocab.TRANSLATOR, project_uri);
            }
          }
        }
      }
    }
    if (!found) {
      user_resource = user_resource.addProperty(HackystatVocab.DEVELOPER, project_uri);
    }
    return user_resource;
  }

  /**
   *
   * @param user
   * @param role
   * @return
   */
  public static Resource addUserType(Resource user, String role) {
    if (role == null) {
      user.addProperty(RDF.type, FoafVocab.AGENT);
    }
    else {
      if (role.equals(HackystatConstants.ROLE_DEVELOPER)) {
        user.addProperty(RDF.type, HackystatVocab.DEVELOPER);
      }
      else if (role.equals(HackystatConstants.ROLE_PROJECTMAN)) {
        user.addProperty(RDF.type, HackystatVocab.PROJECT_MANAGER);
      }
      else {
        user.addProperty(RDF.type, FoafVocab.PERSON);
      }
    }
    return user;
  }

}

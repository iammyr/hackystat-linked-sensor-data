package org.hackystat.linkedservicedata.vocabularies;

import java.util.HashMap;
import java.util.LinkedList;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.server.ServerProperties;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * Hackystat constants.
 *
 * @author Myriam Leggieri.
 *
 */
public class HackystatConstants {
  public static HashMap<String, String[]> hackystatLisedServers_list = null;
  public static String hackystatLisedServers_serializedObj = "associatedLiSeDServers.ser";
  public static LinkedList<String> noLoop_toPropagate = new LinkedList<String>();

  /** Vocabulary of Interlinked Data used to describe the Hackystat dataset. */
  public static final Model voIDModel = LinkedServiceDataResource.initVoIDModel();
  /** VoID dataset description URI. */
  public static final String voIDURI = HackystatVocab.NS + "void";

  public static final String SEPARATOR1_ID = " ";
  public static final String SEPARATOR2_ID = "__";
  public static final String SLASH = "/";
  public static final String CUMULATIVE_PARAM = ",false";

  public static final String DEVTIME = "DevTime";
  public static final String UNIT_TEST = "UnitTest";
  public static final String MONTH = "Month";
  public static final String DAY = "Day";
  public static final String CODE_ISSUE = "CodeIssue";
  public static final String COMMIT = "Commit";
  public static final String COVERAGE = "Coverage";
  public static final String BUILD = "Build";

  public static final String ISSUE = "Issue";

  public static final String PROFILE_VISIBILITY = "profileVisibility";
  public static final String SENSORDATA_VISIBILITY = "sensorDataVisibility";

  public static final String[] SKILLS_LIST = new String[] {
      HackystatVocab.AMOUNT_OF_CODE_ISSUES.getLocalName(),
      HackystatVocab.BUILD_QUALITY.getLocalName(), HackystatVocab.COMMIT_FREQUENCY.getLocalName(),
      HackystatVocab.DEVELOPMENT_TIME.getLocalName(),
      HackystatVocab.COMMUNICATION_EFFORT.getLocalName() };

  public static final String[] ROLES_LIST = new String[] { DoapVocab.DEVELOPER.getLocalName(),
      DoapVocab.DOCUMENTER.getLocalName(), DoapVocab.MAINTAINER.getLocalName(),
      DoapVocab.TESTER.getLocalName(), DoapVocab.TRANSLATOR.getLocalName(),
      DoapVocab.HELPER.getLocalName() };

  /**
   * Some resource types are not associatable with a Hackystat user. Then they're stored in cache
   * grouped by the following generic identifier 'others' rather than by user's email
   */
  public static final String otherResourceID = "others";

  public static final String ROLE_DEVELOPER = "Developer";

  public static final String ROLE_PROJECTMAN = "ProjectManager";

  public static final String ROLE_REVIEWER = "Reviewer";

  public static final String ROLE_DOCUMENTER = "Documenter";

  public static final String ROLE_TRANSLATOR = "Translator";

  /**
   * I have to suppose there are a limited set of SDT because I must define specific properties and
   * classes for them (the potential SDTs are too different to allow the creation of a set of
   * properties suitable for all of them). When a not foreseen SDT will be created, it will be
   * represented only with its basic information within the RDF model (using, this time, a little
   * set of generic properties).
   */
  public static final String[] sensorDataType_list = new String[] {
  // 0
      "Commit",
      // 1
      "FileMetric",
      // 2
      "DevEvent",
      // 3
      "ReviewIssue",
      // 4
      "ReviewActivity",
      // 5
      "CodeIssue",
      // 6
      "Activity",
      // 7
      "BufferTransition",
      // 8
      "Dependency",
      // 9
      "Cli",
      // 10
      "Build",
      // 11
      "UnitTest",
      // 12
      "Coverage",
      // 13
      "Perf",
      // 14
      "Issue" };

  public static final String LANG_N3 = "N3";
  public static final String LANG_TURTLE = "TURTLE";
  public static final String LANG_RDFXML = "RDF/XML";
  public static final String LANG_NTRIPLE = "N-TRIPLE";
  public static final String LANG_RDFXML_ABBREV = "RDF/XML-ABBREV";

  /** as stated at: http://www.w3.org/2008/01/rdf-media-types **/
  public static final String MEDIA_TYPE_TURTLE = "application/x-turtle";
  public static final String MEDIA_TYPE_NTRIPLES = "text/plain";
  public static final String MEDIA_TYPE_SPARQL_RESULTS = "application/sparql-results+xml";

  public static final String RESOURCE_URI_BASE = "http://"
      + System.getProperty(ServerProperties.HOSTNAME_KEY) + ":"
      + System.getProperty(ServerProperties.PORT_KEY) + "/"
      + System.getProperty(ServerProperties.CONTEXT_ROOT_KEY) + "/";

  public static final String USER_RESOURCE_TYPE = "users/";
  public static final String PROJECT_RESOURCE_TYPE = "projects/";
  public static final String ISSUE_RESOURCE_TYPE = "issues/";

  public static final String USER_SPARQL_RESOURCE_TYPE = "users/sparql?query=";
  public static final String PROJECT_SPARQL_RESOURCE_TYPE = "projects/sparql?query=";
  public static final String ISSUE_SPARQL_RESOURCE_TYPE = "issues/sparql?query=";

  public static final String PING_RESOURCE_TYPE = "ping";
  public static final String NETWORK_RESOURCE_TYPE = "network/";
  public static final String REPOSITORY_RESOURCE_TYPE = "repository/";
  public static final String LANGUAGE_RESOURCE_TYPE = "programmingLanguage/";
  public static final String OPERATINGSYSTEM_RESOURCE_TYPE = "operatingSystem/";
  public static final String MACHINE_RESOURCE_TYPE = "machine/";
  public static final String COMMAND_RESOURCE_TYPE = "command/";
  public static final String SWDEVELOPMENTPHASE_RESOURCE_TYPE = "devPhase/";
  public static final String TOOL_RESOURCE_TYPE = "tool/";

  public static final String NICKNAME = "nickName";
  public static final String HOMEPAGE = "homepage";
  public static final String WEBLOG = "weblog";
  public static final String NAME = "name";
  public static final String SURNAME = "surname";

  public static final String DEFAULT_PROJECT = "Default";

  /** property key of ID. */
  public static final String ISSUE_ID_PROPERTY_KEY = "IssueId";
  /** property key of TYPE. */
  public static final String ISSUE_TYPE_PROPERTY_KEY = "Type";
  /** property key of STATUS. */
  public static final String ISSUE_STATUS_PROPERTY_KEY = "Status";
  /** property key of PRIORITY. */
  public static final String ISSUE_PRIORITY_PROPERTY_KEY = "Priority";
  /** property key of MILESTONE. */
  public static final String ISSUE_MILESTONE_PROPERTY_KEY = "Milestone";
  /** property key of OWNER. */
  public static final String ISSUE_OWNER_PROPERTY_KEY = "Owner";
  /**
   * properties added by me until I don't have the Hackystat version in which the following
   * properties are stored as attributes and not in the property list
   */
  public static final String ISSUE_OPENED_TIME_PROPERTY_KEY = "OpenedTime";
  public static final String ISSUE_CLOSED_TIME_PROPERTY_KEY = "ClosedTime";
  public static final String ISSUE_MODIFIED_TIME_PROPERTY_KEY = "ModifiedTime";

  // Project
  public static final String REPOSITORY_LOCATION = "repositoryLocation";
  public static final String REPOSITORY_TYPE = "repositoryType";
  public static final String REPOSITORY_ANON_ROOT = "repositoryAnonymousRoot";
  public static final String REPOSITORY_WEB_INTERFACE = "repositoryWebInterface";
  public static final String PROJECT_DOWNLOAD_PAGE = "downloadPage";
  public static final String PROJECT_WIKI = "wiki";
  public static final String PROJECT_DOWLOAD_MIRRORS = "mirrorsList";
  public static final String PROJECT_BUG_DB = "bugDatabase";
  public static final String PROJECT_DEVELOPERS = "developersList";
  public static final String PROJECT_DOCUMENTERS = "documentersList";
  public static final String PROJECT_MAINTAINERS = "maintainersList";
  public static final String PROJECT_TRANSLATORS = "translatorsList";
  public static final String PROJECT_TESTERS = "testersList";
  public static final String PROJECT_HELPERS = "helpersList";
  public static final String PROJECT_PHASES = "phases";
  public static final String PROJECT_TAGS = "tagsList";
  public static final String PROJECT_TOOLS = "toolsList";
  public static final String PROJECT_TEST_PURPOSE = "testIdPurpose";
  public static final String NEXT_PHASES = "nextPhases";
  public static final String POTENTIAL_ISSUES = "issueItems";
  public static final String INVOLVED_FILES = "fileItems";

  /** external datasets **/
  public static final String RDFOHLOH_PROJECT_PATTERN = "http://rdfohloh.wikier.org/project/";
  public static final String RDFOHLOH_USER_PATTERN = "http://rdfohloh.wikier.org/user/";
  public static final String HACKYSTATLISED_SPARQL_PROJECT_ENDPOINT = "projects/sparql/?query=";

  public static final String SIOCLOG_PROJECT_PATTERN = "http://irc.sioc-project.org/users/";

  public static final String FOAFQDOS_PATTERN = "http://foaf.qdos.com/sameas/reverse?path=";
  public static final String SINDICE_BASE_URI = "http://sindice.com/vocab/fields#";
  public static final String SINDICE_PATTERN = "http://sindice.com/api/v2/search?page=1&q=";
  public static final String PTSW_BASE_URI = "http://pingthesemanticweb.com/rest/?url=";
  /** The preferred representation type. */
  public static final Preference<MediaType> rdfMedia = new Preference<MediaType>(
      MediaType.TEXT_RDF_N3);

  // Development Phase
  public static final String INVOLVED_USERS = "involvedUsers";
  // Cli
  public static final String COMMAND_ARGUMENTS = "arguments";
  public static final String COMMAND_RESULT = "result";

  public static final String OPERATING_SYSTEM = "operatingSystem";
  public static final String MACHINE = "machine";
  public static final String COMMAND = "command";
  public static final String CLI = "Cli";
  public static final String LANGUAGE = "programmingLanguage";

  public static final String ISSUE_REPORTER = "resporte";
  public static final String ISSUE_INTERESTED = "interested";
  public static final String ISSUE_COMMENTS = "comments";
  public static final String ISSUE_DEPENDS_ON = "dependsOn";
  public static final String ISSUE_CAUSES = "causes";
  public static final String ISSUE_OPERATING_SYSTEM = "operatingSystem";
  public static final String ISSUE_ENVIRONMENT = "environment";
  public static final String ISSUE_VOTES = "votes";
  public static final String ISSUE_RESOLVED_WITH = "resolvedWith";
  public static final String ISSUE_DUPLICATES_LIST = "duplicatesList";
  public static final String ISSUE_TAGS_LIST = "tagsList";
  // baetle issue types
  public static final String BUG = "bug";
  public static final String DEFECT = "defect";
  public static final String ENHANCEMENT = "enhancement";

  public static final String ISSUE_SUMMARY = "summary";

  public static final String PROJECT_FROM = "project";

}

package org.hackystat.linkedservicedata.resource.project;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.linkedservicedata.externalDatasets.HackystatLiSeDManager;
import org.hackystat.linkedservicedata.externalDatasets.OhlohApiManager;
import org.hackystat.linkedservicedata.resource.linkedservicedata.LinkedServiceDataResource;
import org.hackystat.linkedservicedata.vocabularies.DoapVocab;
import org.hackystat.linkedservicedata.vocabularies.HackystatConstants;
import org.hackystat.linkedservicedata.vocabularies.HackystatVocab;
import org.hackystat.sensorbase.resource.projects.jaxb.Project;
import org.hackystat.sensorbase.resource.projects.jaxb.Properties;
import org.hackystat.sensorbase.resource.projects.jaxb.Property;
import org.hackystat.telemetry.service.client.TelemetryClient;
import org.hackystat.telemetry.service.client.TelemetryClientException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Construct a project resource.
 *
 * @author Myriam Leggieri.
 *
 */
public class LiSeDProjectResource extends LinkedServiceDataResource {

  private String[] tags = null;
  private String[] tools = null;
  protected boolean uncompleteQuery = false;

  public LiSeDProjectResource(Context context, Request request, Response response)
      throws UnsupportedEncodingException {
    super(context, request, response);
  }

  @Override
  public Representation represent(Variant variant) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   *
   * @param m_returned
   * @param projObj
   * @param predicates
   * @return
   * @throws Exception
   */
  protected Resource makeProjectData(Model m_returned, Project projObj,
      LinkedList<String> predicates) throws Exception {
    Resource project_resource = null;
    project_resource = createProjectResource(project_resource, m_returned, projObj.getOwner(),
        projObj.getName());
    project_resource = addProjectData(m_returned, project_resource, projObj, getTelemetryClient(),
        predicates);
    if (predicates.contains(RDFS.seeAlso.getLocalName())
        || predicates.contains(OWL.sameAs.getLocalName()) || predicates.isEmpty()) {
      project_resource = addProjectLinkedData(project_resource, projObj.getName());
    }
    project_resource.addProperty(m_returned
        .createProperty("http://dublincore.org/documents/dcmi-terms/#terms-isPartOf"),
        HackystatConstants.voIDURI);
    return project_resource;
  }

  /**
   * Creates 'seeAlso' links between this project and external ones that have the same topics and
   * use the same tools. Creates 'sameAs' links between this project and external ones that have the
   * same title
   *
   * @param resource subject of all the triples that are going to be created
   * @param projName name of the current project-subject
   * @return resource updated with all the new triples/links
   * @throws UnsupportedEncodingException
   */
  public Resource addProjectLinkedData(Resource resource, String projName)
      throws UnsupportedEncodingException {
    // Ohloh
    String[] similarProjects = null;

    if (tags != null) {
      similarProjects = OhlohApiManager.getOhlohProjectDataByKeyWords(tags);
      if (similarProjects != null) {
        for (int i = 0; i < similarProjects.length; i++) {
          resource.addProperty(RDFS.seeAlso, HackystatConstants.RDFOHLOH_PROJECT_PATTERN
              + similarProjects[i]);
        }
      }
    }
    if (projName != null) {
      similarProjects = OhlohApiManager.getOhlohProjectDataByKeyWords(new String[] { projName });
      if (similarProjects != null) {
        for (int i = 0; i < similarProjects.length; i++) {
          resource.addProperty(OWL.sameAs, HackystatConstants.RDFOHLOH_PROJECT_PATTERN
              + similarProjects[i]);
        }
      }
    }
    // Hackystat servers
    HackystatLiSeDManager hackman = new HackystatLiSeDManager();
    similarProjects = hackman.getSeeAlsoLinkableProjects(tags, tools);
    if (similarProjects != null) {
      for (int i = 0; i < similarProjects.length; i++) {
        resource.addProperty(RDFS.seeAlso, similarProjects[i]);
      }
    }
    similarProjects = hackman.getSameAsLinkableProjects(projName);
    if (similarProjects != null) {
      for (int i = 0; i < similarProjects.length; i++) {
        resource.addProperty(OWL.sameAs, similarProjects[i]);
      }
    }
    return resource;
  }

  /**
   *
   * @param model
   * @param project_resource
   * @param p
   * @param client
   * @param predicates
   * @return
   * @throws TelemetryClientException
   */
  public Resource addProjectData(Model model, Resource project_resource, Project p,
      TelemetryClient client, LinkedList<String> predicates) throws TelemetryClientException {
    project_resource.addProperty(RDF.type, DoapVocab.PROJECT);
    if (predicates.contains(DoapVocab.DESCRIPTION.getLocalName()) || predicates.isEmpty()) {
      if (p.getDescription() != null) {
        project_resource.addProperty(DoapVocab.DESCRIPTION, p.getDescription());
      }
    }
    if (predicates.contains(DoapVocab.MAINTAINER.getLocalName()) || predicates.isEmpty()) {
      project_resource.addProperty(DoapVocab.MAINTAINER, getUserUri(p.getOwner()));
    }
    if (predicates.contains(HackystatVocab.PROJECT_OWNER.getLocalName()) || predicates.isEmpty()) {
      project_resource.addProperty(HackystatVocab.PROJECT_OWNER, getUserUri(p.getOwner()));
    }
    if (predicates.contains(DoapVocab.NAME.getLocalName()) || predicates.isEmpty()) {
      project_resource.addProperty(DoapVocab.NAME, p.getName());
    }
    if (predicates.contains(DoapVocab.CREATED.getLocalName()) || predicates.isEmpty()) {
      project_resource.addProperty(DoapVocab.CREATED, p.getStartTime().toString());
    }
    if (predicates.contains(HackystatVocab.ENDED.getLocalName()) || predicates.isEmpty()) {
      project_resource.addProperty(HackystatVocab.ENDED, p.getEndTime().toString());
    }
    if (predicates.contains(HackystatVocab.MODIFIED.getLocalName()) || predicates.isEmpty()) {
      if (p.getLastMod() != null)
        project_resource.addProperty(HackystatVocab.MODIFIED, p.getLastMod().toString());
    }
    if (predicates.contains(HackystatVocab.MEMBER.getLocalName()) || predicates.isEmpty()) {
      project_resource = addProjectMembers(project_resource, p);
    }
    if (predicates.contains(HackystatVocab.SPECTATOR.getLocalName()) || predicates.isEmpty()) {
      project_resource = addProjectSpectators(project_resource, p);
    }
    if (predicates.contains(HackystatVocab.INVITATED.getLocalName()) || predicates.isEmpty()) {
      project_resource = addProjectInvitated(project_resource, p);
    }

    project_resource = addProjectProperties(project_resource, p, predicates);
    if (client != null) {
      project_resource = addProjectQualityValues(project_resource, p, client, model, predicates);
    }

    return project_resource;
  }

  /**
   *
   * @param project_resource
   * @param p
   * @param client
   * @param model
   * @param predicates
   * @return
   * @throws TelemetryClientException
   */
  private static Resource addProjectQualityValues(Resource project_resource, Project p,
      TelemetryClient client, Model model, LinkedList<String> predicates)
      throws TelemetryClientException {
    String granularity = HackystatConstants.MONTH;
    System.out.println("");
    XMLGregorianCalendar[] startEndDates = adjustStartEndDates((XMLGregorianCalendar) p
        .getStartTime().clone(), (XMLGregorianCalendar) p.getEndTime().clone(), granularity);
    if (startEndDates != null && startEndDates[0] == null) {
      granularity = HackystatConstants.DAY;
      startEndDates = adjustStartEndDates((XMLGregorianCalendar) p.getStartTime().clone(),
          (XMLGregorianCalendar) p.getEndTime().clone(), granularity);
    }
    if (startEndDates != null) {
      HashMap<XMLGregorianCalendar, Double> ratios = null;
      HashMap<XMLGregorianCalendar, Integer> intValues = null;
      if (predicates.contains(HackystatVocab.BUILD_QUALITY.getLocalName()) || predicates.isEmpty()) {
        // build
        ratios = getSuccFailRatiosPerTimePeriod(p, startEndDates[0], startEndDates[1], client,
            HackystatConstants.BUILD, "*,", ",*" + HackystatConstants.CUMULATIVE_PARAM, "Success",
            "Failure", granularity);
        for (XMLGregorianCalendar month : ratios.keySet()) {
          project_resource.addProperty(HackystatVocab.BUILD_QUALITY, model.createResource()
              .addProperty(DC.date, month.toXMLFormat()).addProperty(HackystatVocab.AVERAGE_VALUE,
                  String.valueOf(ratios.get(month))));
        }
      }
      if (predicates.contains(HackystatVocab.UNIT_TEST_QUALITY.getLocalName())
          || predicates.isEmpty()) {
        // unit test
        ratios = getSuccFailRatiosPerTimePeriod(p, startEndDates[0], startEndDates[1], client,
            HackystatConstants.UNIT_TEST, "", ",*" + HackystatConstants.CUMULATIVE_PARAM,
            "SuccessCount", "FailureCount", granularity);
        for (XMLGregorianCalendar month : ratios.keySet()) {
          project_resource.addProperty(HackystatVocab.UNIT_TEST_QUALITY, model.createResource()
              .addProperty(DC.date, month.toXMLFormat()).addProperty(HackystatVocab.AVERAGE_VALUE,
                  String.valueOf(ratios.get(month))));
        }
      }
      if (predicates.contains(HackystatVocab.COVERAGE_QUALITY.getLocalName())
          || predicates.isEmpty()) {
        // coverage
        intValues = getIntegerTimeValuesPerTimePeriod(client, startEndDates[0], startEndDates[1], p
            .getOwner(), p.getName(), HackystatConstants.COVERAGE, "Percentage,class", granularity);
        for (XMLGregorianCalendar month : intValues.keySet()) {
          project_resource.addProperty(HackystatVocab.COVERAGE_QUALITY, model.createResource()
              .addProperty(DC.date, month.toXMLFormat()).addProperty(HackystatVocab.AVERAGE_VALUE,
                  String.valueOf(intValues.get(month))));
        }
      }
      if (predicates.contains(HackystatVocab.AMOUNT_OF_CODE_ISSUES.getLocalName())
          || predicates.isEmpty()) {
        // code issue
        intValues = getIntegerTimeValuesPerTimePeriod(client, startEndDates[0], startEndDates[1], p
            .getOwner(), p.getName(), HackystatConstants.CODE_ISSUE, "*,*", granularity);
        for (XMLGregorianCalendar month : intValues.keySet()) {
          project_resource.addProperty(HackystatVocab.AMOUNT_OF_CODE_ISSUES, model.createResource()
              .addProperty(DC.date, month.toXMLFormat()).addProperty(HackystatVocab.AVERAGE_VALUE,
                  String.valueOf(intValues.get(month))));
        }
      }
      if (predicates.contains(HackystatVocab.COMMIT_FREQUENCY.getLocalName())
          || predicates.isEmpty()) {
        // commit
        intValues = getIntegerTimeValuesPerTimePeriod(client, startEndDates[0], startEndDates[1], p
            .getOwner(), p.getName(), HackystatConstants.COMMIT, "*"
            + HackystatConstants.CUMULATIVE_PARAM, granularity);
        for (XMLGregorianCalendar month : intValues.keySet()) {
          project_resource.addProperty(HackystatVocab.COMMIT_FREQUENCY, model.createResource()
              .addProperty(DC.date, month.toXMLFormat()).addProperty(HackystatVocab.AVERAGE_VALUE,
                  String.valueOf(intValues.get(month))));
        }
      }
      if (predicates.contains(HackystatVocab.DEVELOPMENT_TIME.getLocalName())
          || predicates.isEmpty()) {
        // development time
        ratios = getDoubleTimeValuesPerTimePeriod(client, startEndDates[0], startEndDates[1], p
            .getOwner(), p.getName(), HackystatConstants.DEVTIME, "*"
            + HackystatConstants.CUMULATIVE_PARAM, granularity);
        for (XMLGregorianCalendar month : ratios.keySet()) {
          project_resource.addProperty(HackystatVocab.DEVELOPMENT_TIME, model.createResource()
              .addProperty(DC.date, month.toXMLFormat()).addProperty(HackystatVocab.AVERAGE_VALUE,
                  String.valueOf(ratios.get(month))));
        }
      }
    }
    return project_resource;
  }

  /**
   *
   * @param project_resource
   * @param p
   * @param predicates
   * @return
   */
  private Resource addProjectProperties(Resource project_resource, Project p,
      LinkedList<String> predicates) {
    if (!predicates.contains(HackystatVocab.INCLUDES_PHASE.getLocalName())
        && !predicates.contains(HackystatVocab.USES_TOOL.getLocalName())
        && !predicates.contains(HackystatVocab.ON_OPERATING_SYSTEM.getLocalName())
        && !predicates.contains(DoapVocab.REPOSITORY.getLocalName())
        && !predicates.contains(DoapVocab.DOWNLOAD_PAGE.getLocalName())
        && !predicates.contains(DoapVocab.DOWNLOAD_MIRROR.getLocalName())
        && !predicates.contains(DoapVocab.WIKI.getLocalName())
        && !predicates.contains(DoapVocab.BUG_DATABASE.getLocalName())
        && !predicates.contains(DoapVocab.MAINTAINER.getLocalName())
        && !predicates.contains(DoapVocab.LANGUAGE.getLocalName())
        && !predicates.contains(DoapVocab.TESTER.getLocalName())
        && !predicates.contains(DoapVocab.HELPER.getLocalName())
        && !predicates.contains(DoapVocab.DOCUMENTER.getLocalName())
        && !predicates.contains(DoapVocab.TRANSLATOR.getLocalName())
        && !predicates.contains(DoapVocab.DEVELOPER.getLocalName())
        && !predicates.contains(DoapVocab.CATEGORY.getLocalName())
        && !predicates.contains(DoapVocab.BROWSE.getLocalName()) && !predicates.isEmpty()) {
      return project_resource;
    }
    Properties props = p.getProperties();
    if (props != null) {
      List<Property> list = props.getProperty();
      if (list != null) {
        Iterator<Property> it = list.iterator();
        Property prop = null;
        String key = null, value = null;
        String[] elems = null;
        while (it.hasNext()) {
          prop = it.next();
          key = prop.getKey();
          value = prop.getValue();
          if (key.equals(HackystatConstants.PROFILE_VISIBILITY)) {
            project_resource.addProperty(HackystatVocab.PRIVACY_LEVEL, value);
          }
          else if (key.equals(HackystatConstants.PROJECT_PHASES)) {
            // in the form of: phaseId phaseId etc.
            String[] phases = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int j = 0; j < phases.length; j++) {
              project_resource.addProperty(HackystatVocab.INCLUDES_PHASE, getDevPhaseUri(uriUser,
                  projectName, phases[j]));
            }
          }
          else if (key.equals(HackystatConstants.REPOSITORY_LOCATION)) {
            project_resource.addProperty(DoapVocab.REPOSITORY, value);
          }
          else if (key.equals(HackystatConstants.REPOSITORY_ANON_ROOT)) {
            project_resource.addProperty(DoapVocab.ANON_ROOT, value);
          }
          else if (key.equals(HackystatConstants.REPOSITORY_WEB_INTERFACE)) {
            project_resource.addProperty(DoapVocab.BROWSE, value);
          }
          else if (key.equals(HackystatConstants.PROJECT_DOWLOAD_MIRRORS)) {
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.DOWNLOAD_MIRROR, elems[i]);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_WIKI)) {
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.WIKI, elems[i]);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_BUG_DB)) {
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.BUG_DATABASE, elems[i]);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_MAINTAINERS)) {
            // in the form of userEmail userEmail etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.MAINTAINER, getUserUri(elems[i]));
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_DEVELOPERS)) {
            // in the form of userEmail userEmail etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.DEVELOPER, getUserUri(elems[i]));
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_TRANSLATORS)) {
            // in the form of userEmail userEmail etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.TRANSLATOR, getUserUri(elems[i]));
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_TESTERS)) {
            // in the form of userEmail userEmail etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.TESTER, getUserUri(elems[i]));
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_DOCUMENTERS)) {
            // in the form of userEmail userEmail etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.DOCUMENTER, getUserUri(elems[i]));
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_HELPERS)) {
            // in the form of userEmail userEmail etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.HELPER, getUserUri(elems[i]));
            }
          }
          else if (key.equals(HackystatConstants.LANGUAGE)) {
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.LANGUAGE, getProgrammingLanguageUri(elems[i]));
            }
          }
          else if (key.equals(HackystatConstants.OPERATING_SYSTEM)) {
            // in the form of: osName osName osName etc.
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(HackystatVocab.ON_OPERATING_SYSTEM,
                  getOperatingSystemUri(elems[i]));
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_TAGS)) {
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            tags = elems;
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(DoapVocab.CATEGORY, elems[i]);
            }
          }
          else if (key.equals(HackystatConstants.PROJECT_TOOLS)) {
            elems = value.split(HackystatConstants.SEPARATOR1_ID);
            this.tools = elems;
            for (int i = 0; i < elems.length; i++) {
              project_resource.addProperty(HackystatVocab.USES_TOOL, getToolUri(elems[i]));
            }
          }
        }
      }
    }
    return project_resource;
  }

  /**
   *
   * @param pr
   * @param p
   * @return
   */
  protected static Resource addProjectInvitated(Resource pr, Project p) {
    List<String> list = p.getInvitations().getInvitation();
    Iterator<String> it = list.iterator();
    String memberName = null;
    while (it.hasNext()) {
      memberName = it.next();
      // it's granted that all members, spectators and invitates of a project
      // are registered hackystat users
      pr.addProperty(HackystatVocab.INVITATED, getUserUri(memberName));
    }
    return pr;
  }

  /**
   *
   * @param pr
   * @param p
   * @return
   */
  protected static Resource addProjectSpectators(Resource pr, Project p) {
    List<String> list = p.getSpectators().getSpectator();
    Iterator<String> it = list.iterator();
    String memberName = null;
    while (it.hasNext()) {
      memberName = it.next();
      pr.addProperty(HackystatVocab.SPECTATOR, getUserUri(memberName));
    }
    return pr;
  }

  /**
   *
   * @param pr
   * @param p
   * @return
   */
  protected static Resource addProjectMembers(Resource pr, Project p) {
    List<String> list = p.getMembers().getMember();
    Iterator<String> it = list.iterator();
    String memberName = null;
    while (it.hasNext()) {
      memberName = it.next();
      pr.addProperty(HackystatVocab.MEMBER, getUserUri(memberName));
    }
    return pr;
  }
}

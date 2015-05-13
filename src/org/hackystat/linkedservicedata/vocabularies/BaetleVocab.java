/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Jane Smart, example.com
 * Author email       jane.smart@example.com
 * Package            @package@
 * Web site           @website@
 * Created            04 Jun 2009 15:23
 * Filename           $RCSfile: schemagen.html,v $
 * Revision           $Revision: 1.15 $
 * Release status     @releaseStatus@ $State: Exp $
 *
 * Last modified on   $Date: 2008/04/04 11:46:00 $
 *               by   $Author: ian_dickinson $
 *
 * @copyright@
 *****************************************************************************/

// Package
///////////////////////////////////////
package org.hackystat.linkedservicedata.vocabularies;

// Imports
///////////////////////////////////////
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.ontology.*;

/**
 * Vocabulary definitions from
 * /home/myrtill/Hackystat_linkedData/mysqlProva/workspace/hackystat-linked
 * -sensor-data/src/org/hackystat/linkedsensordata/vocabularies/Baetle.n3
 *
 * @author Auto-generated by schemagen on 04 Jun 2009 15:23
 */
public class BaetleVocab {
  /**
   * <p>
   * The ontology model that holds the vocabulary terms
   * </p>
   */
  private static OntModel m_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);

  /**
   * <p>
   * The namespace of the vocabulary as a string
   * </p>
   */
  public static final String NS = "http://xmlns.com/baetle/#";

  public static final String PREFIX = "baetle";

  /**
   * <p>
   * The namespace of the vocabulary as a string
   * </p>
   *
   * @see #NS
   */
  public static String getURI() {
    return NS;
  }

  /**
   * <p>
   * The namespace of the vocabulary as a resource
   * </p>
   */
  public static final Resource NAMESPACE = m_model.createResource(NS);

  /** Factory for generating symbols */
  /** private static KsValueFactory s_vf = new DefaultValueFactory(); */

  // Vocabulary properties
  // /////////////////////////
  public static final ObjectProperty ABOUT = m_model
      .createObjectProperty("http://xmlns.com/baetle/#about");

  /**
   * <p>
   * the person the bug is assigned to
   * </p>
   */
  public static final ObjectProperty ASSIGNED_TO = m_model
      .createObjectProperty("http://xmlns.com/baetle/#assigned_to");

  /**
   * <p>
   * this issue is blocking the other
   * </p>
   */
  public static final ObjectProperty BLOCKS = m_model
      .createObjectProperty("http://xmlns.com/baetle/#blocks");

  /**
   * <p>
   * this Issue causes the other one
   * </p>
   */
  public static final ObjectProperty CAUSES = m_model
      .createObjectProperty("http://xmlns.com/baetle/#causes");

  /**
   * <p>
   * A comment on an Issue
   * </p>
   */
  public static final ObjectProperty COMMENT = m_model
      .createObjectProperty("http://xmlns.com/baetle/#comment");

  public static final ObjectProperty CONTAINS = m_model
      .createObjectProperty("http://xmlns.com/baetle/#contains");

  /**
   * <p>
   * a task that depends on this one being completed
   * </p>
   */
  public static final ObjectProperty DEPENDS_ON = m_model
      .createObjectProperty("http://xmlns.com/baetle/#depends_on");

  /**
   * <p>
   * this Issue is a duplicate of the other.
   * </p>
   */
  public static final ObjectProperty DUPLICATE = m_model
      .createObjectProperty("http://xmlns.com/baetle/#duplicate");

  /**
   * <p>
   * A user interested in changes to this issue
   * </p>
   */
  public static final ObjectProperty INTERESTED = m_model
      .createObjectProperty("http://xmlns.com/baetle/#interested");

  public static final ObjectProperty NAME = m_model
      .createObjectProperty("http://xmlns.com/baetle/#name");

  public static final ObjectProperty OPERATING_SYSTEM = m_model
      .createObjectProperty("http://xmlns.com/baetle/#operating_system");

  public static final ObjectProperty PLATFORM = m_model
      .createObjectProperty("http://xmlns.com/baetle/#platform");

  /**
   * <p>
   * the priority value recognised by those responsible for the issue
   * </p>
   */
  public static final ObjectProperty PRIORITY = m_model
      .createObjectProperty("http://xmlns.com/baetle/#priority");

  /**
   * <p>
   * the project this issue concerns
   * </p>
   */
  public static final ObjectProperty PROJECT = m_model
      .createObjectProperty("http://xmlns.com/baetle/#project");

  /**
   * <p>
   * A contact point
   * </p>
   */
  public static final ObjectProperty QA_CONTACT = m_model
      .createObjectProperty("http://xmlns.com/baetle/#qa_contact");

  public static final ObjectProperty RELATES_TO = m_model
      .createObjectProperty("http://xmlns.com/baetle/#relates_to");

  /**
   * <p>
   * the reporter of the bug
   * </p>
   */
  public static final ObjectProperty REPORTER = m_model
      .createObjectProperty("http://xmlns.com/baetle/#reporter");

  public static final ObjectProperty RESOLVED_WITH = m_model
      .createObjectProperty("http://xmlns.com/baetle/#resolvedWith");

  public static final ObjectProperty STATUS = m_model
      .createObjectProperty("http://xmlns.com/baetle/#status");

  public static final ObjectProperty TARGET_MILESTONE = m_model
      .createObjectProperty("http://xmlns.com/baetle/#target_milestone");

  public static final ObjectProperty VERSION = m_model
      .createObjectProperty("http://xmlns.com/baetle/#version");

  /**
   * <p>
   * Creation date of Issue
   * </p>
   */
  public static final DatatypeProperty CREATED = m_model
      .createDatatypeProperty("http://xmlns.com/baetle/#created");

  /**
   * <p>
   * longer description of the Issue
   * </p>
   */
  public static final DatatypeProperty DESCRIPTION = m_model
      .createDatatypeProperty("http://xmlns.com/baetle/#description");

  /**
   * <p>
   * date the task is due by
   * </p>
   */
  public static final DatatypeProperty DUE_DATE = m_model
      .createDatatypeProperty("http://xmlns.com/baetle/#due_date");

  /**
   * <p>
   * description of the environment where the issue was noticed
   * </p>
   */
  public static final DatatypeProperty ENVIRONMENT = m_model
      .createDatatypeProperty("http://xmlns.com/baetle/#environment");

  /**
   * <p>
   * a subtask of this issue
   * </p>
   */
  public static final DatatypeProperty SUBTASK = m_model
      .createDatatypeProperty("http://xmlns.com/baetle/#subtask");

  /**
   * <p>
   * summary of Issue
   * </p>
   */
  public static final DatatypeProperty SUMMARY = m_model
      .createDatatypeProperty("http://xmlns.com/baetle/#summary");

  /**
   * <p>
   * date Issue was updated
   * </p>
   */
  public static final DatatypeProperty UPDATED = m_model
      .createDatatypeProperty("http://xmlns.com/baetle/#updated");

  /**
   * <p>
   * number of votes for the issue
   * </p>
   */
  public static final DatatypeProperty VOTES = m_model
      .createDatatypeProperty("http://xmlns.com/baetle/#votes");

  // Vocabulary classes
  // /////////////////////////

  public static final OntClass BUG = m_model.createClass("http://xmlns.com/baetle/#Bug");

  public static final OntClass COMITTING = m_model
      .createClass("http://xmlns.com/baetle/#Comitting");

  public static final OntClass DEFECT = m_model.createClass("http://xmlns.com/baetle/#Defect");

  public static final OntClass DOCUMENT_VERSION = m_model
      .createClass("http://xmlns.com/baetle/#DocumentVersion");

  public static final OntClass ENHANCEMENT = m_model
      .createClass("http://xmlns.com/baetle/#Enhancement");

  /**
   * <p>
   * An issue in a bug database.
   * </p>
   */
  public static final OntClass ISSUE = m_model.createClass("http://xmlns.com/baetle/#Issue");

  public static final OntClass NOT_REPRODUCIBLE = m_model
      .createClass("http://xmlns.com/baetle/#NotReproducible");

  public static final OntClass PATCH = m_model.createClass("http://xmlns.com/baetle/#Patch");

  /**
   * <p>
   * Priority values.
   * </p>
   */
  public static final OntClass PRIORITY_CLASS = m_model
      .createClass("http://xmlns.com/baetle/#Priority");

  public static final OntClass SOFTWARE_PACKAGE = m_model
      .createClass("http://xmlns.com/baetle/#SoftwarePackage");

  public static final OntClass SOLUTION = m_model.createClass("http://xmlns.com/baetle/#Solution");

  // Vocabulary individuals
  // /////////////////////////

}

/*
 * @footer@
 */


/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Myriam Leggieri
 * Author email       myriam.leggieri@gmail.com
 * Package            @package@
 * Web site           @website@
 * Created            16 Jun 2009 17:54
 * Filename           $RCSfile $
 * Revision           $Revision$
 * Release status     @releaseStatus@ $State: Exp $
 *
 * Last modified on   $Date: 2009/06/05 11:46:00 $
 *               by   $Author: myriam $
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
 * -service-data/src/org/hackystat/linkedservicedata/vocabularies/sec.owl
 *
 * @author Auto-generated by schemagen on 16 Jun 2009 17:54
 */
public class SecVocab {
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
  public static final String NS = "http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#";

  public static final String PREFIX = "sec";

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

  // Vocabulary properties
  // /////////////////////////

  /**
   * <p>
   * Defines the relationship between an OO Requirement and an OO Class which encodes it.
   * </p>
   */
  public static final ObjectProperty ENCODES_REQUIREMENT = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#encodesRequirement");

  /**
   * <p>
   * Defines the relationship between a Method and a Requirement, where the Requirement's
   * implemention is best understood by starting with the Method.
   * </p>
   */
  public static final ObjectProperty ENTRY_POINT_FOR = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#entryPointFor");

  /**
   * <p>
   * Defines the relationship between an OO super class and another class which extends it.
   * </p>
   */
  public static final ObjectProperty EXTENDED_BY = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#extendedBy");

  /**
   * <p>
   * Defines the relationship between an OO Class which extends another and the OO CLass which it
   * extends.
   * </p>
   */
  public static final ObjectProperty EXTENDS = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#extends");

  /**
   * <p>
   * Defines the relationship between a Requirement and a Method which is the best place to start
   * understanding the Requirement's implementation.
   * </p>
   */
  public static final ObjectProperty HAS_ENTRY_POINT = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasEntryPoint");

  /**
   * <p>
   * Defines the relationship between an Object-Oriented Class and the methods that it contains.
   * </p>
   */
  public static final ObjectProperty HAS_METHOD = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasMethod");

  /**
   * <p>
   * Defines the relationship between an OO Interface or Abstract Class and an OO Method Signature.
   * </p>
   */
  public static final ObjectProperty HAS_METHOD_SIGNATURE = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasMethodSignature");

  /**
   * <p>
   * Defines the relationship between an OO Software Component and a Metric which has been
   * calculated for it.
   * </p>
   */
  public static final ObjectProperty HAS_METRIC = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasMetric");

  /**
   * <p>
   * Defines the relationship between a Metric and its calculated value.
   * </p>
   */
  public static final ObjectProperty HAS_METRIC_VALUE = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasMetricValue");

  /**
   * <p>
   * Defines the relationship between an OO Program and a Package which it contains.
   * </p>
   */
  public static final ObjectProperty HAS_PACKAGE = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasPackage");

  /**
   * <p>
   * Defines the relationship between an OO Package and the OO Classes/AbstractClasses/Interfaces
   * which belong to it.
   * </p>
   */
  public static final ObjectProperty HAS_PACKAGE_MEMBER = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasPackageMember");

  /**
   * <p>
   * Defines the relationship between an OO Software Component and an associated Test.
   * </p>
   */
  public static final ObjectProperty HAS_TEST = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasTest");

  /**
   * <p>
   * Defines the relationship between an OO Class and an OO Interface which it extends.
   * </p>
   */
  public static final ObjectProperty IMPLEMENTS_INTERFACE = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#implementsInterface");

  /**
   * <p>
   * Defines the relationship between an OO Interface and an OO Class which implements it.
   * </p>
   */
  public static final ObjectProperty INTERFACE_IMPLEMENTED_BY = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#interfaceImplementedBy");

  /**
   * <p>
   * Defines the relationship between a Metric and an OO Software Component for which it was
   * calculated.
   * </p>
   */
  public static final ObjectProperty IS_METRIC_OF = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#isMetricOf");

  /**
   * <p>
   * Defines the relationship between a Test and an OO Software Component.
   * </p>
   */
  public static final ObjectProperty IS_TEST_OF = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#isTestOf");

  /**
   * <p>
   * Defines at which date and time a given element was modified.¬¨¬®‚Äö√Ñ‚Ä†
   * </p>
   */
  public static final ObjectProperty LAST_MODIFIED_AT = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#lastModifiedAt");

  /**
   * <p>
   * Defines at which date and time a given Requirement was validated by a person.
   * </p>
   */
  public static final ObjectProperty LAST_VALIDATED_AT = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#lastValidatedAt");

  /**
   * <p>
   * Defines the relationship between a method and the Object-Oriented Class which contains it.
   * </p>
   */
  public static final ObjectProperty METHOD_OF = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#methodOf");

  /**
   * <p>
   * Defines the relationship between an OO Method Signature and a containing OO Interface or
   * Abstract Class.
   * </p>
   */
  public static final ObjectProperty METHOD_SIGNATURE_OF = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#methodSignatureOf");

  /**
   * <p>
   * Defines the relationship between two OO Methods, from the used Method to the using Method.
   * </p>
   */
  public static final ObjectProperty METHOD_USED_BY = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#methodUsedBy");

  /**
   * <p>
   * Defines the relationship between a Metric and the date-time at which it was calculated.
   * </p>
   */
  public static final ObjectProperty METRIC_CALCULATED_AT = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#metricCalculatedAt");

  /**
   * <p>
   * Defines the relationship between an OO Class/AbstractClass/Interface and the OO Package to
   * which it belongs.
   * </p>
   */
  public static final ObjectProperty PACKAGE_MEMBER_OF = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#packageMemberOf");

  /**
   * <p>
   * Defines the relationship between an OO Package and the OO Program which contains it.
   * </p>
   */
  public static final ObjectProperty PACKAGE_OF = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#packageOf");

  /**
   * <p>
   * Defines the relationship between an OO Requirement and an OO Class which encodes it.
   * </p>
   */
  public static final ObjectProperty REQUIREMENT_ENCODED_BY = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#requirementEncodedBy");

  /**
   * <p>
   * Defines the relationship between a Test and a Boolen indicating whether the Test passed or
   * failed.
   * </p>
   */
  public static final ObjectProperty SUCCEEDED = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#succeeded");

  /**
   * <p>
   * Defines the relationship between a Test and the date-time at which the test was concluded.
   * </p>
   */
  public static final ObjectProperty TESTED_AT = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#testedAt");

  /**
   * <p>
   * Defines the relationship between two OO Methods, from the using Method to the used Method.
   * </p>
   */
  public static final ObjectProperty USES_METHOD = m_model
      .createObjectProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#usesMethod");

  /**
   * <p>
   * Defines the relationship between a Test and the its results.
   * </p>
   */
  public static final DatatypeProperty HAS_TEST_RESULTS = m_model
      .createDatatypeProperty("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#hasTestResults");

  // Vocabulary classes
  // /////////////////////////

  /**
   * <p>
   * An integration test of a software subsystem.
   * </p>
   */
  public static final OntClass INTEGRATION_TEST = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#IntegrationTest");

  /**
   * <p>
   * An automatically generated metric about a software component.
   * </p>
   */
  public static final OntClass METRIC = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#Metric");

  /**
   * <p>
   * An Object-Oriented Abstract Class.
   * </p>
   */
  public static final OntClass OOABSTRACT_CLASS = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#OOAbstractClass");

  /**
   * <p>
   * An Object-Oriented Class
   * </p>
   */
  public static final OntClass OOCLASS = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#OOClass");

  /**
   * <p>
   * An Object-Oriented Interface.
   * </p>
   */
  public static final OntClass OOINTERFACE = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#OOInterface");

  /**
   * <p>
   * An Object-Oriented Method.
   * </p>
   */
  public static final OntClass OOMETHOD = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#OOMethod");

  /**
   * <p>
   * An Object-Oriented Method Signature.
   * </p>
   */
  public static final OntClass OOMETHOD_SIGNATURE = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#OOMethodSignature");

  /**
   * <p>
   * An Object-Oriented package of classes.
   * </p>
   */
  public static final OntClass OOPACKAGE = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#OOPackage");

  /**
   * <p>
   * An Object-Oriented software program.
   * </p>
   */
  public static final OntClass OOPROGRAM = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#OOProgram");

  /**
   * <p>
   * A Object-Oriented software component.
   * </p>
   */
  public static final OntClass OOSOFTWARE_COMPONENT = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#OOSoftwareComponent");

  /**
   * <p>
   * A requirement for a software system.
   * </p>
   */
  public static final OntClass REQUIREMENT = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#Requirement");

  /**
   * <p>
   * An automated test of a software component.
   * </p>
   */
  public static final OntClass TEST = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#Test");

  /**
   * <p>
   * A unit test of a software component.
   * </p>
   */
  public static final OntClass UNIT_TEST = m_model
      .createClass("http://www.itee.uq.edu.au/~dwood/ontologies/sec.owl#UnitTest");

  // Vocabulary individuals
  // /////////////////////////

}

/*
 * @footer@
 */


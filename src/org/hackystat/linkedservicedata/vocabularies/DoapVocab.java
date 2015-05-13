/*****************************************************************************
 * Source code information
 * -----------------------
 * Original author    Myriam Leggieri
 * Author email       myriam.leggieri@gmail.com
 * Package            @package@
 * Web site           @website@
 * Created            04 Jun 2009 19:13
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
 * -sensor-data/src/org/hackystat/linkedsensordata/vocabularies/doap.rdf
 *
 * @author Auto-generated by schemagen on 04 Jun 2009 19:13
 */
public class DoapVocab {
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
  public static final String NS = "http://usefulinc.com/ns/doap#";

  public static final String PREFIX = "doap";

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
   * Repository für anonymen ZugriffRepository for anonymous access.Repositorio para acceso
   * anónimo.Dépôt pour accès anonyme.Úložiště pro anonymní přístup.
   * </p>
   */
  public static final OntProperty ANON_ROOT = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#anon-root");

  /**
   * <p>
   * Description of target user base
   * </p>
   */
  public static final OntProperty AUDIENCE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#audience");

  /**
   * <p>
   * URI of a blog related to a project
   * </p>
   */
  public static final OntProperty BLOG = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#blog");

  /**
   * <p>
   * Webové rozhraní pro prohlížení úložiště.Web browser interface to repository.Web-Browser
   * Interface für das Repository.Interface web del repositorio.Interface web au dépôt.
   * </p>
   */
  public static final OntProperty BROWSE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#browse");

  /**
   * <p>
   * Suivi des bugs pour un projet.Bug tracker para un proyecto.Správa chyb projektu.Fehlerdatenbank
   * eines Projektes.Bug tracker for a project.
   * </p>
   */
  public static final OntProperty BUG_DATABASE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#bug-database");

  /**
   * <p>
   * A category of project.Eine Kategorie eines Projektes.Una categoría de proyecto.Une catégorie de
   * projet.Kategorie projektu.
   * </p>
   */
  public static final OntProperty CATEGORY = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#category");

  /**
   * <p>
   * Fecha en la que algo fue creado, en formato AAAA-MM-DD. e.g. 2004-04-05Erstellungsdatum von
   * Irgendwas, angegeben im YYYY-MM-DD Format, z.B. 2004-04-05.Date à laquelle a été créé quelque
   * chose, au format AAAA-MM-JJ (par ex. 2004-04-05)Datum, kdy bylo něco vytvořeno ve formátu
   * RRRR-MM-DD, např. 2004-04-05Date when something was created, in YYYY-MM-DD form. e.g.
   * 2004-04-05
   * </p>
   */
  public static final OntProperty CREATED = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#created");

  /**
   * <p>
   * Čistě textový, 2 až 4 věty dlouhý popis projektu.Descripción en texto plano de un proyecto, de
   * 2 a 4 enunciados de longitud.Texte descriptif d'un projet, long de 2 à 4 phrases.Plain text
   * description of a project, of 2-4 sentences in length.Beschreibung eines Projekts als einfacher
   * Text mit der Länge von 2 bis 4 Sätzen.
   * </p>
   */
  public static final OntProperty DESCRIPTION = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#description");

  /**
   * <p>
   * Developer of software for the project.Développeur pour le projet.Software-Entwickler für eine
   * Projekt.Vývojář softwaru projektu.Desarrollador de software para el proyecto.
   * </p>
   */
  public static final OntProperty DEVELOPER = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#developer");

  /**
   * <p>
   * Proveedor de documentación para el proyecto.Spoluautor dokumentace projektu.Contributor of
   * documentation to the project.Mitarbeiter an der Dokumentation eines Projektes.Collaborateur à
   * la documentation du projet.
   * </p>
   */
  public static final OntProperty DOCUMENTER = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#documenter");

  /**
   * <p>
   * Miroir de la page de téléchargement du programme.Spiegel der Seite von die Projekt-Software
   * heruntergeladen werden kann.Mirror de la página web de descarga.Mirror of software download web
   * page.Zrcadlo stránky pro stažení softwaru.
   * </p>
   */
  public static final OntProperty DOWNLOAD_MIRROR = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#download-mirror");

  /**
   * <p>
   * Page web à partir de laquelle on peut télécharger le programme.Página web de la cuál se puede
   * bajar el software.Web page from which the project software can be downloaded.Web-Seite von der
   * die Projekt-Software heruntergeladen werden kann.Webová stránka, na které lze stáhnout
   * projektový software.
   * </p>
   */
  public static final OntProperty DOWNLOAD_PAGE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#download-page");

  /**
   * <p>
   * URI adresa stažení asociované s revizí.URI of download associated with this release.
   * </p>
   */
  public static final OntProperty FILE_RELEASE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#file-release");

  /**
   * <p>
   * Colaborador del proyecto.Projekt-Mitarbeiter.Project contributor.Spoluautor
   * projektu.Collaborateur au projet.
   * </p>
   */
  public static final OntProperty HELPER = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#helper");

  /**
   * <p>
   * URL der Projekt-Homepage, verbunden mit genau einem Projekt.El URL de la página de un proyecto,
   * asociada con exactamente un proyecto.L'URL de la page web d'un projet, associée avec un unique
   * projet.URL adresa domovské stránky projektu asociované s právě jedním projektem.URL of a
   * project's homepage, associated with exactly one project.
   * </p>
   */
  public static final OntProperty HOMEPAGE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#homepage");

  /**
   * <p>
   * A specification that a project implements. Could be a standard, API or legally defined level of
   * conformance.
   * </p>
   */
  public static final OntProperty IMPLEMENTS = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#implements");

  /**
   * <p>
   * ISO language code a project has been translated into
   * </p>
   */
  public static final OntProperty LANGUAGE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#language");

  /**
   * <p>
   * Die URI einer RDF-Beschreibung einer Lizenz unter der die Software herausgegeben wird.El URI de
   * una descripción RDF de la licencia bajo la cuál se distribuye el software.The URI of an RDF
   * description of the license the software is distributed under.URI adresa RDF popisu licence, pod
   * kterou je software distribuován.L'URI d'une description RDF de la licence sous laquelle le
   * programme est distribué.
   * </p>
   */
  public static final OntProperty LICENSE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#license");

  /**
   * <p>
   * Lokation eines Repositorys.Emplacement d'un dépôt.Location of a repository.lugar de un
   * repositorio.Umístění úložiště.
   * </p>
   */
  public static final OntProperty LOCATION = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#location");

  /**
   * <p>
   * Mailing list home page or email address.Homepage der Mailing Liste oder E-Mail Adresse.Page web
   * de la liste de diffusion, ou adresse de courriel.Domovská stránka nebo e–mailová adresa
   * e–mailové diskuse.Página web de la lista de correo o dirección de correo.
   * </p>
   */
  public static final OntProperty MAILING_LIST = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#mailing-list");

  /**
   * <p>
   * Hauptentwickler eines Projektes, der ProjektleiterSprávce projektu, vedoucí
   * projektu.Développeur principal d'un projet, un meneur du projet.Maintainer of a project, a
   * project leader.Desarrollador principal de un proyecto, un líder de proyecto.
   * </p>
   */
  public static final OntProperty MAINTAINER = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#maintainer");

  /**
   * <p>
   * Nom du module d'un dépôt.Jméno modulu v úložišti.Nombre del módulo de un repositorio.Module
   * name of a repository.Modul-Name eines Subversion.
   * </p>
   */
  public static final OntProperty MODULE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#module");

  /**
   * <p>
   * Der Name von IrgendwasJméno něčeho.A name of something.El nombre de algo.Le nom de quelque
   * chose.
   * </p>
   */
  public static final OntProperty NAME = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#name");

  /**
   * <p>
   * URL adresa předešlé domovské stránky projektu asociované s právě jedním projektem.El URL de la
   * antigua página de un proyecto, asociada con exactamente un proyecto.URL of a project's past
   * homepage, associated with exactly one project.L'URL d'une ancienne page web d'un projet,
   * associée avec un unique projet.URL der letzten Projekt-Homepage, verbunden mit genau einem
   * Projekt.
   * </p>
   */
  public static final OntProperty OLD_HOMEPAGE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#old-homepage");

  /**
   * <p>
   * Operating system that a project is limited to. Omit this property if the project is not
   * OS-specific.Sistema opertivo al cuál está limitado el proyecto. Omita esta propiedad si el
   * proyecto no es específico de un sistema opertaivo en particular.Operační systém, na jehož
   * použití je projekt limitován. Vynechejte tuto vlastnost, pokud je projekt nezávislý na
   * operačním systému.Système d'exploitation auquel est limité le projet. Omettez cette propriété
   * si le projet n'est pas limité à un système d'exploitation.Betriebssystem auf dem das Projekt
   * eingesetzt werden kann. Diese Eigenschaft kann ausgelassen werden, wenn das Projekt nicht
   * BS-spezifisch ist.
   * </p>
   */
  public static final OntProperty OS = m_model.createOntProperty("http://usefulinc.com/ns/doap#os");

  /**
   * <p>
   * Indicator of software platform (non-OS specific), e.g. Java, Firefox, ECMA CLR
   * </p>
   */
  public static final OntProperty PLATFORM = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#platform");

  /**
   * <p>
   * Lenguaje de programación en el que un proyecto es implementado o con el cuál pretende
   * usarse.Programming language a project is implemented in or intended for use
   * with.Programmiersprache in der ein Projekt implementiert ist oder intendiert wird zu
   * benutzen.Langage de programmation avec lequel un projet est implémenté, ou avec lequel il est
   * prévu de l'utiliser.Programovací jazyk, ve kterém je projekt implementován nebo pro který je
   * zamýšlen k použití.
   * </p>
   */
  public static final OntProperty PROGRAMMING_LANGUAGE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#programming-language");

  /**
   * <p>
   * Une release (révision) d'un projet.Relase (verze) projektu.A project release.Un release
   * (versión) de un proyecto.Ein Release (Version) eines Projekts.
   * </p>
   */
  public static final OntProperty RELEASE = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#release");

  /**
   * <p>
   * Úložiště zdrojových kódů.Quellcode-Versionierungssystem.Dépôt du code source.Source code
   * repository.Repositorio del código fuente.
   * </p>
   */
  public static final OntProperty REPOSITORY = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#repository");

  /**
   * <p>
   * Indentificador de la versión de un release de software.Versionsidentifikator eines
   * Software-Releases.Identifiant de révision d'une release du programme.Revision identifier of a
   * software release.Identifikátor zpřístupněné revize softwaru.
   * </p>
   */
  public static final OntProperty REVISION = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#revision");

  /**
   * <p>
   * Webová stránka projektu se snímky obrazovky.Web-Seite mit Screenshots eines Projektes.Page web
   * avec des captures d'écran du projet.Web page with screenshots of project.Página web con
   * capturas de pantalla del proyecto.
   * </p>
   */
  public static final OntProperty SCREENSHOTS = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#screenshots");

  /**
   * <p>
   * The URI of a web service endpoint where software as a service may be accessed
   * </p>
   */
  public static final OntProperty SERVICE_ENDPOINT = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#service-endpoint");

  /**
   * <p>
   * Krátký (8 nebo 9 slov) čistě textový popis projektu.Short (8 or 9 words) plain text description
   * of a project.Descripción corta (8 o 9 palabras) en texto plano de un proyecto.Texte descriptif
   * concis (8 ou 9 mots) d'un projet.Kurzbeschreibung (8 oder 9 Wörter) eines Projects als
   * einfacher Text.
   * </p>
   */
  public static final OntProperty SHORTDESC = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#shortdesc");

  /**
   * <p>
   * Un tester u otro proveedor de control de calidad.Tester nebo jiný spoluautor kontrolující
   * kvalitu.Un testeur ou un collaborateur au contrôle qualité.Ein Tester oder anderer Mitarbeiter
   * der Qualitätskontrolle.A tester or other quality control contributor.
   * </p>
   */
  public static final OntProperty TESTER = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#tester");

  /**
   * <p>
   * Spoluautor překladu projektu.Proveedor de traducciones al proyecto.Collaborateur à la
   * traduction du projet.Contributor of translations to the project.Mitarbeiter an den
   * Übersetzungen eines Projektes.
   * </p>
   */
  public static final OntProperty TRANSLATOR = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#translator");

  /**
   * <p>
   * Vendor organization: commercial, free or otherwise
   * </p>
   */
  public static final OntProperty VENDOR = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#vendor");

  /**
   * <p>
   * URL of Wiki for collaborative discussion of project.L'URL du Wiki pour la discussion
   * collaborative sur le projet.URL del Wiki para discusión colaborativa del proyecto.Wiki-URL für
   * die kollaborative Dikussion eines Projektes.URL adresa wiki projektu pro společné diskuse.
   * </p>
   */
  public static final OntProperty WIKI = m_model
      .createOntProperty("http://usefulinc.com/ns/doap#wiki");

  // Vocabulary classes
  // /////////////////////////

  /**
   * <p>
   * GNU Arch Quellcode-Versionierungssystem.Dépôt GNU Arch du code source.GNU Arch source code
   * repository.Repositorio GNU Arch del código fuente.Úložiště zdrojových kódů GNU Arch.
   * </p>
   */
  public static final OntClass ARCH_REPOSITORY = m_model
      .createClass("http://usefulinc.com/ns/doap#ArchRepository");

  /**
   * <p>
   * Dépôt BitKeeper du code source.Úložiště zdrojových kódů BitKeeper.BitKeeper
   * Quellcode-Versionierungssystem.Repositorio BitKeeper del código fuente.BitKeeper source code
   * repository.
   * </p>
   */
  public static final OntClass BKREPOSITORY = m_model
      .createClass("http://usefulinc.com/ns/doap#BKRepository");

  /**
   * <p>
   * Bazaar source code branch.
   * </p>
   */
  public static final OntClass BAZAAR_BRANCH = m_model
      .createClass("http://usefulinc.com/ns/doap#BazaarBranch");

  /**
   * <p>
   * CVS source code repository.Úložiště zdrojových kódů CVS.Repositorio CVS del código fuente.CVS
   * Quellcode-Versionierungssystem.Dépôt CVS du code source.
   * </p>
   */
  public static final OntClass CVSREPOSITORY = m_model
      .createClass("http://usefulinc.com/ns/doap#CVSRepository");

  /**
   * <p>
   * Dépôt darcs du code source.darcs source code repository.Repositorio darcs del código fuente.
   * </p>
   */
  public static final OntClass DARCS_REPOSITORY = m_model
      .createClass("http://usefulinc.com/ns/doap#DarcsRepository");

  /**
   * <p>
   * Repositorio Git del código fuente.Úložiště zdrojových kódů Git.Dépôt Git du code source.Git
   * source code repository.Git Quellcode-Versionierungssystem.
   * </p>
   */
  public static final OntClass GIT_REPOSITORY = m_model
      .createClass("http://usefulinc.com/ns/doap#GitRepository");

  /**
   * <p>
   * Mercurial source code repository.
   * </p>
   */
  public static final OntClass HG_REPOSITORY = m_model
      .createClass("http://usefulinc.com/ns/doap#HgRepository");

  /**
   * <p>
   * Ein Projekt.Projekt.A project.Un proyecto.Un projet.
   * </p>
   */
  public static final OntClass PROJECT = m_model
      .createClass("http://usefulinc.com/ns/doap#Project");

  /**
   * <p>
   * Úložiště zdrojových kódů.Quellcode-Versionierungssystem.Dépôt du code source.Source code
   * repository.Repositorio del código fuente.
   * </p>
   */
  public static final OntClass REPOSITORY_CLASS = m_model
      .createClass("http://usefulinc.com/ns/doap#Repository");

  /**
   * <p>
   * Dépôt Subversion du code source.Úložiště zdrojových kódů Subversion.Subversion
   * Quellcode-Versionierungssystem.Subversion source code repository.Repositorio Subversion del
   * código fuente.
   * </p>
   */
  public static final OntClass SVNREPOSITORY = m_model
      .createClass("http://usefulinc.com/ns/doap#SVNRepository");

  /**
   * <p>
   * A specification of a system's aspects, technical or otherwise.
   * </p>
   */
  public static final OntClass SPECIFICATION = m_model
      .createClass("http://usefulinc.com/ns/doap#Specification");

  /**
   * <p>
   * Versionsinformation eines Projekt Releases.Informace o uvolněné verzi projektu.Détails sur une
   * version d'une realease d'un projet.Información sobre la versión de un release del
   * proyecto.Version information of a project release.
   * </p>
   */
  public static final OntClass VERSION = m_model
      .createClass("http://usefulinc.com/ns/doap#Version");

  // Vocabulary individuals
  // /////////////////////////

}

/*
 * @footer@
 */

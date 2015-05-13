package org.hackystat.linkedservicedata.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Provides access to the values stored in the linkedservicedata.properties file.
 *
 * @author Philip Johnson, Myriam Leggieri
 */
public class ServerProperties {

  /** The sensorbase fully qualified host name, such as http://localhost:9876/sensorbase. */
  public static final String SENSORBASE_FULLHOST_KEY = "linkedservicedata.sensorbase.host";
  /** The DPD host. */
  public static final String DAILYPROJECTDATA_FULLHOST_KEY = "linkedservicedata.dailyprojectdata.host";
  /** The DPD host. */
  public static final String TELEMETRY_FULLHOST_KEY = "linkedservicedata.telemetry.host";

  /** The Hackystat server admin e-mail */
  public static final String ADMIN_EMAIL_KEY = "linkedservicedata.admin.email";
  /** The linkedservicedata hostname key. */
  public static final String HOSTNAME_KEY = "linkedservicedata.hostname";
  /** The linkedservicedata context root. */
  public static final String CONTEXT_ROOT_KEY = "linkedservicedata.context.root";
  /** The logging level key. */
  public static final String LOGGING_LEVEL_KEY = "linkedservicedata.logging.level";
  /** The linkedservicedata port key. */
  public static final String PORT_KEY = "linkedservicedata.port";
  /** The RDF directory key. */
  public static final String RDF_DIR_KEY = "linkedservicedata.rdf.dir";
  /** The Restlet Logging key. */
  public static final String RESTLET_LOGGING_KEY = "linkedservicedata.restlet.logging";
  /** The dpd port key during testing. */
  public static final String TEST_PORT_KEY = "linkedservicedata.test.port";
  /** The test installation key. */
  public static final String TEST_INSTALL_KEY = "linkedservicedata.test.install";
  /** The test installation key. */
  public static final String TEST_HOSTNAME_KEY = "linkedservicedata.test.hostname";
  /** The path of the RDF schema. */
  // public static final String SCHEMA_FULLPATH_KEY = "linkedservicedata.schema.path";

  /** The test installation key. */
  public static final String TEST_SENSORBASE_FULLHOST_KEY = "linkedservicedata.test.sensorbase.host";
  /** The test dpd host key. */
  public static final String TEST_DAILYPROJECTDATA_FULLHOST_KEY = "linkedservicedata.test.dpd.host";
  /** The test telemetry host key. */
  public static final String TEST_TELEMETRY_FULLHOST_KEY = "linkedservicedata.test.telemetry.host";

  /** Indicates whether SensorBaseClient caching is enabled. */
  public static final String CACHE_ENABLED = "linkedservicedata.cache.enabled";
  /** The maxLife in days for each instance in each SensorBaseClient cache. */
  public static final String CACHE_MAX_LIFE = "linkedservicedata.cache.max.life";
  /** The total capacity of each SensorBaseClient cache. */
  public static final String CACHE_CAPACITY = "linkedservicedata.cache.capacity";
  /** Whether or not the front side cache is enabled. */
  public static final String FRONTSIDECACHE_ENABLED = "linkedservicedata.cache.frontside.enabled";

  private String falseString = "false"; // for PMD.

  /** Where we store the properties. */
  private Properties properties;

  /**
   * Creates a new ServerProperties instance. Prints an error to the console if problems occur on
   * loading.
   */
  public ServerProperties() {
    try {
      initializeProperties();
    }
    catch (Exception e) {
      System.out.println("Error initializing server properties: " + e.getMessage());
    }
  }

  /**
   * Reads in the properties in ~/.hackystat/linkedservicedata/linkedservicedata.properties if this
   * file exists, and provides default values for all properties.
   *
   * @throws Exception if errors occur.
   */
  private void initializeProperties() throws Exception {
    String userHome = System.getProperty("user.home");
    String userDir = System.getProperty("user.dir");
    String propFile = userHome + "/.hackystat/linkedservicedata/linkedservicedata.properties";
    this.properties = new Properties();
    // Set defaults
    properties.setProperty(SENSORBASE_FULLHOST_KEY, "http://dasha.ics.hawaii.edu:9876/sensorbase/");
    properties.setProperty(DAILYPROJECTDATA_FULLHOST_KEY,
        "http://dasha.ics.hawaii.edu:9877/dailyprojectdata/");
    properties.setProperty(TELEMETRY_FULLHOST_KEY, "http://dasha.ics.hawaii.edu:9878/telemetry/");
    properties.setProperty(HOSTNAME_KEY, "localhost");
    properties.setProperty(PORT_KEY, "9875");
    properties.setProperty(CONTEXT_ROOT_KEY, "linkedservicedata");
    properties.setProperty(LOGGING_LEVEL_KEY, "INFO");
    properties.setProperty(RESTLET_LOGGING_KEY, falseString);
    properties.setProperty(RDF_DIR_KEY, userDir + "/rdf");
    properties.setProperty(TEST_PORT_KEY, "9875");
    properties.setProperty(TEST_HOSTNAME_KEY, "localhost");
    properties.setProperty(TEST_SENSORBASE_FULLHOST_KEY, "http://localhost:9876/sensorbase/");
    properties.setProperty(TEST_DAILYPROJECTDATA_FULLHOST_KEY,
        "http://localhost:9877/dailyprojectdata/");
    properties.setProperty(TEST_TELEMETRY_FULLHOST_KEY, "http://localhost:9878/telemetry/");
    properties.setProperty(TEST_INSTALL_KEY, falseString);
    properties.setProperty(CACHE_ENABLED, "true");
    properties.setProperty(FRONTSIDECACHE_ENABLED, "true");
    properties.setProperty(CACHE_MAX_LIFE, "365");
    properties.setProperty(CACHE_CAPACITY, "500000");
    // properties.setProperty(SCHEMA_FULLPATH_KEY, "/home/myrtill/Hackystat_linkedData/mysqlProva" +
    // "/workspace/hackystat-linked-service-data/src/org/hackystat/linkedservicedata/" +
    // "vocabularies/hackystat.rdf");
    properties.setProperty(ADMIN_EMAIL_KEY, "myrpandemon@yahoo.it");
    FileInputStream stream = null;
    try {
      stream = new FileInputStream(propFile);
      System.out.println("Loading Linkedservicedata properties from: " + propFile);
      properties.load(stream);
    }
    catch (IOException e) {
      System.out.println(propFile + " not found. Using default linkedservicedata properties.");
    }
    finally {
      if (stream != null) {
        stream.close();
      }
    }

    trimProperties(properties);
    // make sure that SENSORBASE_HOST always has a final slash.
    String sensorBaseHost = (String) properties.get(SENSORBASE_FULLHOST_KEY);
    if (!sensorBaseHost.endsWith("/")) {
      properties.put(SENSORBASE_FULLHOST_KEY, sensorBaseHost + "/");
    }
    // make sure that DAILYPROJECTDATA_HOST always has a final slash.
    String dailyProjectDataHost = (String) properties.get(DAILYPROJECTDATA_FULLHOST_KEY);
    if (!dailyProjectDataHost.endsWith("/")) {
      properties.put(DAILYPROJECTDATA_FULLHOST_KEY, dailyProjectDataHost + "/");
    }
    // make sure that TELEMETRY_HOST always has a final slash.
    String telemetryHost = (String) properties.get(TELEMETRY_FULLHOST_KEY);
    if (!telemetryHost.endsWith("/")) {
      properties.put(TELEMETRY_FULLHOST_KEY, telemetryHost + "/");
    }
    trimProperties(properties);
    // Now add to System properties.
    Properties systemProperties = System.getProperties();
    systemProperties.putAll(properties);
    System.setProperties(systemProperties);
  }

  /**
   * Sets the following properties' values to their "test" equivalent.
   * <ul>
   * <li>HOSTNAME_KEY
   * <li>PORT_KEY
   * <li>SENSORBASE_HOST_KEY
   * <li>DAILYPROJECTDATA_FULLHOST_KEY
   * <li>TELEMETRY_FULLHOST_KEY
   * <li>DEFINITIONS_DIR
   * </ul>
   * Also sets TEST_INSTALL_KEY's value to "true".
   */
  public void setTestProperties() {
    properties.setProperty(HOSTNAME_KEY, properties.getProperty(TEST_HOSTNAME_KEY));
    properties.setProperty(PORT_KEY, properties.getProperty(TEST_PORT_KEY));
    properties.setProperty(SENSORBASE_FULLHOST_KEY, properties
        .getProperty(TEST_SENSORBASE_FULLHOST_KEY));
    properties.setProperty(DAILYPROJECTDATA_FULLHOST_KEY, properties
        .getProperty(TEST_DAILYPROJECTDATA_FULLHOST_KEY));
    properties.setProperty(TELEMETRY_FULLHOST_KEY, properties
        .getProperty(TEST_TELEMETRY_FULLHOST_KEY));
    properties.setProperty(TEST_INSTALL_KEY, "true");
    properties.setProperty(CACHE_ENABLED, falseString);
    properties.setProperty(FRONTSIDECACHE_ENABLED, falseString);
    trimProperties(properties);
  }

  /**
   * Returns the value of the Server Property specified by the key.
   *
   * @param key Should be one of the public static final strings in this class.
   * @return The value of the key, or null if not found.
   */
  public String get(String key) {
    return this.properties.getProperty(key);
  }

  /**
   * Ensures that the there is no leading or trailing whitespace in the property values. The fact
   * that we need to do this indicates a bug in Java's Properties implementation to me.
   *
   * @param properties The properties.
   */
  private void trimProperties(Properties properties) {
    // Have to do this iteration in a Java 5 compatible manner. no stringPropertyNames().
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      String propName = (String) entry.getKey();
      properties.setProperty(propName, properties.getProperty(propName).trim());
    }
  }

  /**
   * Returns the fully qualified host name, such as "http://localhost:9877/linkedservicedata/".
   *
   * @return The fully qualified host name.
   */
  public String getFullHost() {
    return "http://" + get(HOSTNAME_KEY) + ":" + get(PORT_KEY) + "/" + get(CONTEXT_ROOT_KEY) + "/";
  }

  /**
   * Returns true if caching is enabled in this service.
   *
   * @return True if caching enabled.
   */
  public boolean isCacheEnabled() {
    return "True".equalsIgnoreCase(this.properties.getProperty(CACHE_ENABLED));
  }

  /**
   * Returns true if front side caching is enabled in this service.
   *
   * @return True if caching enabled.
   */
  public boolean isFrontSideCacheEnabled() {
    return "True".equalsIgnoreCase(this.properties.getProperty(FRONTSIDECACHE_ENABLED));
  }

  /**
   * Returns the caching max life as a double. If the property has an illegal value, then return the
   * default.
   *
   * @return The max life of each instance in the cache.
   */
  public double getCacheMaxLife() {
    String maxLifeString = this.properties.getProperty(CACHE_MAX_LIFE);
    double maxLife = 0;
    try {
      maxLife = Double.valueOf(maxLifeString);
    }
    catch (Exception e) {
      System.out.println("Illegal cache max life: " + maxLifeString + ". Using default.");
      maxLife = 365D;
    }
    return maxLife;
  }

  /**
   * Returns the in-memory capacity for each cache. If the property has an illegal value, then
   * return the default.
   *
   * @return The in-memory capacity.
   */
  public long getCacheCapacity() {
    String capacityString = this.properties.getProperty(CACHE_CAPACITY);
    long capacity = 0;
    try {
      capacity = Long.valueOf(capacityString);
    }
    catch (Exception e) {
      System.out.println("Illegal cache capacity: " + capacityString + ". Using default.");
      capacity = 500000L;
    }
    return capacity;
  }

  /**
   * Returns a string containing all current properties in alphabetical order.
   *
   * @return A string with the properties.
   */
  public String echoProperties() {
    String cr = System.getProperty("line.separator");
    String eq = " = ";
    String pad = "                ";
    // Adding them to a treemap has the effect of alphabetizing them.
    TreeMap<String, String> alphaProps = new TreeMap<String, String>();
    for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
      String propName = (String) entry.getKey();
      String propValue = (String) entry.getValue();
      alphaProps.put(propName, propValue);
    }
    StringBuffer buff = new StringBuffer(30);
    buff.append("Linkedservicedata Properties:").append(cr);
    for (String key : alphaProps.keySet()) {
      buff.append(pad).append(key).append(eq).append(get(key)).append(cr);
    }
    return buff.toString();
  }

}

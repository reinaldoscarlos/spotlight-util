import com.typesafe.config.ConfigFactory
import org.junit.Assert._
import org.junit.{Before, Test}

/**
 * Tests if the properties file has the needed configuration
 *
 * @author reinaldo
 */
class PropertiesTests {

  val config = ConfigFactory.load;

  @Before
  def checkInputFile {
    assertTrue(config.hasPath("dataSet.inputFile"))
    assertTrue(config.hasPath("dataSet.location"))
    assertTrue(config.hasPath("execution.extraction"))
    assertTrue(config.hasPath("execution.outputFormat"))
    assertTrue(config.hasPath("execution.inputFile"))
    assertTrue(config.hasPath("execution.outputFile"))
  }

  @Test
  def checkPossibleValues {
    assertTrue("object".equals(config.getString("execution.extraction")) || "property".equals(config.getString("execution.extraction")))
    assertTrue("TSV".equals(config.getString("execution.outputFormat")) || "JSON".equals(config.getString("execution.outputFormat")))
  }
}

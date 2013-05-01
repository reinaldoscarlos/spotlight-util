import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.util.FileManager
import java.io.{IOException, InputStream}

/**
 * Allows reading statements from RDF files using Jena.
 * TODO may need refactoring to read from multiple files
 * TODO may need refactoring to store input from files into a DB-backed model
 */
class JenaStatementSource(inputFile: String) extends Traversable[Statement] {
  //Opening the input
  val model: Model = ModelFactory.createDefaultModel
  val input: InputStream = FileManager.get.open(inputFile)
  if (input != null) {
    model.read(input, null, "N-TRIPLE")
  } else {
    throw new IOException("Cannot open input %s".format(inputFile))
  }

  /*
  This method takes as input a function that maps from a Statement to anything
   */
  override def foreach[U](extract : Statement => U) {
    val inputSubjects: ResIterator = model.listSubjects
    while (inputSubjects.hasNext) {
      //Get the subject
      val subject: Resource = inputSubjects.next
      val properties: StmtIterator = subject.listProperties
      while (properties.hasNext) {
        extract(properties.next)
      }
    }
  }
}

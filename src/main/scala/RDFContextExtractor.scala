import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.util.FileManager
import java.io._
import org.apache.lucene.analysis.br.BrazilianAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.util.Version

/**
 * Context Extraction from RDF
 * Used to process the properties and labels and group them by the subject
 * The output is formatted as TSV or JSON
 *
 * @author reinaldo
 */
object RDFContextExtractor extends App {

  /**
   * Choose the format of the output
   * Choose the relevant part to be extracted: object, property or type
   * Fill the input and output file names
   * Check the output to see the extraction
   */

  val format = "TSV"
  //val format = "JSON"

  //Extracting the label of the object
  labelExtraction("object", format, "files/inputs/3.8_sl_en_sl_labels_en.nt", "files/VALUE_OF_OBJECT_3.8_sl_en_sl_labels_en.tsv")
  labelExtraction("object", format, "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt", "files/VALUE_OF_OBJECT_3.8_sl_en_sl_mappingbased_properties_en.tsv")
  labelExtraction("object", format, "files/inputs/3.8_sl_en_sl_instance_types_en.nt","files/VALUE_OF_OBJECT_3.8_sl_en_sl_instance_types_en.tsv")

  //Extracting the label of the property
  labelExtraction("property", format, "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt", "files/VALUE_OF_PROPERTY_3.8_sl_en_sl_mappingbased_properties_en.tsv")

  /*
  Reads in files into a Jena Model, performs context extraction, formatting and outputs context into a file.
  Context extraction can focus on property labels, object labels or object type labels.
  */
  def labelExtraction (partToBeExtracted: String, format: String, inputFile: String, outputFile: String) = {
    //Reading the input
    val source = new JenaStatementSource(inputFile)
    // creating the output
    val output = new PrintStream(outputFile)
    // choosing output format
    val formatter = if (format.equals("TSV")) new TSVOutputFormatter else new PigOutputFormatter
    // choosing extraction strategy
    val extractor = if (partToBeExtracted.equals("object")) new ObjectExtractor else new PropertyExtractor
    // applying over input
    source.groupBy( e => e.getSubject ).flatMap {
      case (subject,statements) => {
        val context = extractor.extract(statements).mkString(" ")
        output.println(formatter.format(subject.getLocalName,context))
        Some((subject,context))
      }
      case _ => None
    }.toSeq
     .sortBy( e => e._1.getLocalName )
    output.close()
  }
}

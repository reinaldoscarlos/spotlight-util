import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.util.FileManager
import java.io._
import org.apache.lucene.analysis.br.BrazilianAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.util.Version

/**
 * Created with IntelliJ IDEA.
 * User: reinaldo
 * Date: 26/04/13
 * Time: 09:57
 * To change this template use File | Settings | File Templates.
 */
object TestingJenaScala extends App {

  /**
   * Choose the format of the output
   * Choose the relevant part to be extracted: object, property or type
   * Fill the input and output file names
   * Check the output to see the extraction
   */

  val format = "TSV"
  //val format = "JSON"

  //object extraction
  extraction("object", format, "files/inputs/3.8_sl_en_sl_labels_en.nt", "files/VALUE_OF_OBJECT_3.8_sl_en_sl_labels_en.tsv")
  extraction("object", format, "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt", "files/VALUE_OF_OBJECT_3.8_sl_en_sl_mappingbased_properties_en.tsv")
  extraction("object", format, "files/inputs/3.8_sl_en_sl_instance_types_en.nt","files/VALUE_OF_OBJECT_3.8_sl_en_sl_instance_types_en.tsv")

  //property extraction
  extraction("property", format, "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt", "files/VALUE_OF_PROPERTY_3.8_sl_en_sl_mappingbased_properties_en.tsv")

  //type extraction
  extraction("type", format, "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt", "files/VALUE_OF_TYPE_3.8_sl_en_sl_mappingbased_properties_en.tsv")

  /*
  Reads in files into a Jena Model, performs context extraction, formatting and outputs context into a file.
  Context extraction can focus on property labels, object labels or object type labels.
  */
  def extraction (partToBeExtracted: String, format: String, inputFile: String, outputFile: String) = {
    //Reading the input
    val source = new JenaStatementSource(inputFile)
    // creating the output
    val output = new PrintStream(outputFile)
    // choosing output format
    val formatter = if (format.equals("TSV")) new TSVOutputFormatter else new PigOutputFormatter
    // choosing extraction strategy
    val extractor = new StatementContextExtractor(partToBeExtracted)
    // applying over input
    source.groupBy( e => e.getSubject ).flatMap {
      case (subject,statements) => {
        val context = extractor.extract(statements).mkString(" ")
        output.println(formatter.format(subject.getURI,context))
        Some((subject,context))
      }
      case _ => None
    }.toSeq
     .sortBy( e => e._1.getURI )

    output.close()


  }


}

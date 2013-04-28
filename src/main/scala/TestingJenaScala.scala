import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.util.FileManager
import java.io.{IOException, StringReader, FileWriter, InputStream}
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
  extraction ("object", format, "files/inputs/3.8_sl_en_sl_labels_en.nt", "files/outputs/VALUE_OF_OBJECT_3.8_sl_en_sl_labels_en.tsv")
  extraction("object", format, "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt", "files/outputs/VALUE_OF_OBJECT_3.8_sl_en_sl_mappingbased_properties_en.tsv")
  extraction("object", format, "files/inputs/3.8_sl_en_sl_instance_types_en.nt","files/outputs/VALUE_OF_OBJECT_3.8_sl_en_sl_instance_types_en.tsv")

  //property extraction
  extraction("property", format, "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt", "files/outputs/VALUE_OF_PROPERTY_3.8_sl_en_sl_mappingbased_properties_en.tsv")

  //type extraction
  extraction("type", format, "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt", "files/outputs/VALUE_OF_TYPE_3.8_sl_en_sl_mappingbased_properties_en.tsv")

  def extraction (partToBeExtracted: String, format: String, inputFile: String, outputFile: String) {

    //Opening the input
    val model: Model = ModelFactory.createDefaultModel
    val input: InputStream = FileManager.get.open(inputFile)
    if (input != null) {
      model.read(input, null, "N-TRIPLE")

      //Reading the input and creating the output
      val outputValue = new StringBuilder
      val inputSubjects: ResIterator = model.listSubjects

      while (inputSubjects.hasNext) {

        //Get the subject
        val subject: Resource = inputSubjects.next
        outputValue.append(subject.getURI)

        //Make the context
        if(format.equals("TSV")) outputValue.append("\t")
        else outputValue.append(" ")
        val context = new StringBuilder
        val properties: StmtIterator = subject.listProperties
        while (properties.hasNext) {
          val property: Statement = properties.next
          if (partToBeExtracted.equals("object"))
            context.append(if (property.getObject.isLiteral) property.getLiteral.getLexicalForm else property.getObject)
          else if (partToBeExtracted.equals("property"))
            context.append(property.getPredicate.getLocalName)
          else{
            if(property.getObject.isLiteral) context.append("literal")
            else if(property.getObject.isURIResource) context.append("named")
            else context.append("anonynous")
          }
          context.append(" ")
        }

        //According the expected format
        if(format.equals("TSV")) outputValue.append(context.toString())
        else outputValue.append(tokenize(context.toString()))

        outputValue.append("\n")

      }
      val output: FileWriter = new FileWriter(outputFile)
      output.append(outputValue)
      output.flush()
      output.close()

      //Uncomment this to check the output
      println(outputValue)

    }
  }

  def tokenize(text: String): String = {
    val result = new StringBuilder("{")
    val map = scala.collection.mutable.Map.empty[String, Int]
    val analyzer = new BrazilianAnalyzer(Version.LUCENE_34)
    val stream: TokenStream = analyzer.tokenStream("field", new StringReader(text))
    try  {
      var key: String = null
      stream.reset()
      while (stream.incrementToken) {
        key = stream.getAttribute(classOf[CharTermAttribute]).toString
        if(map.contains(key)) map(key) = map(key)+1
        else map(key) = 1
      }
      stream.end()
      stream.close()
    }catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    }
    var toRead = map.size
    map foreach {case (key, value) => result.append("("+key+","+value+")"+(if({toRead-=1;toRead}>0)"," else ""))}
    result.append("}")
    result.toString()
  }
}

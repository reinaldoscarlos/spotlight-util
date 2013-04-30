import java.io.{IOException, StringReader}
import java.lang.RuntimeException
import org.apache.lucene.analysis.br.BrazilianAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.util.Version

/*
Prints the output in a given format to an output print stream.
Uses System.out by default.
 */
trait ContextOutputFormatter {
  def format(subject: String, context: String) : String
}

class TSVOutputFormatter extends ContextOutputFormatter {
  def format(subject: String, context: String) = {
    List(subject,context).mkString("\t")
  }
}

class PigOutputFormatter extends ContextOutputFormatter {

  def format(subject: String, context: String) = {
    List(subject,tokenize(context)).mkString("\t")
  }

  /*
  Performs tokenization and counting (frequency of tokens in the input)
  */
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



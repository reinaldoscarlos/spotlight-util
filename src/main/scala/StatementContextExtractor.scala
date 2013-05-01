import com.hp.hpl.jena.rdf.model.Statement

/**
 * Temporary class that extracts different context definitions from a Jena Statement.
 * TODO refactor to have subclasses that behave according to "partToBeExtracted"
 */
class StatementContextExtractor(partToBeExtracted: String) {
  //TODO instead of a long "if", use subclasses with specific behaviors
  def extract(property: Statement) : String = {
    if (partToBeExtracted.equals("object"))
      if (property.getObject.isLiteral)
        property.getLiteral.getLexicalForm
      else
        property.getObject.toString //TODO get label
    else if (partToBeExtracted.equals("property"))
      property.getPredicate.getLocalName //TODO get label (label does not necessarily equal local name)
    else { //TODO Pablo does not know what these below are for. Probable misinterpretation.
      if(property.getObject.isLiteral)
        "literal"
      else if(property.getObject.isURIResource)
        "named"
      else
        "anonynous"
    }
  }

  def extract(statements: Traversable[Statement]) : Traversable[String] = {
    statements.map(s => extract(s))
  }
}

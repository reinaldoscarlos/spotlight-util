import com.hp.hpl.jena.rdf.model._

/**
 * Class that extracts different context definitions from a Jena Statement.
 */
trait StatementContextExtractor {
  def extract(property: Statement) : String
  def extract(statements: Traversable[Statement]) : Traversable[String] = {
    statements.map(s => extract(s))
  }
}

class ObjectExtractor extends StatementContextExtractor {
  def extract(property: Statement) : String = {
    property.getObject.visitWith(new ObjectVisitor).toString
  }

  //Visitor used to get the label according to the object type
  class ObjectVisitor extends RDFVisitor {
    def visitBlank(node: Resource, arg1: AnonId): AnyRef = {
      ""
    }
    def visitLiteral(literal: Literal): AnyRef = {
      literal.getLexicalForm
    }
    def visitURI(resource: Resource, arg1: String): AnyRef = {
      resource.getLocalName
    }
  }
}

class PropertyExtractor extends StatementContextExtractor {
  def extract(property: Statement) : String = {
    property.getPredicate.getLocalName  //TODO get label (label does not necessarily equal local name)
  }
}
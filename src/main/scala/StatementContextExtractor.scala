import com.hp.hpl.jena.ontology.{OntModelSpec, OntModel}
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
    property.getObject.visitWith(new StatementVisitor).toString
  }
}

class PropertyExtractor extends StatementContextExtractor {
  def extract(property: Statement) : String = {
    property.getPredicate.visitWith(new StatementVisitor).toString
    //property.getPredicate.getLocalName
  }
}

//Visitor used to get the label according to the object type
class StatementVisitor extends RDFVisitor {
  def visitBlank(node: Resource, arg1: AnonId): AnyRef = {
    ""
  }
  def visitLiteral(literal: Literal): AnyRef = {
    literal.getLexicalForm
  }
  def visitURI(resource: Resource, uri: String): AnyRef = {

    // TODO: use SDB or TDB and store the labels from OWL or NT at a database
    val base: OntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null)
    val language = null //"EN" "FR"...
    base.read("file:files/inputs/dbpedia_3.8.owl")
    base.read("file:files/inputs/3.8_sl_en_sl_labels_en.nt")
    if (base.getOntClass(uri)!=null && base.getOntClass(uri).getLabel(language)!=null)
      base.getOntClass(uri).getLabel(language)
    else if (base.getOntResource(uri)!=null && base.getOntResource(uri).getLabel(language)!=null)
      base.getOntResource(uri).getLabel(language)
    else
      resource.getLocalName
  }
}
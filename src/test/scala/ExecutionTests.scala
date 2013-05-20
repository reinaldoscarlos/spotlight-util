import io.Source
import java.io.{File}
import org.junit.Assert._
import org.junit.{ After, Before, Test}

/**
 * Tests the functions of the project.
 *
 * @author reinaldo
 */
class ExecutionTests {

  @Before
  def createDatabases {

    DataConn.create_TDB_Filesystem("files/outputs/tdb_nt", "files/inputs/3.8_sl_en_sl_labels_en.nt")
    DataConn.create_TDB_Filesystem("files/outputs/tdb_owl", "files/inputs/dbpedia_3.8.owl")

    DataConn.get_TDB_Filesystem("files/outputs/tdb_nt")
    assertEquals("AlbaniaHistory", DataConn.executeQuery("http://dbpedia.org/resource/AlbaniaHistory"))

    DataConn.get_TDB_Filesystem("files/outputs/tdb_owl")
    assertEquals("anatomical structure", DataConn.executeQuery("http://dbpedia.org/ontology/AnatomicalStructure"))
  }

  @Test
  def execution{
    extractObjectsFromMapBasPropertiesAndNtDatabase
    extractObjectsFromTypesAndNtDatabase
    extractObjectsFromTypesAndOwlDatabase
    extractPropertiesFromMapBasPropertiesAndOwlDatabase
  }

  def extractObjectsFromMapBasPropertiesAndNtDatabase {

    DataConn.get_TDB_Filesystem("files/outputs/tdb_nt")
    RDFContextExtractor.labelExtraction("object", "TSV", "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt",
      "files/outputs/objectsFromMapBasPropertiesAndNtDatabase.tsv")

    val source = Source.fromFile("files/outputs/objectsFromMapBasPropertiesAndNtDatabase.tsv")
    val lines = source.mkString
    assertEquals("""Aristotle	Metaphysics Theatre Biology  Galileo_Galilei List of writers influenced by Aristotle Alexander_the_Great Albertus_Magnus Christian_philosophy Socrates , Aristotélēs Science Music Ethics Parmenides Politics Democritus Zoology -0384 Duns_Scotus Rhetoric Peripatetic_school Western_philosophy Thomas_Aquinas Physics Government Avicenna Reason Logic Nicolaus_Copernicus Western_philosophy Aristotelianism Syllogism Heraclitus Ptolemy -0322 Poetry Jewish_philosophy Maimonides Plato Ancient_philosophy Islamic_philosophy Averroes
Animal_Farm	Animal Farm: A Fairy Story George_Orwell 53163540 112 Nineteen_Eighty-Four ISBN 0-452-28424-4 (present) ISBN 978-0-452-28424-1 Harvill_Secker 823/.912 20 _Socialism_and_the_English_Genius PR6029.R8 A63 2003b Animal Farm
Autism	Autism D001321 3202 med 001526 209850 299.00 1142
Alabama	_Alabama English_American Alabamian State of Alabama United_States
""", lines)
    source.close
  }

  def extractObjectsFromTypesAndNtDatabase {

    DataConn.get_TDB_Filesystem("files/outputs/tdb_nt")
    RDFContextExtractor.labelExtraction("object", "TSV", "files/inputs/3.8_sl_en_sl_instance_types_en.nt",
      "files/outputs/objectsFromTypesAndNtDatabase.tsv")

    val source = Source.fromFile("files/outputs/objectsFromTypesAndNtDatabase.tsv")
    val lines = source.mkString
    assertEquals("""Allan_Dwan	Agent Person Thing Person Person
Allan_Dwan__2	Thing PersonFunction
Academy_Award_for_Best_Art_Direction	Thing Award
Actrius	Thing CreativeWork Work Movie Film
Animal_Farm	CreativeWork Work WrittenWork Thing Book Book Book
Abraham_Lincoln	Agent Person Thing Person Person OfficeHolder
Allan_Dwan__1	Thing PersonFunction
Alain_Connes	Agent Person Thing Person Person Scientist
Academy_Award	Thing Award
Alabama	Thing Place Place PopulatedPlace AdministrativeArea AdministrativeRegion
Autism	Thing Disease
Aristotle	Agent Person Thing Person Person Philosopher
America_the_Beautiful	Thing CreativeWork Work Musical Work MusicRecording Song
Ayn_Rand	Artist Writer
Allan_Dwan__3	Thing PersonFunction
""", lines)
    source.close
  }

  def extractObjectsFromTypesAndOwlDatabase {

    DataConn.get_TDB_Filesystem("files/outputs/tdb_owl")
    RDFContextExtractor.labelExtraction("object", "TSV", "files/inputs/3.8_sl_en_sl_instance_types_en.nt",
      "files/outputs/objectsFromTypesAndOwlDatabase.tsv")

    val source = Source.fromFile("files/outputs/objectsFromTypesAndOwlDatabase.tsv")
    val lines = source.mkString
    assertEquals("""Allan_Dwan	agent Person Thing Person Person
Allan_Dwan__2	Thing person function
Academy_Award_for_Best_Art_Direction	Thing Auszeichnung
Actrius	Thing CreativeWork Werk Movie Film
Animal_Farm	CreativeWork Werk written work Thing Book Book Buch
Abraham_Lincoln	agent Person Thing Person Person Amtsinhaber
Allan_Dwan__1	Thing person function
Alain_Connes	agent Person Thing Person Person Wissenschaftler
Academy_Award	Thing Auszeichnung
Alabama	Thing Place Ort populated place AdministrativeArea administrative region
Autism	Thing Krankheit
Aristotle	agent Person Thing Person Person Philosoph
America_the_Beautiful	Thing CreativeWork Werk musical work MusicRecording song
Ayn_Rand	Künstler 작가
Allan_Dwan__3	Thing person function
""", lines)
    source.close
  }

  def extractPropertiesFromMapBasPropertiesAndOwlDatabase {

    DataConn.get_TDB_Filesystem("files/outputs/tdb_owl")
    RDFContextExtractor.labelExtraction("property", "JSON", "files/inputs/3.8_sl_en_sl_mappingbased_properties_en.nt",
      "files/outputs/propertiesFromMapBasPropertiesAndOwlDatabase.tsv")

    val source = Source.fromFile("files/outputs/propertiesFromMapBasPropertiesAndOwlDatabase.tsv")
    val lines = source.mkString
    assertEquals("""Aristotle	{(death,1),(περιοχή,1),(birth,1),(nam,1),(interest,11),(by,5),(year,2),(notableide,4),(main,11),(era,1),(influenced,21),(philosophicalschool,2)}
Animal_Farm	{(nam,2),(decimal,1),(author,1),(dewey,1),(previous,1),(classification,1),(work,2),(of,1),(subsequent,1),(numb,1),(pag,1),(isbn,1),(oclc,1),(herausgeb,1),(lcc,1)}
Autism	{(nam,1),(medlineplus,1),(subject,1),(icd9,1),(mesh,1),(omim,1),(id,2),(emedicin,2),(diseasesdb,1),(topic,1)}
Alabama	{(nam,1),(hauptstadt,1),(demonym,1),(χώρα,1),(sprach,1)}
""", lines)
    source.close
  }

  @After
  def removeOutputFilesOfTest {

    assertTrue(new File("files/outputs/propertiesFromMapBasPropertiesAndOwlDatabase.tsv").delete)
    assertTrue(new File("files/outputs/objectsFromTypesAndNtDatabase.tsv").delete)
    assertTrue(new File("files/outputs/objectsFromTypesAndOwlDatabase.tsv").delete)
    assertTrue(new File("files/outputs/objectsFromMapBasPropertiesAndNtDatabase.tsv").delete)

  }

}

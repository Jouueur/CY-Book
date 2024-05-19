import org.apache.jena.query.*;

import java.util.Scanner;
import org.apache.jena.rdf.model.RDFNode;
//search by ark fonctionne pas
//enlever la casse avec les accents

public class Main {

    private static final String sparqlEndpoint = "https://data.bnf.fr/sparql";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez titre à rechercher : ");
        String titre = scanner.nextLine().toLowerCase().trim();

        System.out.print("Entrez auteur (nom) à rechercher : ");
        String auteur = scanner.nextLine().toLowerCase().trim();

        System.out.print("Entrez auteur (prénom) à rechercher : ");
        String prenomauteur = scanner.nextLine().toLowerCase().trim();

        System.out.print("Entrez l'ISBN à rechercher : ");
        String isbn = scanner.nextLine().trim();
        String ark = "12148/cb37706266g";
        // searchEditions();
        searchByTitle(titre);
        searchByTitleAndAuthor(titre, auteur);
        searchByAuthorLastName(auteur);
        searchByAuthorFullName(auteur, prenomauteur);
        searchByISBN(isbn);
        searchEditionsByTitleAndAuthor(titre,auteur);
        System.out.println("cherchelivreparark");
        searchByARK(ark);

        // System.out.println(getArkNumber("http://data.bnf.fr/ark:/12148/cb35965644b#about"));
    }

    //donne un titre et renvoie tous les livres dont le titre contient la var titre
    private static void searchByTitle(String title) {
        String query = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "SELECT DISTINCT ?titre ?nomAuteur ?auteur\n" +
                "WHERE {\n" +
                "  ?oeuvre dcterms:title ?titre.\n" +
                "  FILTER regex(?titre, \"" + title.toLowerCase() + "\", \"i\").\n" +
                "  ?oeuvre dcterms:creator ?auteur.\n" +
                " ?auteur foaf:familyName ?nomAuteur. \n" +
                "}";

        executeQuery(query);
    }
    // cherche un livre avec son titre et son auteur
    private static void searchByTitleAndAuthor(String title, String author) {
        String query = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "SELECT DISTINCT ?titre ?nomauteur ?auteur\n" +
                "WHERE {\n" +
                "  ?oeuvre dcterms:title ?titre.\n" +
                "  FILTER regex(?titre, \"" + title + "\", \"i\").\n" +
                "  ?oeuvre dcterms:creator ?auteur.\n" +
                " ?auteur foaf:familyName ?nomauteur. \n" +
                "  FILTER regex(?nomauteur, \"" + author + "\", \"i\").\n" +
                "}";

        executeQuery(query);
    }
    // renvoie liste de toutes les editions et les ark du livre donné
    private static void searchEditionsByARK(String oeuvreURI) {
        String queryEditions =
                "PREFIX dcterms: <http://purl.org/dc/terms/> " +
                        "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                        "PREFIX rdarelationships: <http://rdvocab.info/RDARelationshipsWEMI/> " +
                        "PREFIX bibo: <http://purl.org/ontology/bibo/> " +
                        "SELECT DISTINCT ?edition ?titre ?date ?editeur ?isbn " +
                        "WHERE { " +
                        "  { " +
                        "    SELECT DISTINCT ?edition ?titre ?date ?editeur " +
                        "    WHERE { " +
                        "      <" + oeuvreURI + "> foaf:focus ?Oeuvre . " +
                        "      ?edition rdarelationships:workManifested ?Oeuvre. " +
                        "      OPTIONAL { ?edition dcterms:date ?date } " +
                        "      OPTIONAL { ?edition dcterms:title ?titre } " +
                        "      OPTIONAL { ?edition dcterms:publisher ?editeur } " +
                        "    } " +
                        "    LIMIT 100 " +
                        "  } " +
                        "  OPTIONAL { ?edition bibo:isbn ?isbn } " +
                        "}";
        Query query = QueryFactory.create(queryEditions);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                RDFNode edition = soln.get("edition");
                RDFNode titre = soln.get("titre");
                RDFNode date = soln.get("date");
                RDFNode editeur = soln.get("editeur");

                String editionURIResult = edition != null ? edition.toString() : "N/A";
                String arkNumber = getArkNumber(editionURIResult);
                System.out.println("Titre: " + (titre != null ? titre.toString() : "N/A"));
                System.out.println("ARK: " + arkNumber);
                System.out.println("Date: " + (date != null ? date.toString() : "N/A"));
                System.out.println("Editeur: " + (editeur != null ? editeur.toString() : "N/A"));
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //donne tous les titres dont le nom de l auteur est donné
    private static void searchByAuthorLastName(String author) {
        String query = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "SELECT DISTINCT ?titre ?nomAuteur \n" +
                "WHERE {\n" +
                "  ?oeuvre dcterms:title ?titre.\n" +
                "  ?oeuvre dcterms:creator ?auteur.\n" +
                " ?auteur foaf:familyName ?nomAuteur. \n" +
                "  FILTER regex(?nomAuteur, \"" + author + "\", \"i\").\n" +
                "} LIMIT 100" ;

        executeQuery(query);
    }
    //cherche tous les titres de l auteur dont le prenom et le nom est donné
    private static void searchByAuthorFullName(String author, String firstName) {
        String query = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "SELECT DISTINCT ?titre ?nomAuteur ?prenomAuteur\n" +
                "WHERE {\n" +
                "  ?oeuvre dcterms:title ?titre.\n" +
                "  ?oeuvre dcterms:creator ?auteur.\n" +
                " ?auteur foaf:familyName ?nomAuteur. \n" +
                " ?auteur foaf:givenName ?prenomAuteur.\n" +
                "  FILTER regex(?nomAuteur, \"" + author + "\", \"i\").\n" +
                "FILTER regex(?prenomAuteur, \"" + firstName + "\", \"i\").\n" +
                "}";

        executeQuery(query);
    }
    // donne un isbn et renvoie le titre et l auteur du livre
    private static void searchByISBN(String isbn) {
        String query = "PREFIX bnf-onto: <http://data.bnf.fr/ontology/bnf-onto/>\n" +
                "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX rdarelationships: <http://rdvocab.info/RDARelationshipsWEMI/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "SELECT DISTINCT ?title ?auteur ?work \n" +
                "WHERE {\n" +
                "  ?manifestation bnf-onto:isbn \"" + isbn + "\" ;\n" +
                "                 rdarelationships:workManifested ?work.\n" +
                "  ?work rdfs:label ?title;\n" +
                "        dcterms:creator ?creator.\n" +
                "  ?creator foaf:name ?auteur.\n" +
                "} LIMIT 100";

        executeQuery(query);
    }
    // renvoie toutes les editions en donnant le lien ark de l edition

    private static void searchEditions() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez le lien ARK de l'édition : ");
        String editionURI = scanner.nextLine();

        String queryEditions =
                "PREFIX dcterms: <http://purl.org/dc/terms/> " +
                        "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                        "PREFIX rdarelationships: <http://rdvocab.info/RDARelationshipsWEMI/> " +
                        "PREFIX bibo: <http://purl.org/ontology/bibo/> " +
                        "SELECT DISTINCT ?edition ?titre ?date ?editeur ?isbn " +
                        "WHERE { " +
                        "  { " +
                        "    SELECT DISTINCT ?edition ?titre ?date ?editeur " +
                        "    WHERE { " +
                        editionURI + " foaf:focus  ?Manifestation.  " +
                        "      ?edition rdarelationships:workManifested ?Manifestation. " +
                        "      OPTIONAL { ?edition dcterms:date ?date } " +
                        "      OPTIONAL { ?edition dcterms:title ?titre } " +
                        "      OPTIONAL { ?edition dcterms:publisher ?editeur } " +
                        "    } " +
                        "    LIMIT 100 " +
                        "  } " +
                        "  OPTIONAL { ?edition bibo:isbn ?isbn } " +
                        "}";

        Query query = QueryFactory.create(queryEditions);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query)) {
            ResultSet results = qexec.execSelect();

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                RDFNode edition = soln.get("edition");
                RDFNode titre = soln.get("titre");
                RDFNode date = soln.get("date");
                RDFNode editeur = soln.get("editeur");

                String editionURIResult = edition != null ? edition.toString() : "N/A";
                String arkNumber = getArkNumber(editionURIResult);

                System.out.println("Titre: " + (titre != null ? titre.toString() : "N/A"));
                System.out.println("ARK: " + arkNumber);
                System.out.println("Date: " + (date != null ? date.toString() : "N/A"));
                System.out.println("Editeur: " + (editeur != null ? editeur.toString() : "N/A"));
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String getArkNumber(String editionURI) {
        if (editionURI.contains("ark:/")) {
            int startIndex = editionURI.indexOf("ark:/")  + "ark:/".length();;
            int endIndex = editionURI.indexOf("#", startIndex);
            if (endIndex == -1) {
                endIndex = editionURI.length();
            }
            return editionURI.substring(startIndex, endIndex);
        }
        return "N/A";
    }
    //chercher les differentes editions et differents numero ark pour un livre précis
    private static void searchEditionsByTitleAndAuthor(String title, String author) {
        // Recherche de l'URI de l'œuvre correspondant au titre et à l'auteur
        String queryTitleAndAuthor =
                "PREFIX dcterms: <http://purl.org/dc/terms/> " +
                        "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                        "SELECT DISTINCT ?oeuvre " +
                        "WHERE { " +
                        "  ?oeuvre dcterms:title ?titre. " +
                        "  FILTER regex(?titre, \"" + title + "\", \"i\"). " +
                        "  ?oeuvre dcterms:creator ?auteur. " +
                        "  ?auteur foaf:familyName ?nomauteur. " +
                        "  FILTER regex(?nomauteur, \"" + author + "\", \"i\"). " +
                        "}";

        // Exécution de la requête SPARQL pour obtenir l'URI de l'œuvre,
        //on n utilise pas la fonction queryexecution car on ne veut pas la fermer apres
        //on veut pouvoir recueper l oeuvre
        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService(sparqlEndpoint, queryTitleAndAuthor)) {
            ResultSet resultSet = queryExecution.execSelect();

            // Si des résultats sont trouvés, récupérer l'URI de l'œuvre et rechercher les éditions correspondantes
            if (!resultSet.hasNext()) {
                System.out.println("Aucun résultat trouvé.");
            } else {
            while (resultSet.hasNext()) {
                //recuperer solution est la stock dans soln ( soln reprensente un resultat retourné par sparql) recuperer les valeurs rdf
                QuerySolution soln = resultSet.nextSolution();
                //recuperer le noeud rdf de l oeuvre a partir de la solution
                RDFNode oeuvreNode = soln.get("oeuvre");
                // Convertir le noeud RDF en URI sous forme de chaîne (String)
                String oeuvreURI = oeuvreNode != null ? oeuvreNode.toString() : null;
                // Si l'URI de l'œuvre est disponible, rechercher les éditions correspondantes
                if (oeuvreURI != null) {
                    //extraire numero ark de l uri de l oeuvre
                    String arkNumber = getArkNumber(oeuvreURI);
                    // Construire le lien ARK complet
                    String fullArkLink = "http://data.bnf.fr/ark:/" + arkNumber;
                    // System.out.println(fullArkLink);
                    // Rechercher les éditions correspondantes en utilisant le lien ARK complet
                    searchEditionsByARK(fullArkLink);
                } else {
                    System.out.println("Aucune œuvre correspondant au titre et à l'auteur spécifiés.");
                }

        } }}catch (Exception e) {
            e.printStackTrace();
        }
    }
    //trouver un livre en donnant son numero ark marche pas
    private static void searchByARK(String arkNumber) {
        // Construire l'URI complet à partir du numéro ARK
        String arkURI = "https://catalogue.bnf.fr/ark:/" + arkNumber;

        // Construire la requête SPARQL pour récupérer le titre et l'auteur de la notice
        String queryByARK =
                "PREFIX dcterms: <http://purl.org/dc/terms/> " +
                        "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                        "PREFIX rdarelationships: <http://rdvocab.info/RDARelationshipsWEMI/> " +
                        "SELECT DISTINCT ?title ?authorName ?publisher ?date " +
                        "WHERE { " +
                        "  <'" + arkURI + "'> a <http://rdvocab.info/uri/schema/FRBRentitiesRDA/Manifestation> ; " +
                        "                  dcterms:title ?title ; " +
                        "                  dcterms:creator ?author ; " +
                        "                  dcterms:publisher ?publisher ; " +
                        "                  dcterms:date ?date . " +
                        "  ?author foaf:name ?authorName . " +
                        "}";


        // Exécuter la requête SPARQL et traiter les résultats
        executeQuery(queryByARK);
    }


    private static ResultSet executeQuery(String query) {
        try (QueryExecution queryExecution = QueryExecutionFactory.sparqlService(sparqlEndpoint, query)) {
            ResultSet resultSet = queryExecution.execSelect();
            ResultSetFormatter.out(System.out, resultSet);
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

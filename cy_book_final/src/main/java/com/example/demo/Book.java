package com.example.demo;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.StringReader;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Represents a book retrieved from the BnF (Bibliothèque nationale de France) catalogue.
 */
public class Book {
    private String ARK;
    private String title;
    private String author_first_name;
    private String author_last_name;
    private String edition;
    private static final String sparqlEndpoint = "https://data.bnf.fr/sparql";

// ----------------------------------- Constructors, Getters & Setters -------------------------------------------------

    /**
     * Constructs a Book object with the specified parameters.
     *
     * @param title            the title of the book.
     * @param author_first_name the first name of the author.
     * @param author_last_name  the last name of the author.
     * @param ARK              the ARK (Archival Resource Key) identifier of the book.
     * @param edition          the edition details of the book.
     */
    public Book(String title, String author_first_name, String author_last_name, String ARK, String edition) {
        this.title = title;
        this.author_first_name = author_first_name;
        this.author_last_name = author_last_name;
        this.ARK = ARK;
        this.edition = edition;
    }

    /**
     * Retrieves the ARK (Archival Resource Key) identifier of the book.
     *
     * @return the ARK identifier of the book.
     */
    public String getARK() {
        return this.ARK;
    }

    /**
     * Retrieves the title of the book.
     *
     * @return the title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the first name of the author of the book.
     *
     * @return the first name of the author.
     */
    public String getAuthorFirstName() {
        return author_first_name;
    }

    /**
     * Retrieves the last name of the author of the book.
     *
     * @return the last name of the author.
     */
    public String getAuthorLastName() {
        return author_last_name;
    }

    /**
     * Retrieves the edition details of the book.
     *
     * @return the edition details of the book.
     */
    public String getEdition() {
        return edition;
    }

    /**
     * Récupère la liste des livres empruntés par un utilisateur donné.
     *
     * @param user       L'utilisateur pour lequel récupérer les livres empruntés.
     * @param connection Connexion à la base de données.
     * @return Une liste d'objets Book représentant les livres empruntés par l'utilisateur.
     */
    public static ArrayList<Book> getBorrowedBooks(User user, Connection connection) {
        ArrayList<String> isbnList = new ArrayList<>();
        String query = "SELECT isbn FROM borrowing WHERE id_customer = ? AND borrowed = 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, user.userConnection(connection)); // Assurez-vous que la méthode userConnection() retourne l'ID de l'utilisateur
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    isbnList.add(rs.getString("isbn"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<Book> books = new ArrayList<>();
        for (String isbn : isbnList) {
            books.addAll(Book.searchByArk(isbn));
        }

        return books;
    }

//-------------------------------------- methods API ---------------------------------------------

    /**
     * Executes an HTTP GET request to the specified URL and returns the response as a string.
     *
     * @param urlString the URL to send the GET request to.
     * @return the response from the server as a string.
     * @throws IOException if an I/O error occurs.
     */
    /**
     * Executes an HTTP GET request to the specified URL and retrieves the server's response.
     *
     * @param urlString the URL to which the HTTP request is sent.
     * @return the server's response as a string.
     * @throws IOException if an I/O error occurs while sending or receiving the HTTP request/response.
     */
    public static String executeHttpRequest(String urlString) throws IOException {
        StringBuilder response = new StringBuilder();
        // A class used to create mutable string objects.
        // Unlike String, StringBuilder objects can be modified without creating new objects every time.
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        // .openConnection(): Opens a connection to the specified URL.
        conn.setRequestMethod("GET");
        // This is a GET request, used to retrieve data from the server.

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            // Opens an input stream from the HTTP connection, allowing to read the server's response.
            String line;
            while ((line = reader.readLine()) != null) {
                // Retrieves a line from the BufferedReader and adds it to the response
                response.append(line);
            }
        }
        // Converts the content of the StringBuilder to a string
        return response.toString();
    }


    /**
     * Parses the given XML string into a Document object.
     *
     * @param xmlString the XML string to parse.
     * @return the parsed Document object.
     * @throws Exception if a parsing error occurs.
     */
    public static Document parseXmlString(String xmlString) throws Exception {
        // newInstance creates a new instance of DocumentBuilder
        // DocumentBuilderFactory is a class used for parsing XML documents
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Parses the provided XML content
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlString)));
    }


///////////////////////////////////////////////// Extract methods //////////////////////////////////////////////////////////////////////
    /**
     * Extracts and retrieves the title from the given XML record element.
     *
     * @param record the XML record element containing the title information.
     * @return the extracted title as a string.
     */
    public static String extractAndDisplayTitle(Element record) {
        // Initialize an empty string to store the title
        String title = "";

        // Returns all child elements of record with the mxc tag...
        NodeList titleList = record.getElementsByTagName("mxc:datafield");
        for (int j = 0; j < titleList.getLength(); j++) {
            // Casts the j-th element to Element and stores it in datafield
            Element datafield = (Element) titleList.item(j);
            // Searches for the title field with the tag 200
            if ("200".equals(datafield.getAttribute("tag"))) {
                // Checks the tag attributes with value 200
                // 200 in UNIMARC represents the title in the XML schema
                NodeList subfields = datafield.getElementsByTagName("mxc:subfield");
                // Iterates through the elements retrieved in subfields
                for (int k = 0; k < subfields.getLength(); k++) {
                    // Casts the k-th element of subfields to Element
                    Element subfield = (Element) subfields.item(k);
                    // Searches for the subfield 'a' which represents the main title in UNIMARC
                    if ("a".equals(subfield.getAttribute("code"))) {
                        // If subfield has 'a', store the title
                        title = subfield.getTextContent();
                    }
                }
            }
        }

        // Return the extracted title
        return title;
    }

    /**
     * Extracts and retrieves the first name of the author from the given XML record element.
     *
     * @param record the XML record element containing the author's information.
     * @return the extracted first name of the author as a string.
     */
    public static String extractAuthorFirstName(Element record) {
        NodeList authorList = record.getElementsByTagName("mxc:datafield");
        for (int j = 0; j < authorList.getLength(); j++) {
            Element datafield = (Element) authorList.item(j);
            if ("700".equals(datafield.getAttribute("tag"))) {
                NodeList subfields = datafield.getElementsByTagName("mxc:subfield");
                for (int k = 0; k < subfields.getLength(); k++) {
                    Element subfield = (Element) subfields.item(k);
                    if ("a".equals(subfield.getAttribute("code"))) {
                        return subfield.getTextContent();
                    }
                }
            }
        }
        return "Author First Name not available";
    }

    /**
     * Extracts and retrieves the last name of the author from the given XML record element.
     *
     * @param record the XML record element containing the author's information.
     * @return the extracted last name of the author as a string.
     */
    public static String extractAuthorLastName(Element record) {
        NodeList authorList = record.getElementsByTagName("mxc:datafield");
        for (int j = 0; j < authorList.getLength(); j++) {
            Element datafield = (Element) authorList.item(j);
            if ("700".equals(datafield.getAttribute("tag"))) {
                NodeList subfields = datafield.getElementsByTagName("mxc:subfield");
                for (int k = 0; k < subfields.getLength(); k++) {
                    Element subfield = (Element) subfields.item(k);
                    if ("b".equals(subfield.getAttribute("code"))) {
                        return subfield.getTextContent();
                    }
                }
            }
        }
        return "Author Last Name not available";
    }

    /**
     * Extracts and retrieves the ARK number from the given XML record element.
     *
     * @param record the XML record element containing the ARK information.
     * @return the extracted ARK number as a string.
     */
    private static String extractArk(Element record) {
        NodeList recordIdentifierList = record.getElementsByTagName("srw:recordIdentifier");
        for (int i = 0; i < recordIdentifierList.getLength(); i++) {
            Element recordIdentifier = (Element) recordIdentifierList.item(i);
            String ark = recordIdentifier.getTextContent().trim();
            if (ark.startsWith("ark:/")) {
                return ark;
            }
        }
        // If no ARK is found, return an appropriate message
        return "ARK: Not specified";
    }

    /**
     * Extracts and retrieves the edition information from the given XML record element.
     *
     * @param record the XML record element containing the edition details.
     * @return the extracted edition information as a string, formatted as "Place, Publisher, Date".
     * If the edition information is not available, returns an empty string.
     */
    public static String extractAndDisplayEdition(Element record) {
        String edition="";
        // Retrieves all child elements of the record with the "mxc:datafield" tag
        NodeList editionList = record.getElementsByTagName("mxc:datafield");
        for (int j = 0; j < editionList.getLength(); j++) {
            Element datafield = (Element) editionList.item(j);
            if ("210".equals(datafield.getAttribute("tag"))) {
                String place = "";
                String publisher = "";
                String date = "";
                NodeList subfields = datafield.getElementsByTagName("mxc:subfield");
                for (int k = 0; k < subfields.getLength(); k++) {
                    Element subfield = (Element) subfields.item(k);
                    if ("a".equals(subfield.getAttribute("code"))) {
                        place = subfield.getTextContent();
                    }
                    if ("c".equals(subfield.getAttribute("code"))) {
                        publisher = subfield.getTextContent();
                    }
                    if ("d".equals(subfield.getAttribute("code"))) {
                        date = subfield.getTextContent();
                    }
                }
                // Display the extracted edition details
                edition = place + ", " + publisher + ", " + date;
            }
        }
        return edition;
    }

////////////////////////////////////////////////////// Search methods ///////////////////////////////////////////////////////////////

    /**
     * Searches for books by title and author using the BnF SRU API and returns the results as a list of books.
     *
     * @param title  the title of the book to search for.
     * @param author the author of the book to search for.
     * @return an ArrayList containing the books found matching the provided title and author.
     *         If no books are found or an error occurs during the search process, an empty ArrayList is returned.
     */
    public static ArrayList<Book> searchByTitleAndAuthor(String title, String author) {
        // Initialize an ArrayList to store the books found
        ArrayList<Book> books = new ArrayList<>();

        try {

            // Encoder le titre et l'auteur pour les utiliser dans une requÃƒÂªte URL
            String queryTitle = URLEncoder.encode("bib.title all \"" + title + "\"", "UTF-8");
            String queryAuthor = URLEncoder.encode("bib.author all \"" + author + "\"", "UTF-8");

            // URL de base pour l'API SRU de la BnF
            int maximumRecords = 100;
            String baseUrl = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=";
            // %20and%20 remplace le AND
            String url = baseUrl + queryTitle + "%20AND%20" + queryAuthor + "&maximumRecords=" + maximumRecords;


            // String url = baseUrl + "(" + queryTitle + ") and (" + queryAuthor + ")";
            String response = executeHttpRequest(url);
            Document document = parseXmlString(response);

            // Impression de dÃƒÂ©bogage pour voir la rÃƒÂ©ponse XML
            System.out.println("Response for title and author search: \n" );
            //System.out.println(response);

            NodeList records = document.getElementsByTagName("srw:record");

            for (int i = 0; i < records.getLength(); i++) {
                Element record = (Element) records.item(i);
                String bookTitle = extractAndDisplayTitle(record); // RÃ©cupÃ©rer le titre
                String bookAuthorFirstName = extractAuthorFirstName(record);
                String bookAuthorLastName = extractAuthorLastName(record);
                String bookEdition = extractAndDisplayEdition(record);
                String bookArk = extractArk(record);

                // CrÃ©ation d'un nouvel objet Book et ajout Ã  la liste
                Book book = new Book(bookTitle, bookAuthorLastName, bookAuthorFirstName, bookArk, bookEdition);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the ArrayList of books
        return books;
    }

    /**
     * Searches for records by ARK number using the BnF SRU API and retrieves the corresponding books.
     *
     * @param arkNumber the ARK number to search for.
     * @return an ArrayList of Book objects corresponding to the records found.
     * Each Book object contains information such as title, author, edition, and ARK.
     */
    public static ArrayList<Book> searchByArk(String arkNumber) {

        ArrayList<Book> books = new ArrayList<>();

        try {
            String queryArk = URLEncoder.encode("bib.persistentid any \"" + arkNumber + "\"", "UTF-8");
            String baseUrl = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=";
            String urlArk = baseUrl + queryArk;

            String response = executeHttpRequest(urlArk);
            Document document = parseXmlString(response);

            NodeList records = document.getElementsByTagName("srw:record");

            for (int i = 0; i < records.getLength(); i++) {
                Element record = (Element) records.item(i);
                String bookTitle = extractAndDisplayTitle(record);
                String bookAuthorFirstName = extractAuthorFirstName(record);
                String bookAuthorLastName = extractAuthorLastName(record);
                String bookEdition = extractAndDisplayEdition(record);
                String bookArk = extractArk(record);


                Book book = new Book(bookTitle,bookAuthorLastName,bookAuthorFirstName, bookArk,bookEdition);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;

    }

    /**
     * Searches for records by author's last name and first name using the BnF SRU API and retrieves the corresponding books.
     *
     * @param lastName  the last name of the author to search for.
     * @param firstName the first name of the author to search for.
     * @return an ArrayList of Book objects corresponding to the records found.
     * Each Book object contains information such as title, author, edition, and ARK.
     */
    public static ArrayList<Book> searchByAuthor(String lastName,String firstName) {
        ArrayList<Book> books = new ArrayList<>();

        String author = "";
        try {
            // Encode the author to use it in a URL request
            String queryAuthor = URLEncoder.encode("bib.author all \"" + lastName + ", " + firstName + "\"", "UTF-8");
            String baseUrl = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve";
            int maximumRecords = 100; // Nombre maximal de rÃ©sultats Ã  rÃ©cupÃ©rer, ajustez selon vos besoins
            String urlAuthor = baseUrl + "&query=" + queryAuthor + "&maximumRecords=" + maximumRecords;
            String response = executeHttpRequest(urlAuthor);
            Document document = parseXmlString(response);

            // Debugging print to see the XML response
            System.out.println("Response for Author search: \n");
            //System.out.println(response);

            NodeList records = document.getElementsByTagName("srw:record");
            for (int i = 0; i < records.getLength(); i++) {
                Element record = (Element) records.item(i);
                String bookTitle = extractAndDisplayTitle(record);
                String bookAuthorFirstName = extractAuthorFirstName(record);
                String bookAuthorLastName = extractAuthorLastName(record);
                String bookEdition = extractAndDisplayEdition(record);
                String bookArk = extractArk(record);


                Book book = new Book(bookTitle,bookAuthorLastName,bookAuthorFirstName, bookArk,bookEdition);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Searches for books by title using the given title string.
     *
     * @param title the title string to search for.
     * @return an ArrayList of Book objects containing the books found.
     */
    public static ArrayList<Book> searchByTitle(String title) {
        ArrayList<Book> books = new ArrayList<>();
        try {
            // Encoder le titre pour l'utiliser dans une requÃªte URL
            String queryTitle = URLEncoder.encode("bib.title all \"" + title + "\"", "UTF-8");
            // URL de base pour l'API SRU de la Bnf
            int maximumRecords = 100;
            String baseUrl = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=";
            String urlTitle = baseUrl + queryTitle + "&maximumRecords=" + maximumRecords;
            String response = executeHttpRequest(urlTitle);
            Document document = parseXmlString(response);
            NodeList records = document.getElementsByTagName("srw:record");
            for (int i = 0; i < records.getLength(); i++) {
                Element record = (Element) records.item(i);
                String bookTitle = extractAndDisplayTitle(record);
                String bookAuthorFirstName = extractAuthorFirstName(record);
                String bookAuthorLastName = extractAuthorLastName(record);
                String bookEdition = extractAndDisplayEdition(record);
                String bookArk = extractArk(record);
                Book book = new Book(bookTitle,bookAuthorLastName,bookAuthorFirstName, bookArk,bookEdition);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }



    /**
     * Searches for books by their ISBN (International Standard Book Number).
     *
     * @param ISBN the ISBN of the book to search for.
     * @return an ArrayList of Book objects matching the given ISBN.
     */
    public static ArrayList<Book> searchByISBN(String ISBN) {

        ArrayList<Book> books = new ArrayList<>();

        try {
            // Encoder l'ISBN pour l'utiliser dans une requÃªte URL
            String queryIsbn = URLEncoder.encode("bib.isbn adj \"" + ISBN + "\"", "UTF-8");
            String baseUrl = "http://catalogue.bnf.fr/api/SRU?version=1.2&operation=searchRetrieve&query=";
            String urlIsbn = baseUrl + queryIsbn;

            String response = executeHttpRequest(urlIsbn);
            Document document = parseXmlString(response);

            NodeList records = document.getElementsByTagName("srw:record");
            for (int i = 0; i < records.getLength(); i++) {
                Element record = (Element) records.item(i);
                String bookTitle = extractAndDisplayTitle(record);
                String bookAuthorFirstName = extractAuthorFirstName(record);
                String bookAuthorLastName = extractAuthorLastName(record);
                String bookEdition = extractAndDisplayEdition(record);
                String bookArk = extractArk(record);


                Book book = new Book(bookTitle,bookAuthorLastName,bookAuthorFirstName, bookArk,bookEdition);
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

// -------------------------------------------- Override --------------------------------------------------------


    /**
     * Generates a string representation of the Book object.
     *
     * @return a string containing the title, first name of the author, last name of the author, ARK, and edition of the book.
     */
    @Override

    public String toString() {
        return "Title : " + title + "\n" +
                "Author first name: " + author_first_name + "\n" +
                "Author last name : " + author_last_name + "\n" +
                "Edition : "+ edition + "\n" +
                "ARK : "+ ARK +"\n ";

    }
}


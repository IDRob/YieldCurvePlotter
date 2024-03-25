package datareader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class gets US Treasury Bill Data from <a href="https://home.treasury.gov/resource-center">...</a>.
 */
public class UsTreasuryDataReader {

    /**
     * The base url holding the US treasury bill par yield information in xml format, requires a year to be added.
     */
    private final static String BASE_URL = "https://home.treasury.gov/resource-center/data-chart-center/interest-rates/" +
            "pages/xml?data=daily_treasury_yield_curve&field_tdr_date_value=";

    /**
     * A map of coupon bearing treasury bill element tag keys to the corresponding months value.
     */
    private final static Map<String, Integer> COUPON_BEARING_TREASURY_BILLS = new HashMap<>()
    {{
        put("d:BC_6MONTH",6);
        put("d:BC_1YEAR",12);
        put("d:BC_2YEAR",24);
        put("d:BC_3YEAR",36);
        put("d:BC_5YEAR",60);
        put("d:BC_7YEAR",84);
        put("d:BC_10YEAR",120);
        put("d:BC_20YEAR",240);
        put("d:BC_30YEAR", 360);
    }};

    /**
     * The date time format of the US Treasury Bill data.
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    /**
     * The node tag for each par yield entry.
     */
    private static final String NODE_TAG = "entry";

    /**
     * The date tag for each par yield date entry.
     */
    private static final String DATE_TAG = "d:NEW_DATE";

    /**
     * Gets the par yield data for the specified date.
     *
     * @param date the requested date
     * @param document the document to be searched
     * @return the month to rate map of par yield data
     */
    public NavigableMap<Integer, Double> getsParDataForDate(LocalDate date, Document document) {

        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName(NODE_TAG);

        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                String elementDateString = eElement.getElementsByTagName(DATE_TAG).item(0).getTextContent();
                LocalDate elementDate = LocalDate.parse(elementDateString, DATE_TIME_FORMATTER);

                if (elementDate.equals(date)) {
                    return new TreeMap<>(COUPON_BEARING_TREASURY_BILLS.entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getValue,
                                    e -> Double.parseDouble(eElement.getElementsByTagName(e.getKey()).item(0).getTextContent()) / 100)));

                }
            }
        }
        throw new RuntimeException("No data found for this date: " + date);
    }

    /**
     * Gets all dates with par yield data from the document.
     *
     * @param document the document to be searched
     * @return a list of available dates
     */
    public List<LocalDate> getDates(Document document) {

        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName(NODE_TAG);

        List<LocalDate> availableDates = new ArrayList<>();
        for (int itr = 0; itr < nodeList.getLength(); itr++) {
            Node node = nodeList.item(itr);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                String elementDate = eElement.getElementsByTagName(DATE_TAG).item(0).getTextContent();
                availableDates.add(LocalDate.parse(elementDate, DATE_TIME_FORMATTER));

            }
        }
        return availableDates;
    }

    public static Document getUSTreasuryRateData(String year){

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder docBuilder;
        try {
            docBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        URI uri;
        try {
            uri = new URI(BASE_URL +year);
            return docBuilder.parse(uri.toURL().openStream());
        } catch (URISyntaxException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

    }
}

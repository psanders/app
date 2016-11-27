package com.fonoster.core;

import com.fonoster.core.api.DBManager;
import com.fonoster.core.api.NumbersAPI;
import com.fonoster.model.Rate;
import com.fonoster.model.ServiceProvider;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * TODO: This could be automate to check for updates at the end of the day (or everytime there is a change)
 * A more advance approach to decide the "selling" price could be develop. Ideally the ability to update selling price
 * by service provider + region etc.
 */
public class RateLoader {
    private static final Logger LOG = LoggerFactory.getLogger(RateLoader.class);
    //CSV file header
    private static final String [] FILE_HEADER_MAPPING = {"Destination","Rate","Numberplan"};
    //Student attributes
    private static final String DESCRIPTION = "Destination";
    private static final String RATE = "Rate";
    private static final String PREFIX = "Numberplan";
    private final Datastore ds;

    public RateLoader() {
        ds = DBManager.getInstance().getDS();
    }

    public void loadRates(ServiceProvider provider, String fileName, BigDecimal sellPercent) {

        FileReader fileReader = null;

        CSVParser csvFileParser = null;

        //Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);

        try {
            //initialize FileReader object
            fileReader = new FileReader(fileName);

            //initialize CSVParser object
            csvFileParser = new CSVParser(fileReader, csvFileFormat);

            //Read the CSV file records starting from the second record to skip the header
            for (CSVRecord record:csvFileParser.getRecords()) {
                if(record.get(RATE).equals(RATE)) continue;

                BigDecimal buying = new BigDecimal(record.get(RATE));
                BigDecimal selling = buying.multiply(sellPercent);
                selling = selling.add(buying);

                Rate r = new Rate();
                r.setPrefix(record.get(PREFIX));
                r.setProvider(provider);
                r.setDescription(record.get(DESCRIPTION));
                r.setBuying(buying);
                r.setSelling(selling);

                ds.save(r);
            }

        }
        catch (Exception e) {
            LOG.error("Something happen while loading the new rate. Cause by: ", e);
        } finally {
            try {
                fileReader.close();
                csvFileParser.close();
            } catch (IOException e) {
                LOG.error("Error while closing fileReader/csvFileParser", e);
            }
        }

    }

    static public void main(String... args) {
        if (args.length < 3) {
            System.out.println("Expected: {providerId} {rate.csv file} {sellingPercent ie.: 0.05} in that order!");
            System.exit(99);
        }

        ServiceProvider provider = NumbersAPI.getInstance().getServiceProviderById(new ObjectId(args[0]));
        String csvFile = args[1];
        BigDecimal sellingPercent = new BigDecimal(args[2]);

        LOG.info("Removing old entries for provider: ".concat(provider.getName()));

        // Lets remove old rate first...
        Query<?> q = DBManager.getInstance().getDS().createQuery(Rate.class).field("provider").equal(provider);
        DBManager.getInstance().getDS().delete(q);

        LOG.info("Adding new rates.");
        new RateLoader().loadRates(provider, csvFile, sellingPercent);
    }
}

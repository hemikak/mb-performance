package Utilities;

import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 *
 */
public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    public static void propertyTreePrinter(HashTree properties){
        for (Map.Entry<Object, HashTree> objectHashTreeEntry : properties.entrySet()) {
            log.info(objectHashTreeEntry.getKey().toString());
        }
    }
}

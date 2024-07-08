package com.alex.chat;

import com.alex.chat.utill.Utill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public final class ParamsServerFactory {
    private final static Logger logger = LoggerFactory.getLogger(ParamsServerFactory.class);
    private ParamsServerFactory(){

    }
    private enum PropName {
        LISTENING_PORT("serverPort"),
        COUNT_MAIN_THREAD("mainThread"),
        COUNT_ERROR_THREAD("errorThread"),
        CAPACITY_QUEUE("capacityQueue");
        private String propName;

        PropName(String propName) {
            this.propName = propName;
        }
        @Override
        public String toString(){
            return propName;
        }

    }
    public static ParamsServer tryParseProperties(Properties properties) {
        logger.debug("The parsing process with properties:[{}]",properties);
        return new ParamsServer(
                Utill.tryParseToInt(properties, PropName.LISTENING_PORT.toString()),
                Utill.tryParseToInt(properties, PropName.COUNT_MAIN_THREAD.toString()),
                Utill.tryParseToInt(properties, PropName.COUNT_ERROR_THREAD.toString()),
                Utill.tryParseToInt(properties, PropName.CAPACITY_QUEUE.toString())
        );
    }

}


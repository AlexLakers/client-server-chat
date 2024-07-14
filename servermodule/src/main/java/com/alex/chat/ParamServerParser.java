package com.alex.chat;

import com.alex.chat.utill.Utill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public final class ParamServerParser {
    private final static Logger logger = LoggerFactory.getLogger(ParamServerParser.class);
    private ParamServerParser(){

    }
    private enum PropName {
        LISTENING_PORT("server.port"),
        COUNT_MAIN_THREAD("pool.main.thread.count"),
        COUNT_ERROR_THREAD("pool.error.thread.count"),
        CAPACITY_QUEUE("connections.queue.capacity");
        private String propName;

        PropName(String propName) {
            this.propName = propName;
        }
        @Override
        public String toString(){
            return propName;
        }

    }
    public static ParamServer tryParseProperties(Properties properties) {
        logger.debug("The parsing process with properties:[{}]",properties);
        return new ParamServer(
                Utill.tryParseToInt(properties, PropName.LISTENING_PORT.toString()),
                Utill.tryParseToInt(properties, PropName.COUNT_MAIN_THREAD.toString()),
                Utill.tryParseToInt(properties, PropName.COUNT_ERROR_THREAD.toString()),
                Utill.tryParseToInt(properties, PropName.CAPACITY_QUEUE.toString())
        );
    }

}


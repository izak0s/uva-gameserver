package amsterdam.izak.progproj.utils;

import amsterdam.izak.progproj.network.ChannelInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLog {
    private static Logger logger = LoggerFactory.getLogger(GameLog.class);

    public static void info(String s) {
        logger.info(s);
    }

    public static void warning(String s){
        logger.warn(s);
    }

    public static void error(String error){
        logger.error(error);
    }

    public static void debug(String s) {
        logger.debug(s);
    }
}

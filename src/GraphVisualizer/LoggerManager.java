package GraphVisualizer;

import java.util.logging.*;

public class LoggerManager {

    private static final Logger logger = Logger.getLogger(GraphVisualizer.class.getName());
    private static final LoggerManager instance = new LoggerManager();

    public static Logger Logger()
    {
        return logger;
    }


    private LoggerManager()
    {
        logger.setLevel(Level.ALL); // captures all levels: SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST

        Logger rootLogger = Logger.getLogger("");
        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h); // remove default console handler
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);

        consoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(
                        "%1$tF %1$tT [%2$s] %3$s: %4$s %n",
                        lr.getMillis(),
                        lr.getLevel().getName(),
                        lr.getLoggerName(),
                        lr.getMessage()
                );
            }
        });

        logger.addHandler(consoleHandler);

        try {
            FileHandler fileHandler = new FileHandler("logs/graph.log", 1024 * 1024, 5, true); // 1MB per file, 5 files, append
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

    }
}

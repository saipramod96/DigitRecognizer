package ramo.klevis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton class that holds and loads the configuration files.
 * @author Klevis Ramo
 * @version 1.0
 * @since 11/24/2017
 */

public final class Configuration {

	private final static Logger LOGGER = LoggerFactory.getLogger(DigitRecognizer.class);
    private static Configuration instance;
    public static final String FILENAME = "config.properties";
    public Properties props = new Properties();

    public static Configuration getInstance() {
        if (Configuration.instance == null) {
            Configuration.instance = new Configuration();
        }
        return Configuration.instance;
    }

    private Configuration() {
        this.loadConfig(FILENAME);
    }

    private void loadConfig(final String fileName) {
        try {
            final File configFile = new File(fileName);
            final FileReader fileReader = new FileReader(configFile);
            this.props.load(fileReader);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

}

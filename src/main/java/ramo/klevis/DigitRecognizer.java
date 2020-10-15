package ramo.klevis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * The DigitRecognizer program implements an application that recognizes Digits drawn by the user, using Neural Networks.
 * @author Klevis Ramo
 * @version 1.0
 * @since 11/24/2017
 */

public class DigitRecognizer {

    private final static Logger LOGGER = LoggerFactory.getLogger(DigitRecognizer.class);
    private static final Configuration config = Configuration.getInstance();
    private static final JFrame mainFrame = new JFrame();
    private static final ConvolutionalNeuralNetwork convolutionalNeuralNetwork = ConvolutionalNeuralNetwork.getInstance();

    public static void main(String[] args) throws Exception {
        LOGGER.info("Application is starting ... ");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("Button.font", new FontUIResource(new Font("Dialog", Font.BOLD, 18)));

        UI ui = new UI();

        setHadoopHomeEnvironmentVariable();
        convolutionalNeuralNetwork.init();
        ui.attach(convolutionalNeuralNetwork);
        convolutionalNeuralNetwork.setSubject(ui);
        mainFrame.setVisible(true);
    }

    /* This method is only for Windows OS. */
    private static void setHadoopHomeEnvironmentVariable() throws Exception {
        HashMap<String, String> hadoopEnvSetUp = new HashMap<>();
        hadoopEnvSetUp.put("HADOOP_HOME", new
                File(config.props.getProperty("HADOOP_HOME")).getAbsolutePath());
        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
        Field theEnvironmentField =
                processEnvironmentClass.getDeclaredField("theEnvironment");
        theEnvironmentField.setAccessible(true);
        Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
        env.clear();
        env.putAll(hadoopEnvSetUp);
        Field theCaseInsensitiveEnvironmentField =
                processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
        theCaseInsensitiveEnvironmentField.setAccessible(true);
        Map<String, String> caseInsensitiveEnv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
        caseInsensitiveEnv.clear();
        caseInsensitiveEnv.putAll(hadoopEnvSetUp);
    }
}

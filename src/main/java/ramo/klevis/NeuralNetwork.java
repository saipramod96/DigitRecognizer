package ramo.klevis;

import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Klevis Ramo
 * @version 1.0
 * @since 11/27/2017
 */

public class NeuralNetwork {

    private final static Logger LOGGER = LoggerFactory.getLogger(NeuralNetwork.class);
    private static final Configuration config = Configuration.getInstance();

    private SparkSession sparkSession;
    private MultilayerPerceptronClassificationModel model;
    private static NeuralNetwork instance;

    protected NeuralNetwork() {
        // Protected default constructor for singleton class.
    }

    public static NeuralNetwork getInstance() {
        if (NeuralNetwork.instance == null) {
            NeuralNetwork.instance = new NeuralNetwork();
        }
        return NeuralNetwork.instance;
    }

    public void init() {
        initSparkSession();
        if (model == null) {
            LOGGER.info("Loading the Neural Network from saved model ... ");
            model = MultilayerPerceptronClassificationModel.load(config.props.getProperty("TRAINED_MODEL"));
            LOGGER.info("Loading from saved model is done");
        }
    }

    public void train(final Integer trainData, final Integer testFieldValue) {

        initSparkSession();

        final List<LabeledImage> labeledImages = IdxReader.loadData(trainData);
        final List<LabeledImage> testLabeledImages = IdxReader.loadTestData(testFieldValue);

        final Dataset<Row> train = sparkSession.createDataFrame(labeledImages, LabeledImage.class).checkpoint();
        final Dataset<Row> test = sparkSession.createDataFrame(testLabeledImages, LabeledImage.class).checkpoint();

        final int[] layers = new int[]{784, 128, 64, 10};

        final MultilayerPerceptronClassifier trainer = new MultilayerPerceptronClassifier()
                .setLayers(layers)
                .setBlockSize(128)
                .setSeed(1234L)
                .setMaxIter(100);

        model = trainer.fit(train);

        evalOnTest(test);
        evalOnTest(train);
    }

    private void evalOnTest(final Dataset<Row> test) {
        final Dataset<Row> result = model.transform(test);
        final Dataset<Row> predictionAndLabels = result.select("prediction", "label");
        final MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setMetricName("accuracy");

        LOGGER.info("Test set accuracy = " + evaluator.evaluate(predictionAndLabels));
    }

    private void initSparkSession() {
        if (sparkSession == null) {
            sparkSession = SparkSession.builder()
                    .master("local[*]")
                    .appName("Digit Recognizer")
                    .getOrCreate();
        }
        sparkSession.sparkContext().setCheckpointDir("checkPoint");
    }

    public LabeledImage predict(final LabeledImage labeledImage) {
        final double predict = model.predict(labeledImage.getFEATURES());
        labeledImage.setLabel(predict);
        return labeledImage;
    }
}

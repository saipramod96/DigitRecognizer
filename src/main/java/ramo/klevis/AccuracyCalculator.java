package ramo.klevis;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.earlystopping.scorecalc.ScoreCalculator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that calculates the accuracy scores for the MultiLaterNetworks.
 * @author Klevis Ramo
 * @version 1.0
 * @since 11/24/2017
 */

public class AccuracyCalculator implements ScoreCalculator<MultiLayerNetwork> {

    private static final Logger logger = LoggerFactory.getLogger(AccuracyCalculator.class);
    private final MnistDataSetIterator dataSetIterator;
    private int accuracyCount = 0;

    /* Method to calculate accuracy score */
    public AccuracyCalculator(final MnistDataSetIterator dataSetIterator) {
        this.dataSetIterator = dataSetIterator;
    }

    @Override
    public double calculateScore(final MultiLayerNetwork network) {
        final Evaluation evaluate = network.evaluate(dataSetIterator);
        final double accuracy = evaluate.accuracy();
        logger.error("Accuracy " + accuracyCount++ + " " + accuracy);
        return 1 - evaluate.accuracy();
    }
}

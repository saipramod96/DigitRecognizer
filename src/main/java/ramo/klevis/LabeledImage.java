package ramo.klevis;

import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;

import java.io.Serializable;

/**
 * Class that holds the labeled image and its features.
 *
 * @author Klevis Ramo
 * @version 1.0
 * @since 11/24/2017
 */

public class LabeledImage implements Serializable {
    private final double[] pixels;
    private double label;
    private final Vector features;

    public LabeledImage(int label, double[] pixels) {
        double[] meanNormalizedPixel = meanNormalizeFeatures(pixels);
        this.pixels = pixels;
        features = Vectors.dense(meanNormalizedPixel);
        this.label = label;
    }

    public double[] getPixels() {
        return pixels;
    }

    private double[] meanNormalizeFeatures(double[] pixels) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        for (double pixel : pixels) {
            sum = sum + pixel;
            if (pixel > max) {
                max = pixel;
            }
            if (pixel < min) {
                min = pixel;
            }
        }
        double mean = sum / pixels.length;

        double[] pixelsNorm = new double[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            pixelsNorm[i] = (pixels[i] - mean) / (max - min);
        }
        return pixelsNorm;
    }

    public Vector getFeatures() {
        return features;
    }

    public double getLabel() {
        return label;
    }

    public void setLabel(double label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "LabeledImage{" +
                "label=" + label +
                '}';
    }
}

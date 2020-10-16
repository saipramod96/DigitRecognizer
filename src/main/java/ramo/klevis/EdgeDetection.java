package ramo.klevis;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class that detects sober, vertical and horizontal edges.
 *
 * @author Klevis Ramo
 * @version 1.0
 * @since 12/07/2017
 */

public class EdgeDetection {

    private static final double[][] FILTER_VERTICAL = {{1, 0, -1}, {1, 0, -1}, {1, 0, -1}};
    private static final double[][] FILTER_HORIZONTAL = {{1, 1, 1}, {0, 0, 0}, {-1, -1, -1}};
    private static final double[][] FILTER_SOBEL = {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}};
    private static final Configuration config = Configuration.getInstance();
    private static final String INPUT_IMAGE = config.props.getProperty("INPUT_IMAGE");
    private static int count = 1;

    private enum filterType {
        SOBEL,
        HORIZONTAL,
        VERTICAL
    }
    ;

    public static void main(String[] args) throws IOException {
        detectEdges(filterType.SOBEL);
        detectEdges(filterType.HORIZONTAL);
        detectEdges(filterType.VERTICAL);
    }

    private static void detectEdges(filterType filter) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(new File(INPUT_IMAGE));
        final double[][][] image = transformImageToArray(bufferedImage);
        double[][] finalConv;

        switch (filter) {
            case SOBEL:
                finalConv = applyConvolution(bufferedImage.getWidth(), bufferedImage.getHeight(), image, FILTER_SOBEL);
                break;
            case HORIZONTAL:
                finalConv = applyConvolution(bufferedImage.getWidth(), bufferedImage.getHeight(), image, FILTER_HORIZONTAL);
                break;
            case VERTICAL:
                finalConv = applyConvolution(bufferedImage.getWidth(), bufferedImage.getHeight(), image, FILTER_VERTICAL);
                break;
            default:
                return;
        }
        reCreateOriginalImageFromMatrix(bufferedImage, finalConv);
    }


    private static double[][][] transformImageToArray(BufferedImage bufferedImage) {
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        return transformImageToArray(bufferedImage, width, height);
    }

    private static double[][] applyConvolution(int width, int height, double[][][] image, double[][] filter) {
        final Convolution convolution = new Convolution();
        final double[][] redConv = convolution.convolutionType2(image[0], height, width, filter, 3, 3, 1);
        final double[][] greenConv = convolution.convolutionType2(image[1], height, width, filter, 3, 3, 1);
        final double[][] blueConv = convolution.convolutionType2(image[2], height, width, filter, 3, 3, 1);
        double[][] finalConv = new double[redConv.length][redConv[0].length];
        for (int i = 0; i < redConv.length; i++) {
            for (int j = 0; j < redConv[i].length; j++) {
                finalConv[i][j] = redConv[i][j] + greenConv[i][j] + blueConv[i][j];
            }
        }
        return finalConv;
    }

    private static double[][][] transformImageToArray(BufferedImage bufferedImage, int width, int height) {
        double[][][] image = new double[3][height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final Color color = new Color(bufferedImage.getRGB(j, i));
                image[0][i][j] = color.getRed();
                image[1][i][j] = color.getGreen();
                image[2][i][j] = color.getBlue();
            }
        }
        return image;
    }

    private static void reCreateOriginalImageFromMatrix(BufferedImage originalImage, double[][] imageRGB) throws IOException {
        final BufferedImage writeBackImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < imageRGB.length; i++) {
            for (int j = 0; j < imageRGB[i].length; j++) {
                final Color color = new Color(fixOutOfRangeRGBValues(imageRGB[i][j]),
                        fixOutOfRangeRGBValues(imageRGB[i][j]),
                        fixOutOfRangeRGBValues(imageRGB[i][j]));
                writeBackImage.setRGB(j, i, color.getRGB());
            }
        }
        final File outputFile = new File("edges" + count++ + ".png");
        ImageIO.write(writeBackImage, "png", outputFile);
    }

    private static int fixOutOfRangeRGBValues(double value) {
        if (value < 0.0) {
            value = -value;
        }
        if (value > 255) {
            return 255;
        } else {
            return (int) value;
        }
    }
}

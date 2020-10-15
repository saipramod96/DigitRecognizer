package ramo.klevis;

import java.awt.*;

/**
 * Convolution class holds the code for applying the convolution operator.
 *
 * @author: Simon Horne
 */

public class Convolution extends Thread {

    /**
     * Takes an image (grey-levels) and a kernel and a position,
     * applies the convolution at that position and returns the
     * new pixel value.
     *
     * @param input        The 2D double array representing the image.
     * @param x            The x coordinate for the position of the convolution.
     * @param y            The y coordinate for the position of the convolution.
     * @param k            The 2D array representing the kernel.
     * @param kernelWidth  The width of the kernel.
     * @param kernelHeight The height of the kernel.
     * @return The new pixel value after the convolution.
     */
    public static double singlePixelConvolution(double[][] input,
                                                int x, int y,
                                                double[][] k,
                                                int kernelWidth,
                                                int kernelHeight) {
        double output = 0;
        for (int i = 0; i < kernelWidth; ++i) {
            for (int j = 0; j < kernelHeight; ++j) {
                output = output + (input[x + i][y + j] * k[i][j]);
            }
        }
        return output;
    }

    public static int applyConvolution(int[][] input,
                                       int x, int y,
                                       double[][] k,
                                       int kernelWidth,
                                       int kernelHeight) {
        int output = 0;
        for (int i = 0; i < kernelWidth; ++i) {
            for (int j = 0; j < kernelHeight; ++j) {
                output = output + (int) Math.round(input[x + i][y + j] * k[i][j]);
            }
        }
        return output;
    }

    /**
     * Takes a 2D array of grey-levels and a kernel and applies the convolution
     * over the area of the image specified by width and height.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @return the 2D array representing the new image
     */
    public static double[][] convolution2D(double[][] input,
                                           int width, int height,
                                           double[][] kernel,
                                           int kernelWidth,
                                           int kernelHeight) {
        int smallWidth = width - kernelWidth + 1;
        int smallHeight = height - kernelHeight + 1;
        double[][] output = new double[smallWidth][smallHeight];
        for (int i = 0; i < smallWidth; ++i) {
            for (int j = 0; j < smallHeight; ++j) {
                output[i][j] = 0;
            }
        }
        for (int i = 0; i < smallWidth; ++i) {
            for (int j = 0; j < smallHeight; ++j) {
                output[i][j] = singlePixelConvolution(input, i, j, kernel,
                        kernelWidth, kernelHeight);
            }
        }
        return output;
    }

    /**
     * Takes a 2D array of grey-levels and a kernel, applies the convolution
     * over the area of the image specified by width and height and returns
     * a part of the final image.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @return the 2D array representing the new image
     */
    public static double[][] convolution2DPadded(double[][] input,
                                                 int width, int height,
                                                 double[][] kernel,
                                                 int kernelWidth,
                                                 int kernelHeight) {
        int smallWidth = width - kernelWidth + 1;
        int smallHeight = height - kernelHeight + 1;
        int top = kernelHeight / 2;
        int left = kernelWidth / 2;
        double small[][];
        small = convolution2D(input, width, height,
                kernel, kernelWidth, kernelHeight);
        double large[][] = new double[width][height];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                large[i][j] = 0;
            }
        }
        for (int j = 0; j < smallHeight; ++j) {
            for (int i = 0; i < smallWidth; ++i) {
                large[i + left][j + top] = small[i][j];
            }
        }
        return large;
    }

    /**
     * Takes a 2D array of grey-levels and a kernel and applies the convolution
     * over the area of the image specified by width and height.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @return the 1D array representing the new image
     */
    public static double[] convolutionDouble(double[][] input,
                                             int width, int height,
                                             double[][] kernel,
                                             int kernelWidth, int kernelHeight) {
        int smallWidth = width - kernelWidth + 1;
        int smallHeight = height - kernelHeight + 1;
        double[][] small;
        small = convolution2D(input, width, height, kernel, kernelWidth, kernelHeight);
        double[] result = new double[smallWidth * smallHeight];
        for (int j = 0; j < smallHeight; ++j) {
            for (int i = 0; i < smallWidth; ++i) {
                result[j * smallWidth + i] = small[i][j];
            }
        }
        return result;
    }

    /**
     * Takes a 2D array of grey-levels and a kernel and applies the convolution
     * over the area of the image specified by width and height.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @return the 1D array representing the new image
     */
    public static double[] convolutionDoublePadded(double[][] input,
                                                   int width, int height,
                                                   double[][] kernel,
                                                   int kernelWidth,
                                                   int kernelHeight) {
        double[][] result2D;
        result2D = convolution2DPadded(input, width, height,
                kernel, kernelWidth, kernelHeight);
        double[] result = new double[width * height];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                result[j * width + i] = result2D[i][j];
            }
        }
        return result;
    }

    /**
     * Converts a grey-level array into a pixel array.
     *
     * @param greys: 1D array of grey pixels.
     * @return the 1D array of RGB pixels.
     */
    public static int[] doublesToValidPixels(double[] greys) {
        int[] result = new int[greys.length];
        int grey;
        for (int i = 0; i < greys.length; ++i) {
            if (greys[i] > 255) {
                grey = 255;
            } else if (greys[i] < 0) {
                grey = 0;
            } else {
                grey = (int) Math.round(greys[i]);
            }
            result[i] = (new Color(grey, grey, grey)).getRGB();
        }
        return result;
    }

    /**
     * Applies the convolution2D algorithm to the input array as many as
     * iterations.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @param iterations   the number of iterations to apply the convolution
     * @return the 2D array representing the new image
     */
    public double[][] convolutionType1(double[][] input,
                                       int width, int height,
                                       double[][] kernel,
                                       int kernelWidth, int kernelHeight,
                                       int iterations) {
        double[][] newInput = input.clone();
        double[][] output = input.clone();
        for (int i = 0; i < iterations; ++i) {
            int smallWidth = width - kernelWidth + 1;
            int smallHeight = height - kernelHeight + 1;
            output = convolution2D(newInput, width, height,
                    kernel, kernelWidth, kernelHeight);
            width = smallWidth;
            height = smallHeight;
            newInput = output.clone();
        }
        return output;
    }

    /**
     * Applies the convolution2DPadded  algorithm to the input array as many as
     * iterations.
     *
     * @param input        the 2D double array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @param iterations   the number of iterations to apply the convolution
     * @return the 2D array representing the new image
     */
    public double[][] convolutionType2(double[][] input,
                                       int width, int height,
                                       double[][] kernel,
                                       int kernelWidth, int kernelHeight,
                                       int iterations) {
        double[][] newInput = input.clone();
        double[][] output = input.clone();

        for (int i = 0; i < iterations; ++i) {
            output = convolution2DPadded(newInput, width, height,
                    kernel, kernelWidth, kernelHeight);
            newInput = output.clone();
        }
        return output;
    }

    /**
     * Applies the convolution2DPadded  algorithm and an offset and scale factors
     *
     * @param input        the 1D int array representing the image
     * @param width        the width of the image
     * @param height       the height of the image
     * @param kernel       the 2D array representing the kernel
     * @param kernelWidth  the width of the kernel
     * @param kernelHeight the height of the kernel
     * @param scale        the scale factor to apply
     * @param offset       the offset factor to apply
     * @return the 1D array representing the new image
     */
    public static int[] convolution_image(int[] input, int width, int height,
                                          double[][] kernel,
                                          int kernelWidth, int kernelHeight,
                                          double scale, double offset) {
        double[][] input2D = new double[width][height];
        double[] output;
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                input2D[i][j] = (new Color(input[j * width + i])).getRed();
            }
        }
        output = convolutionDoublePadded(input2D, width, height,
                kernel, kernelWidth, kernelHeight);
        int[] outputInts = new int[width * height];

        for (int i = 0; i < outputInts.length; ++i) {
            outputInts[i] = (int) Math.round(output[i] * scale + offset);
            if (outputInts[i] > 255) outputInts[i] = 255;
            if (outputInts[i] < 0) outputInts[i] = 0;
            int g = outputInts[i];
            outputInts[i] = (new Color(g, g, g)).getRGB();
        }
        return outputInts;
    }
}
    
package ramo.klevis;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * The UI class is a JFrame to hold the JPanels for drawing area (input) and prediction area (output).
 * @author Klevis Ramo
 */

public class UI extends JFrame implements UISubject {

    private final static Logger LOGGER = LoggerFactory.getLogger(UI.class);
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 628;
    private DrawArea drawArea;
    private final JPanel mainPanel;
    private final JPanel drawAndDigitPredictionPanel;
    private JPanel resultPanel;
    private NNObserver observer;
    private String subjectState;

    public String getSubjectState() {
        return subjectState;
    }

    public void setSubjectState(String subjectState) {
        this.subjectState = subjectState;
    }

    @Override
    public void attach(NNObserver observer) {
        this.observer = observer;
    }

    @Override
    public void detach(NNObserver observer) {
        this.observer = null;
    }

    @Override
    public int notifyObs(LabeledImage labeledImage) {
        return this.observer.update(labeledImage);
    }

    public UI() {
        LOGGER.info("Creating UI");

        setMainFrame();
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        drawAndDigitPredictionPanel = new JPanel(new GridLayout());
        addActionPanel();
        addDrawAreaAndPredictionArea();
        mainPanel.add(drawAndDigitPredictionPanel, BorderLayout.CENTER);

        addSignature();
        this.add(mainPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }

    private void addActionPanel() {
        LOGGER.info("Adding Action Panel");

        JButton recognizeCNN = new JButton("Recognize Digit");

        recognizeCNN.addActionListener(e -> {
            Image drawImage = drawArea.getImage();
            BufferedImage sbi = toBufferedImage(drawImage);
            Image scaled = scale(sbi);
            BufferedImage scaledBuffered = toBufferedImage(scaled);
            double[] scaledPixels = transformImageToOneDimensionalVector(scaledBuffered);
            LabeledImage labeledImage = new LabeledImage(0, scaledPixels);
            int predict = notifyObs(labeledImage);
            JLabel predictNumber = new JLabel("" + predict);
            predictNumber.setForeground(Color.RED);
            predictNumber.setFont(new Font("SansSerif", Font.BOLD, 128));
            resultPanel.removeAll();
            resultPanel.add(predictNumber);
            resultPanel.updateUI();
        });

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            drawArea.setImage(null);
            drawArea.repaint();
            drawAndDigitPredictionPanel.updateUI();
        });

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.add(recognizeCNN);
        actionPanel.add(clear);
        mainPanel.add(actionPanel, BorderLayout.NORTH);
    }

    private void addDrawAreaAndPredictionArea() {
        LOGGER.info("Adding Drawing area and Prediction area");
        drawArea = new DrawArea();
        drawAndDigitPredictionPanel.add(drawArea);
        resultPanel = new JPanel();
        resultPanel.setLayout(new GridBagLayout());
        drawAndDigitPredictionPanel.add(resultPanel);
    }

    private static BufferedImage scale(BufferedImage imageToScale) {
        ResampleOp resizeOp = new ResampleOp(28, 28);
        resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
        final BufferedImage filter = resizeOp.filter(imageToScale, null);
        return filter;
    }

    private static BufferedImage toBufferedImage(Image img) {
        // Create a buffered image with transparency
        BufferedImage binaryImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = binaryImage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return binaryImage;
    }

    private static double[] transformImageToOneDimensionalVector(BufferedImage img) {
        double[] imageGray = new double[28 * 28];
        int w = img.getWidth();
        int h = img.getHeight();
        int index = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color color = new Color(img.getRGB(j, i), true);
                int red = (color.getRed());
                int green = (color.getGreen());
                int blue = (color.getBlue());
                double v = 255 - (red + green + blue) / 3d;
                imageGray[index] = v;
                index++;
            }
        }
        return imageGray;
    }

    private void setMainFrame() {
        this.setTitle("Digit Recognizer");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        ImageIcon imageIcon = new ImageIcon("icon.png");
        this.setIconImage(imageIcon.getImage());
    }

    private void addSignature() {
        LOGGER.info("Adding Application signature");
        JLabel signature = new JLabel("ramok.tech", SwingConstants.CENTER);
        signature.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
        signature.setForeground(Color.BLUE);
        mainPanel.add(signature, BorderLayout.SOUTH);
    }

}
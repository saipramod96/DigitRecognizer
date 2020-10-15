package ramo.klevis;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Component that holds the drawing area for the user.
 *
 * @author sylsau
 */

public class DrawArea extends JComponent {

    // Image in which we're going to draw
    private Image image;
    // Graphics2D object ==> used to draw on
    private Graphics2D graphics2D;
    // Mouse coordinates
    private int currentX;
    private int currentY;
    private int oldX;
    private int oldY;

    public DrawArea() {
        super();
        setDoubleBuffered(false);
        Font sansSerifBold = new Font("SansSerif", Font.BOLD, 18);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Please draw a digit",
                TitledBorder.LEFT,
                TitledBorder.TOP, sansSerifBold, Color.BLUE));
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                // save coord x,y when mouse is pressed
                oldX = event.getX();
                oldY = event.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent event) {
                // coord x,y when drag mouse
                currentX = event.getX();
                currentY = event.getY();

                if (graphics2D != null) {
                    graphics2D.setStroke(new BasicStroke(10));
                    // draw line if g2 context not null
                    graphics2D.drawLine(oldX, oldY, currentX, currentY);
                    // refresh draw area to repaint
                    repaint();
                    // store current coord x,y as olds x,y
                    oldX = currentX;
                    oldY = currentY;
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if (image == null) {
            // create image to draw null
            image = createImage(getSize().width, getSize().height);
            graphics2D = (Graphics2D) image.getGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            clear();
        }
        graphics.drawImage(image, 0, 0, null);
    }

    public void clear() {
        graphics2D.setPaint(Color.white);
        // draw white on entire draw area to clear
        graphics2D.fillRect(0, 0, getSize().width, getSize().height);
        graphics2D.setPaint(Color.black);
        repaint();
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
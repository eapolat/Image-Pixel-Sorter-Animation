import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;


class ImagePanel extends JPanel {
    private BufferedImage image;

    public ImagePanel(BufferedImage img) {
        this.image = img;
    }

    @Override
    public Dimension getPreferredSize() {

        return new Dimension(image.getWidth(), image.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        double fWidth = getWidth() / (double) image.getWidth();
        double fHeight = getHeight() / (double) image.getHeight();
        double f = Math.min(fWidth, fHeight);


        int newWidth = (int) (f * image.getWidth());
        int newHeight = (int) (f * image.getHeight());


        g.drawImage(image, 0, 0, newWidth, newHeight, null);
    }

    public void setImage(BufferedImage img) {
        this.image = img;
        this.repaint();
    }
}
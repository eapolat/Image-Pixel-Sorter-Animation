import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;


public class AnimatedSelectionSort extends JFrame {
    private BufferedImage originalImage;  // The original image
    private BufferedImage currentImage;   // Image currently being sorted
    private int iterationCount = 0;       // Iteration count for sorting
    private boolean sorting = false;      // Flag to indicate if sorting is ongoing
    private JLabel imageLabel;


    public AnimatedSelectionSort(String imagePath) throws IOException {
        originalImage = ImageIO.read(new File(imagePath));
        currentImage = deepCopy(originalImage);
        initUI();
        startSorting();
    }

    private void initUI() {
        setTitle("Animated Selection Sort");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imageLabel = new JLabel(new ImageIcon(currentImage));
        add(imageLabel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void resetImage() {
        currentImage = deepCopy(originalImage);
        iterationCount = 0;
        sorting = false;
        repaint();
    }

/*     private void startSorting() {
        new Thread(() -> {
            sorting = true;
            int width = currentImage.getWidth();
            int height = currentImage.getHeight();
    
            // Perform the animated diagonal selection sort
            for (int k = 0; k < width + height - 1; k++) {
                for (int j = 0; j <= k; j++) {
                    int i = k - j;
                    if (i < height && j < width) {
                        int minIndex = findMinIndex(currentImage, k, i, j);
                        if (minIndex != i) {
                            swapPixels(currentImage, i, j, minIndex, k - minIndex);
                        }
                    }
                }
                iterationCount++;
                SwingUtilities.invokeLater(this::repaint);
                try {
                    Thread.sleep(100); // Delay for animation effect
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sorting = false;
        }).start();
    } */

    private void startSorting() {
        new Thread(() -> {
            sorting = true;
            int width = currentImage.getWidth();
            int height = currentImage.getHeight();

            // Create a buffer strategy
            createBufferStrategy(2);
            BufferStrategy bufferStrategy = getBufferStrategy();

            // Perform the animated diagonal selection sort
            for (int k = 0; k < width + height - 1; k++) {
                for (int j = 0; j <= k; j++) {
                    int i = k - j;
                    if (i < height && j < width) {
                        int minIndex = findMinIndex(currentImage, k, i, j);
                        if (minIndex != i) {
                            swapPixels(currentImage, i, j, minIndex, k - minIndex);
                        }
                    }
                }
                iterationCount++;
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(new ImageIcon(currentImage));
                    imageLabel.repaint();
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            sorting = false;
        }).start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        synchronized (currentImage) {
            if (currentImage != null) {
                g.drawImage(currentImage, 0, 0, this);
            }
        }
    }

    private static int findMinIndex(BufferedImage image, int k, int i, int j) {
        int minIndex = i;
        int minBrightness = getBrightness(image.getRGB(j, i));
        for (int m = i + 1; m < k; m++) {
            if (m < image.getHeight() && (k - m) < image.getWidth()) {
                int currentBrightness = getBrightness(image.getRGB(k - m, m));
                if (currentBrightness < minBrightness) {
                    minIndex = m;
                    minBrightness = currentBrightness;
                }
            }
        }
        return minIndex;
    }

    private static void swapPixels(BufferedImage image, int i1, int j1, int i2, int j2) {
        int temp = image.getRGB(j1, i1);
        image.setRGB(j1, i1, image.getRGB(j2, i2));
        image.setRGB(j2, i2, temp);
    }

    private static int getBrightness(int rgb) {
        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = rgb & 0xff;
        return (int) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
    }

    private static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AnimatedSelectionSort("Yellow.jpg"); // Replace with the actual image file path
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

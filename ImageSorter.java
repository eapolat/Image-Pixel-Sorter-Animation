// Java Code with Updated Requirements

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

import java.awt.Graphics;

public class ImageSorter extends JFrame implements KeyListener {

    private BufferedImage[] images;
    private int currentImageIndex = 0;
    private int[] iterationCounts; 

    public ImageSorter() {

        loadImages();

        setTitle("Image Sorter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setVisible(true);

        iterationCounts = new int[images.length];

        sortImagePixels();
    }

    private void loadImages() {

        images = new BufferedImage[3]; 
        try {
            images[0] = ImageIO.read(new File("Mountain.jpg"));
            images[1] = ImageIO.read(new File("clouds.jpg"));
            images[2] = ImageIO.read(new File("image3.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private void sortImagePixels() {
    SwingWorker<Void, BufferedImage> worker = new SwingWorker<Void, BufferedImage>() {
        @Override
        protected Void doInBackground() throws Exception {
            BufferedImage img = images[currentImageIndex];
            int width = img.getWidth();
            int height = img.getHeight();

            ArrayList<ArrayList<Pixel>> diagonalChains = createDiagonalChains(img, width, height);
            int maxChainLength = getMaxChainLength(diagonalChains);

            for (int iterationCount = 0; iterationCount < maxChainLength; iterationCount++) {
                for (ArrayList<Pixel> chain : diagonalChains) {
                    sortChain(chain, iterationCount);
                }
                updateImage(img, diagonalChains);
                publish(img);
                Thread.sleep(100);
            }
            return null;
        }

        @Override
        protected void process(List<BufferedImage> chunks) {
            BufferedImage latestImage = chunks.get(chunks.size() - 1);
            images[currentImageIndex] = latestImage;
            repaint();
        }
    };
    worker.execute();
}

    public static double calculateBrightness(int rgb) {

        int red = (rgb >> 16) & 0xff;
        int green = (rgb >> 8) & 0xff;
        int blue = rgb & 0xff;
        return 0.2126 * red + 0.7152 * green + 0.0722 * blue;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (images != null && images[currentImageIndex] != null) {
            BufferedImage img = images[currentImageIndex];
            double resizeFactor = Math.min((double) getWidth() / img.getWidth(), (double) getHeight() / img.getHeight());
            g.drawImage(img, 0, 0, (int) (resizeFactor * img.getWidth()), (int) (resizeFactor * img.getHeight()), this);
        }
    }
    

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentImageIndex = (currentImageIndex + 1) % images.length;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            currentImageIndex = (currentImageIndex - 1 + images.length) % images.length;
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_R) {
            loadImages();
            iterationCounts[currentImageIndex] = 0;
            repaint();
        }
    }

    private void repaintImmediately() {

        Graphics g = getGraphics();
        paint(g);
        g.dispose();
    }

    private static ArrayList<ArrayList<Pixel>> createDiagonalChains(BufferedImage image, int width, int height) {
    ArrayList<ArrayList<Pixel>> diagonalChains = new ArrayList<>();

    // Diagonals starting from the top row
    for (int startX = 0; startX < width; startX++) {
        int x = startX, y = 0;
        ArrayList<Pixel> chain = new ArrayList<>();
        while (x < width && y < height) {
            chain.add(new Pixel(x, y, image.getRGB(x, y)));
            x++;
            y++;
        }
        diagonalChains.add(chain);
    }

    // Diagonals starting from the left column, excluding the top-left corner
    for (int startY = 1; startY < height; startY++) {
        int x = 0, y = startY;
        ArrayList<Pixel> chain = new ArrayList<>();
        while (x < width && y < height) {
            chain.add(new Pixel(x, y, image.getRGB(x, y)));
            x++;
            y++;
        }
        diagonalChains.add(chain);
    }

    return diagonalChains;
}

private static void sortChain(ArrayList<Pixel> chain, int iterationCount) {
    // Reverse insertion sort
    for (int i = 1; i <= iterationCount && i < chain.size(); i++) {
        Pixel key = chain.get(i);
        int j = i - 1;

        while (j >= 0 && chain.get(j).brightness < key.brightness) {
            Collections.swap(chain, j + 1, j);
            j--;
        }
    }
}

/* private static void updateImage(BufferedImage image, ArrayList<Pixel> chain) {
    for (Pixel p : chain) {
        image.setRGB(p.x, p.y, p.rgb);
    }
} */

private static int getMaxChainLength(ArrayList<ArrayList<Pixel>> diagonalChains) {
    int maxLength = 0;
    for (ArrayList<Pixel> chain : diagonalChains) {
        if (chain.size() > maxLength) {
            maxLength = chain.size();
        }
    }
    return maxLength;
}

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
    private static void updateImage(BufferedImage image, ArrayList<ArrayList<Pixel>> diagonalChains) {
        for (ArrayList<Pixel> chain : diagonalChains) {
            for (Pixel p : chain) {
                image.setRGB(p.x, p.y, p.rgb);
            }
        }
    }

    private static class Pixel {
        int x, y, rgb;
        double brightness;

        public Pixel(int x, int y, int rgb) {
            this.x = x;
            this.y = y;
            this.rgb = rgb;
            this.brightness = calculateBrightness(rgb);
        }
    }

    public static void main(String[] args) {

        new ImageSorter();
    }
    
}


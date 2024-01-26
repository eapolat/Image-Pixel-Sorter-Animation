import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javax.imageio.ImageIO;

public class ImageSorter2 {

    private static ImagePanel imagePanel;
    private static JFrame frame;    
    private static List<BufferedImage> images = new ArrayList<>();
    private static List<Boolean> sortingStates = new ArrayList<>();
    private static int currentIndex = 0;
    private static Thread sortingThread = null;

    public static void main(String[] args) throws IOException {

        try {


        images.add(ImageIO.read(new File("view.jpg")));
        images.add(ImageIO.read(new File("deneme3.jpg")));
        images.add(ImageIO.read(new File("deneme2.png")));

                    
        for (int i = 0; i < images.size(); i++) {
            sortingStates.add(false); 
        }        

        frame = new JFrame("Sorting Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imagePanel = new ImagePanel(images.get(currentIndex));
        frame.add(imagePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {

                    if (sortingStates.get(currentIndex)) {
                        sortingThread.interrupt();
                    }

                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        currentIndex = (currentIndex - 1 + images.size()) % images.size();
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        currentIndex = (currentIndex + 1) % images.size();
                    }

                    imagePanel.setImage(images.get(currentIndex));
                    startSorting(images.get(currentIndex));
                }
                frame.pack();
            }
        });

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {

                    resetAndSortCurrentImage();
                }
                        
                frame.pack();

            }
        });

        startSorting(images.get(currentIndex));
    } catch (IOException e) {
        e.printStackTrace();
        System.exit(1); 
    }
}

private static void resetAndSortCurrentImage() {
    try {

        BufferedImage originalImage;
        switch (currentIndex) {
            case 0:
                originalImage = ImageIO.read(new File("view.jpg"));
                break;
            case 1:
                originalImage = ImageIO.read(new File("deneme3.jpg"));
                break;
            case 2:
                originalImage = ImageIO.read(new File("deneme2.png"));
                break;
            default:
                throw new IllegalStateException("Invalid image index");
        }

        if (sortingStates.get(currentIndex)) {
            sortingThread.interrupt();
        }

        images.set(currentIndex, originalImage);
        imagePanel.setImage(originalImage);

        startSorting(originalImage);
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    frame.pack();

}

private static void startSorting(BufferedImage image) {
    sortingThread = new Thread(() -> sortImage(image));
    sortingThread.start();
    sortingStates.set(currentIndex, true);
}

// selection sort
private static void sortImage(BufferedImage image) {
        
    int width = image.getWidth();
    int height = image.getHeight();

        
    for (int iterationCount = 1; iterationCount < width + height; iterationCount++) {
           
        boolean sorted = true;
            
        for (int start = 0; start < width + height - 1; start++) {
                
            int x = start < width ? start : width - 1;
            int y = start < width ? 0 : start - width + 1;

                
            for (int i = 0; i < iterationCount; i++) {

                if (x - i < 0 || y + i >= height) {      
                    break;
                }

                int minIndex = i;
                float minBrightness = Float.MAX_VALUE;
                for (int j = i; x - j >= 0 && y + j < height; j++) {
                    float brightness = calculateBrightness(new Color(image.getRGB(x - j, y + j)));
                    
                    if (brightness < minBrightness) {
                                
                        minIndex = j;
                        minBrightness = brightness;
                        sorted = false;
                            
                    }  
        
            
                }

                if (minIndex != i) {
                
                    int minColor = image.getRGB(x - minIndex, y + minIndex);
                    image.setRGB(x - minIndex, y + minIndex, image.getRGB(x - i, y + i));
                    image.setRGB(x - i, y + i, minColor);
            
                }
            }    
        }

        final int count = iterationCount;

        SwingUtilities.invokeLater(() -> {
               
            imagePanel.setImage(image);
            imagePanel.repaint();
            
        });
    
        if (sorted) { 
    
            break;     
   
        }
        try {
        
            Thread.sleep(5);
        
        } catch (InterruptedException e) {
        
            Thread.currentThread().interrupt();
            return;       
        }    

    }

    try {

        ImageIO.write(image, "jpg", new File("sorted_image.jpg"));

    } catch (IOException e) {
        e.printStackTrace();
    }
        
}
        
private static float calculateBrightness(Color color) {

    return 0.2126f * color.getRed() + 0.7152f * color.getGreen() + 0.0722f * color.getBlue();

}
}
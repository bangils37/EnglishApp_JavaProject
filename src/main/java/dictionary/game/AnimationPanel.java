package dictionary.game;

import javax.swing.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;

public class AnimationPanel extends JPanel implements Runnable {

    ImageView animationView;
    Thread animationThread;
    public Image[] victoryImage = new Image[17];

    int FPS = 60;

    int currentVictoryFrame = 0;

    public AnimationPanel() {
        this.setPreferredSize(new Dimension(900,800));
        this.setBackground(Color.black);
    }

    public AnimationPanel(ImageView imageView) {
        animationView = imageView;
    }

    public void startAnimationThread() {
        animationThread = new Thread(this);
        animationThread.start();

        loadImage();
    }

    public void stopAnimationThread() {
        if (animationThread != null) {
            System.out.println("Stopping thread: ");
            animationThread.interrupt();

            animationThread = null;
            if(animationView!= null)
                animationView.setImage(null);
        }
    }

    void loadImage() {
        try {
            for(int i=0; i<=16; i++)
            {
                String path = "/animation/victory/tile" + String.format("%03d", i) + ".png";
                victoryImage[i] = new Image(path);

                if(victoryImage[i] == null)
                {
                    System.out.println("load image false");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        double drawInterval = 1.0 * 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        long timer = 0;
        long drawCount = 0;

        while (animationThread != null) {

            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
//                repaint();
                delta--;
                drawCount++;
            }

            if(timer >= 1000000000)
            {
//                System.out.println("FPS: " + drawCount);

                drawCount = 0;
                timer = 0;
            }
        }
    }

    void update() {

        currentVictoryFrame++;
        if(currentVictoryFrame >=17)
            currentVictoryFrame = 0;
        animationView.setImage(victoryImage[currentVictoryFrame]);

    }
}

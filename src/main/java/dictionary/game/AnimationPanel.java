package dictionary.game;

import javax.swing.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;

public class AnimationPanel extends JPanel implements Runnable {

    ImageView animationView;
    ImageView animationNariRunView;
    ImageView animationAttackView;
    Thread animationThread;
    public Image[] victoryImage = new Image[17];

    public Image[] nariRunImage = new Image[10];

    public Image[] attackImage = new Image[17];

    int FPS = 60;
    long timer = 0;
    long drawCount = 0;

    int currentVictoryFrame = 0;
    int currentNariRunFrame = 0;
    int currentAttackFrame = 0;

    public AnimationPanel() {
        this.setPreferredSize(new Dimension(900, 800));
        this.setBackground(Color.black);
    }

    public AnimationPanel(ImageView imageView, ImageView nariRun, ImageView attackView) {
        animationView = imageView;

        animationNariRunView = nariRun;

        animationAttackView = attackView;
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
        }
    }

    void loadImage() {
        try {
            for (int i = 0; i <= 16; i++) {
                String path = "/animation/victory/tile" + String.format("%03d", i) + ".png";
                victoryImage[i] = new Image(path);
            }

            for (int i = 0; i <= 9; i++) {
                String path = "/animation/nariRun/run1_" + Integer.toString(i + 1) + ".png";
                nariRunImage[i] = new Image(path);
            }

            for (int i = 0; i <= 16; i++) {
                String path = "/animation/attack/attack0_" + Integer.toString(i + 1) + ".png";
                attackImage[i] = new Image(path);
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

            if (timer >= 1000000000) {
//                System.out.println("FPS: " + drawCount);

                drawCount = 0;
                timer = 0;
            }
        }
    }

    void setVictoryFrame() {
        if (drawCount % 3 == 0) {
            currentVictoryFrame++;
            if (currentVictoryFrame >= 17)
                currentVictoryFrame = 0;
            animationView.setImage(victoryImage[currentVictoryFrame]);
        }
    }
    void setNariRunFrame() {
        if (drawCount % 4 == 0) {
            currentNariRunFrame++;
            if (currentNariRunFrame >= 10)
                currentNariRunFrame = 0;
            animationNariRunView.setImage(nariRunImage[currentNariRunFrame]);
        }
    }

    void setAttackFrame() {
        if (drawCount % 3 == 0) {
            currentAttackFrame++;
            if (currentAttackFrame >= 17)
                currentAttackFrame = 0;
            animationAttackView.setImage(attackImage[currentAttackFrame]);
        }
    }

    void update() {
        setNariRunFrame();
        setVictoryFrame();
        setAttackFrame();
    }
}

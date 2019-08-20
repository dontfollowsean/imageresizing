package com.bouncex.imageresizing;

import com.google.common.base.Stopwatch;
import com.mortennobel.imagescaling.MultiStepRescaleOp;
import net.coobird.thumbnailator.Thumbnails;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class ImageResizer {
    private static final Logger LOG = Logger.getLogger("ImageResizer");


    public static void main(String[] args) {
        int dWidth = 100;
        int dHeight = 100;
        ImageResizer resizer = new ImageResizer();
        Stopwatch nobelTimer = Stopwatch.createUnstarted();
        Stopwatch imgsclrTimer = Stopwatch.createUnstarted();
        Stopwatch thumbnailatorTimer = Stopwatch.createUnstarted();
        Stopwatch jdkTimer = Stopwatch.createUnstarted();

        try {
            Map<String, BufferedImage> nobelScaledImages = new HashMap<>(), imgscalrScaledImages = new HashMap<>(), jdkScaledImages = new HashMap<>(), thumbnailatorScaledImages = new HashMap<>();
            File imageFolder = new File("./images/originals/");

            for (File image : Objects.requireNonNull(imageFolder.listFiles())) {
                BufferedImage imageToScale = ImageIO.read(image);

                nobelTimer.start();
                nobelScaledImages.put(image.getName(), resizer.nobelScaling(dWidth, dHeight, imageToScale));
                nobelTimer.stop();

                thumbnailatorTimer.start();
                thumbnailatorScaledImages.put(image.getName(), resizer.thumbnailatorScaling(dWidth, dHeight, imageToScale));
                thumbnailatorTimer.stop();

                jdkTimer.start();
                jdkScaledImages.put(image.getName(), resizer.jdkScaling(dWidth, dHeight, imageToScale));
                jdkTimer.stop();

                imgsclrTimer.start();
                imgscalrScaledImages.put(image.getName(), resizer.imgscalrScaling(dWidth, dHeight, imageToScale));
                imgsclrTimer.stop();
            }



            for (Map.Entry<String, BufferedImage> entry : nobelScaledImages.entrySet()) {
                String name = entry.getKey();
                BufferedImage image = entry.getValue();
                ImageIO.write(image, "jpg", new File(String.format("./images/nobel/nobelScaledImage-%s", name)));
            }

            for (Map.Entry<String, BufferedImage> entry : imgscalrScaledImages.entrySet()) {
                String name = entry.getKey();
                BufferedImage image = entry.getValue();
                ImageIO.write(image, "jpg", new File(String.format("./images/imgscalr/imgscalrScaledImage-%s", name)));
            }

            for (Map.Entry<String, BufferedImage> entry : thumbnailatorScaledImages.entrySet()) {
                String name = entry.getKey();
                BufferedImage image = entry.getValue();
                ImageIO.write(image, "jpg", new File(String.format("./images/thumbnailator/thumbnailatorScaledImage-%s", name)));
            }

            for (Map.Entry<String, BufferedImage> entry : jdkScaledImages.entrySet()) {
                String name = entry.getKey();
                BufferedImage image = entry.getValue();
                ImageIO.write(image, "jpg", new File(String.format("./images/jdk/jdkScaledImage-%s", name)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("Thumnailator: " + thumbnailatorTimer);
        LOG.info("Imgscalr: " + imgsclrTimer);
        LOG.info("NobelLib: " + nobelTimer);
        LOG.info("JDK: " + jdkTimer);

    }

    private BufferedImage nobelScaling(int width, int height, BufferedImage imageToScale) {
        return new MultiStepRescaleOp(width, height, RenderingHints.VALUE_INTERPOLATION_BILINEAR).filter(imageToScale, null);
    }

    private BufferedImage imgscalrScaling(int width, int height, BufferedImage imageToScale) {
        return Scalr.resize(imageToScale, Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, width, height);
    }

    private BufferedImage thumbnailatorScaling(int width, int height, BufferedImage imageToScale) throws IOException {
        return Thumbnails.of(imageToScale).size(width, height).outputFormat("jpg").asBufferedImage();
    }

    private BufferedImage jdkScaling(int width, int height, BufferedImage imageToScale) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(imageToScale, 0, 0, width, height, null);
        graphics2D.dispose();
        return scaledImage;
    }
}



package PP.DZ;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class DZ3 { //Множество мальдеброта. Боже храни нейронки за возможность добавления визуализации к коду

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int MAX_ITER = 1000;

    public static void main(String[] args) throws Exception {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        for (int y = 0; y < HEIGHT; y++) {
            final int currentY = y;

            executor.submit(() -> {
                for (int x = 0; x < WIDTH; x++) {

                    double c_re = (x - WIDTH / 2.0) * 4.0 / WIDTH - 0.5;
                    double c_im = (currentY - HEIGHT / 2.0) * 4.0 / WIDTH;

                    int iter = calculateMandelbrot(c_re, c_im);

                    int color = getColor(iter);
                    image.setRGB(x, currentY, color);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        File outputFile = new File("mandelbrot.png");
        ImageIO.write(image, "png", outputFile);
        System.out.println("Готово! Результат сохранен в mandelbrot.png");
    }

    private static int calculateMandelbrot(double c_re, double c_im) {
        double z_re = 0;
        double z_im = 0;
        int iter = 0;

        // z_{n+1} = z_n^2 + c
        // z_re^2 + z_im^2 > 4
        while (z_re * z_re + z_im * z_im <= 4.0 && iter < MAX_ITER) {
            // Re(z^2) = Re(z)^2 - Im(z)^2
            // Im(z^2) = 2 * Re(z) * Im(z)
            double z_re_new = z_re * z_re - z_im * z_im + c_re;
            double z_im_new = 2 * z_re * z_im + c_im;

            z_re = z_re_new;
            z_im = z_im_new;
            iter++;
        }

        return iter;
    }

    private static int getColor(int iter) {
        if (iter == MAX_ITER) {
            return Color.BLACK.getRGB();
        }
        return Color.HSBtoRGB((float) iter / MAX_ITER * 10f, 1f, 1f);
    }
}
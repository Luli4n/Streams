package pictures;

import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PicturesProcessing {


    private Stream<Path> pathStream;
    private Stream<Pair<String, BufferedImage>> origStream;
    private Stream<Pair<String, BufferedImage>> procStream;

    public void process(ForkJoinPool pool)
    {
        Path source = Path.of("./src/main/resources/original");
        List<Path> files;

        try (Stream<Path> stream = Files.list(source)){
            files = stream.collect(Collectors.toList());

            pool.submit(() -> {
                pathStream = files.stream().parallel();
                pathStream_to_nameAndOriginalPictureStream();
                nameAndOriginalPictureStream_to_nameAndProcessedPictureStream();
                saveProcessed();
            }).get();

        } catch (IOException | InterruptedException | ExecutionException e) {
            System.out.println("Couldn't load pictures.");
            System.exit(0);
        }
    }
    public void pathStream_to_nameAndOriginalPictureStream()
    {
        origStream = pathStream.map( value -> {

            String name = value.getFileName().toString();
            BufferedImage image = null;

            try {
                image = ImageIO.read(value.toFile());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return Pair.of(name, image);
        });
    }

    public void nameAndOriginalPictureStream_to_nameAndProcessedPictureStream()
    {
        procStream = origStream.map( value -> {

            String name = value.getLeft();
            BufferedImage image = processImage(value.getRight());

            return Pair.of(name, image);
        });
    }

    public BufferedImage processImage(BufferedImage original)
    {
        BufferedImage proc = new BufferedImage(original.getWidth(),
                original.getHeight(),
                original.getType());

        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {

                int rgb = original.getRGB(i, j);

                Color color = new Color(rgb);
                int red = color.getRed();
                int blue = color.getBlue();
                int green = color.getGreen();

                Color outColor = new Color(red, blue, green);
                int outRgb = outColor.getRGB();

                proc.setRGB(i, j, outRgb);
            }
        }
        return proc;
    }

    public void saveProcessed()
    {
        procStream.forEach(value -> {
            Path path = Path.of("./src/main/resources/processed/"+value.getLeft());
            File outputfile = new File(path.toString());
            try {
                ImageIO.write(value.getRight(), "jpg", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}

import controller.ProcessingController;
import pictures.PicturesProcessing;

public class Main {

    public static void main(String[] args) {

        ProcessingController proc = new ProcessingController(new PicturesProcessing());
        proc.initialize(args[0]);

    }
}

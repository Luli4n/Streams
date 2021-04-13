package controller;

import lombok.RequiredArgsConstructor;
import pictures.PicturesProcessing;

import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor

public class ProcessingController {

    private static final int LIMIT = 10;
    private final PicturesProcessing proc;

    public void initialize(String path)
    {
        ForkJoinPool pool = new ForkJoinPool(LIMIT);

        long time = System.currentTimeMillis();
        proc.process(pool, path);
        System.out.println(System.currentTimeMillis() - time+" milisekund(y)");

        pool.shutdown();
    }

}

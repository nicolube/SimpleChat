package de.nicolube.simplechat.client;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ResourceLoader {
    private final JProgressBar processBar;
    private List<Runnable> runnables;
    private int process;

    public ResourceLoader(JProgressBar progressBar) {
        this.runnables = new ArrayList<>();
        this.processBar = progressBar;
    }

    public void add(Runnable runnable) {
        this.runnables.add(runnable);
    }

    public CompletableFuture<Void> run() {
        this.processBar.setMinimum(0);
        this.processBar.setMaximum(runnables.size());
        return CompletableFuture.runAsync(() -> {
            for (Runnable runnable : runnables) {
                try {
                    runnable.run();
                    processBar.setValue(++process);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
}

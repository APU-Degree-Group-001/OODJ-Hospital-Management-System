/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodj_hospital_management_system.gui.utils.file_listener;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

/**
 *
 * @author sheaw
 */
public class FileWatcher {

    private final Path directory;
    private final WatchService watchService;
    private final Map<String, List<Runnable>> listeners = new HashMap<>();

    public FileWatcher(String filePath) throws IOException {

        Path file = Paths.get(filePath);
        this.directory = file.getParent();

        this.watchService = FileSystems.getDefault().newWatchService();

        directory.register(
            watchService,
            StandardWatchEventKinds.ENTRY_MODIFY
        );
    }

    public void addListener(String filePath, Runnable listener) {

        String fileName = Paths.get(filePath).getFileName().toString();

        listeners
            .computeIfAbsent(fileName, k -> new ArrayList<>())
            .add(listener);
    }

    public void start() {

        Thread thread = new Thread(() -> {

            try {

                while (true) {

                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {

                        if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) {
                            continue;
                        }

                        Path changedFile = (Path) event.context();

                        String fileName = changedFile.getFileName().toString();

                        List<Runnable> fileListeners =
                                listeners.get(fileName);

                        if (fileListeners != null) {

                            SwingUtilities.invokeLater(() -> {

                                for (Runnable listener : fileListeners) {
                                    listener.run();
                                }

                            });
                        }
                    }

                    key.reset();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
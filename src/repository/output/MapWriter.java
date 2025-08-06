package repository.output;

import model.MultiChildTreeNode;
import services.map.SiteMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MapWriter {
    private static final String MAPS_DIRECTORY_PATH = "Multithreading_task/map_files/";

    private final SiteMap map;
    private final Path path;

    private int linesCount = 0;

    public MapWriter(SiteMap map) {
        this.map = map;
        String fileName = map.getSiteName() + ".txt";
        path = Paths.get(MAPS_DIRECTORY_PATH + fileName);
    }

    public void write() {
        try {
            clear();
        } catch (Exception e) {
            System.out.println("Не удалось подготовить файл для записи");
        }

        try {
            print("", map.getMainNode(), 0);
        } catch (IOException e) {
            System.out.println("Запись в файл завершилась с ошибкой: ");
            e.printStackTrace();
        }

        System.out.println("Запись в файл завершена. Записано: " + linesCount + " строк");
    }

    private void clear() throws IOException {
        Files.writeString(
                path,
                "",
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private void print(String prefix, MultiChildTreeNode<String> node, int level) throws IOException {
        String part = node.getValue() + "/";
        String absolutePath = prefix + part;
        prefix = absolutePath;

        StringBuilder builder = new StringBuilder();
        builder.repeat("\t", level);
        builder.append(absolutePath);
        builder.append("\n");

        println(builder.toString());

        for (var child : node.getChildren()) {
            print(prefix, child, level + 1);
        }

        linesCount++;
    }

    private void println(String content) throws IOException {
        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
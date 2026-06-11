package repository.output;

import model.LinkPartNode;
import services.map.SiteMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MapWriter {
    private static final String OUTPUT_DIR = "Multithreading_task/map_files/";

    private final SiteMap map;
    private final Path path;

    private int linesCount = 0;

    public MapWriter(SiteMap map) {
        this.map = map;
        String fileName = map.getSiteName() + ".txt";
        path = Paths.get(OUTPUT_DIR + fileName);
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
        System.out.println("Полный путь файла: " + path.getFileName().toAbsolutePath());
    }

    private void clear() throws IOException {
        ensureDirectoryExists();
        Files.writeString(
                path,
                "",
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    private void ensureDirectoryExists() throws IOException {
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    private void print(String prefix, LinkPartNode node, int level) throws IOException {
        String part = node.getValue() + "/";
        String absolutePath = prefix + part;
        prefix = absolutePath;

        StringBuilder builder = new StringBuilder();
        builder.repeat("\t", level);
        builder.append(absolutePath);
        builder.append("\n");

        if (node.isPage()) {
            println(builder.toString());
        }

        for (var child : node.getChildren()) {
            var childNode = (LinkPartNode) child;
            print(prefix, childNode, level + 1);
        }

        linesCount++;
    }

    private void println(String content) throws IOException {
        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
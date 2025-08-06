package services.map.filler;

import model.MapFillerArgs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

public class SiteMapFiller extends RecursiveTask<List<String>> {
    private static final int MILLISECONDS_DELAY = 120;

    private final transient MapFillerArgs args;
    private final String currentPage;
    private final int currentLevel;

    public SiteMapFiller(MapFillerArgs args, int currentLevel, String currentPage) {
        this.args = args;
        this.currentPage = currentPage;
        this.currentLevel = currentLevel;
    }

    @Override
    protected List<String> compute() {
        List<String> urls;
        try {
            args.visitedLinks().add(currentPage);
            urls = args.parser().parse(currentPage);
        } catch (IOException e) {
            System.out.println("Не удалось открыть страницу: " + currentPage);
            return List.of();
        }
        processUrls(urls);
        return urls;
    }

    private void processUrls(List<String> urls) {
        List<SiteMapFiller> taskList = new LinkedList<>();
        for (var url : urls) {
            if (checkLink(url)) {
                var factory = args.linksFactory();
                var link = factory.createLink(url);
                args.map().addLink(link);

                if (currentLevel > args.maxSearchingLevel()) {
                    return;
                }

                SiteMapFiller task = new SiteMapFiller(args, currentLevel + 1, url);
                taskList.add(task);
                task.fork();
            }
        }
        try {
            sleep();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        System.out.println("Обработка страницы:  " + currentPage + " Найдено: " + taskList.size() + " ссылок");
        for (var task : taskList) {
            task.join();
        }
    }

    private void sleep() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(MILLISECONDS_DELAY);
    }

    private boolean checkLink(String link) {
        var visitedLinks = args.visitedLinks();
        if (visitedLinks.contains(link)) {
            return false;
        }
        visitedLinks.add(link);
        return true;
    }
}

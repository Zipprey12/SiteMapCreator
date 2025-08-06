package services.map.filler;

import model.MapFillerArgs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

public class SiteMapFiller extends RecursiveTask<List<String>> {
    private static final int MILLISECONDS_DELAY = 150;

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
        var count = args.getProcessedArticlesCount();
        if(count.get() >= args.getMaxArticlesCount()){
            return List.of();
        }
        count.incrementAndGet();

        List<String> urls;
        try {
            args.getVisitedLinks().add(currentPage);
            urls = args.getParser().parse(currentPage);
        } catch (IOException e) {
            return List.of();
        }
        processUrls(urls);
        return urls;
    }

    private void processUrls(List<String> urls) {
        List<SiteMapFiller> taskList = new LinkedList<>();

        int maxSearchingLevel = args.getMaxSearchingLevel();
        var factory = args.getLinksFactory();

        for (var url : urls) {
            if (checkLink(url)) {
                var link = factory.createLink(url);
                args.getMap().addLink(link);
                if (currentLevel > maxSearchingLevel) {
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

        System.out.println("Обработка страницы:  " + currentPage + " Найдено: " + taskList.size() + " новых ссылок");
        for (var task : taskList) {
            task.join();
        }
    }

    private void sleep() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(MILLISECONDS_DELAY);
    }

    private boolean checkLink(String link) {
        var visitedLinks = args.getVisitedLinks();
        if (visitedLinks.contains(link)) {
            return false;
        }
        visitedLinks.add(link);
        return true;
    }
}

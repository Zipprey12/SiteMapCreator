package services.map.filler;

import model.Link;
import model.MapFillerArgs;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

public class SiteMapFiller extends RecursiveTask<List<Link>> {
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
    protected List<Link> compute() {
        var count = args.getProcessedArticlesCount();
        if (count.get() >= args.getMaxArticlesCount()) {
            return List.of();
        }
        count.incrementAndGet();

        List<Link> urls;
        try {
            args.getVisitedLinks().add(currentPage);
            urls = args.getParser().parse(currentPage);
        } catch (IOException e) {
            return List.of();
        }
        if (!urls.isEmpty()) {
            processLinks(urls);
        }
        else {
            System.out.println("1");
        }
        return urls;
    }

    private void processLinks(List<Link> links) {
        List<SiteMapFiller> taskList = new LinkedList<>();

        int maxSearchingLevel = args.getMaxSearchingLevel();
        var factory = args.getLinksFactory();

        for (var link : links) {
            if (checkLink(link.getValue())) {
                args.getMap().addLink(link);
                if (currentLevel > maxSearchingLevel) {
                    return;
                }
                var absolutePath = factory.getAbsoluteUrl(link);
                SiteMapFiller task = new SiteMapFiller(args, currentLevel + 1, absolutePath);
                taskList.add(task);
                task.fork();
            }
        }
        sleep();

        System.out.println("Обработка страницы:  " + currentPage + " Найдено: " + taskList.size() + " новых ссылок");
        for (var task : taskList) {
            task.join();
        }
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(MILLISECONDS_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private boolean checkLink(String url) {
        var visitedLinks = args.getVisitedLinks();
        if (visitedLinks.contains(url)) {
            return false;
        }
        visitedLinks.add(url);
        return true;
    }
}

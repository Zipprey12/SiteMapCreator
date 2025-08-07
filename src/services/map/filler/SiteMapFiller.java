package services.map.filler;

import model.Link;
import model.MapFillerArgs;
import services.links.LinksFactory;

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

    private int maxSearchingLevel;
    private LinksFactory factory;

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

        List<Link> links;
        try {
            args.getVisitedLinks().add(currentPage);
            links = args.getParser().parse(currentPage);
        } catch (IOException e) {
            return List.of();
        }
        factory = args.getLinksFactory();
        maxSearchingLevel = args.getMaxSearchingLevel();
        processLinks(links);
        return links;
    }

    private void processLinks(List<Link> links) {
        List<SiteMapFiller> taskList = new LinkedList<>();
        for (var link : links) {
            if (!checkUrlVisit(link.getValue())) {
                continue;
            }
            var task = processLink(link);
            if (task != null) {
                task.fork();
                taskList.add(task);
            }
        }
        sleep();

        showStatus(currentPage, links.size(), taskList.size());
        for (var task : taskList) {
            task.join();
        }
    }

    private boolean checkUrlVisit(String url) {
        var visitedLinks = args.getVisitedLinks();
        if (visitedLinks.contains(url)) {
            return false;
        }
        visitedLinks.add(url);
        return true;
    }

    private SiteMapFiller processLink(Link link) {
        args.getMap().addLink(link);
        if (currentLevel >= maxSearchingLevel) {
            return null;
        }
        var absolutePath = factory.getAbsoluteUrl(link);
        return new SiteMapFiller(args, currentLevel + 1, absolutePath);
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(MILLISECONDS_DELAY);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private void showStatus(String link, int linksCount, int tasksCount) {
        String out = String.format("Обработка страницы:  %1$s  Ссылок найдено: %2$d. Новых: %3$d",
                link, linksCount, tasksCount);
        System.out.println(out);
    }
}

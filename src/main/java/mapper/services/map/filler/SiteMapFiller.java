package mapper.services.map.filler;

import lombok.extern.slf4j.Slf4j;
import mapper.model.Link;
import mapper.model.MapFillerArgs;
import mapper.services.links.LinksFactory;
import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SiteMapFiller extends RecursiveTask<List<Link>> {
    private final transient MapFillerArgs args;
    private final String currentPage;
    private final int currentLevel;

    private final int maxSearchingLevel;
    private final LinksFactory factory;

    public SiteMapFiller(MapFillerArgs args, String currentPage, int currentLevel) {
        this.args = args;
        this.currentPage = currentPage;
        this.currentLevel = currentLevel;
        factory = args.getLinksFactory();
        maxSearchingLevel = args.getMaxSearchingLevel();
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
        } catch (SocketException | SocketTimeoutException e) {
            args.getFailedRequestsCount().incrementAndGet();
            log.warn("Разрыв соединения [{}]: {}", currentPage, e.getMessage());
            return List.of();
        } catch (HttpStatusException e) {
            args.getFailedRequestsCount().incrementAndGet();
            log.warn("Ошибка HTTP {} [{}]", e.getStatusCode(), currentPage);
            return List.of();
        } catch (IOException e) {
            args.getFailedRequestsCount().incrementAndGet();
            log.error("Ошибка запроса [{}]: {}", currentPage, e.getMessage());
            return List.of();
        }
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
        return new SiteMapFiller(args, absolutePath, currentLevel + 1);
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(args.getRequestDelayMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void showStatus(String link, int linksCount, int tasksCount) {
        log.info("Обработка страницы: {}   Ссылок найдено: {}. Новых: {}", link, linksCount, tasksCount);
    }
}

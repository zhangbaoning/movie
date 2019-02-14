package idyjy;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.List;
import java.util.Random;

/**
 * @Description
 * @Author zhangbaoning
 * @Date 2019/2/14
 */
public class IdyjyPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);

    public void process(Page page) {

        try {
            Thread.sleep(1000);
            page.putField("title", page.getHtml().xpath("//div[@class='h1title']/h1/span[@id='name']/text()"));
            page.putField("url", page.getHtml().xpath("//input[@class='down-true-url']/@value").toString());

            page.addTargetRequests(page.getHtml().regex("http://www\\.idyjy\\.com/sub/[0-9]+\\.html").all());
            List<String> downList =page.getHtml().regex("/down/[0-9]+\\-[0-9]+\\-[0-9]+\\.html").all();
            System.out.println(downList);
            for (int i = 0; i <downList.size() ; i++) {
                String url = downList.get(i);
                downList.set(i,"http://www.idyjy.com"+url);
            }
            page.addTargetRequests(downList);
            if (page.getResultItems().get("url")==null) {
                page.setSkip(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        try {
            HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
            httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("106.42.43.77",9999)));
            //spider.setDownloader();
            Spider.create(new IdyjyPageProcessor()).addUrl("http://www.idyjy.com")
                   .setDownloader(httpClientDownloader)
                    .addPipeline(new JsonFilePipeline("/Users/zhangbaoning/java"))
                    .addPipeline(new ConsolePipeline())
                    .thread(1).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

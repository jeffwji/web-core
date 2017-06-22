package net.tinybrick.web.configure;

/**
 * Created by ji.wang on 2017-06-21.
 */
public class WebResources {
    String[] staticResources = null;

    public void setStaticResources(String[] staticResources) {
        this.staticResources = staticResources;
    }

    public String[] getStaticResources() {
        return null == staticResources? new String[]{
                "classpath:/images/",
                "classpath:/css/",
                "classpath:/static/",
                "classpath:/public/",
                "classpath:/js/"
        } :staticResources;
    }
}

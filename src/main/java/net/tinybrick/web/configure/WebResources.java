package net.tinybrick.web.configure;

import java.util.Map;

/**
 * Created by ji.wang on 2017-06-21.
 */
public class WebResources {
    Map<String, String> staticResources = null;

    public void setStaticResources(Map<String, String> staticResources) {
        this.staticResources = staticResources;
    }

    public Map<String, String> getStaticResources() {
        return staticResources;
    }
}

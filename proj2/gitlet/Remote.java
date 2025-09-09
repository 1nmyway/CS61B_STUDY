package gitlet;

import java.io.Serializable;
import java.util.Map;

public class Remote implements Serializable {
    private Map<String,String> remoteNameToUrl;
    public Map<String,String> getRemoteNameToUrl() {
        return remoteNameToUrl;
    }
    public void setRemoteNameToUrl(Map<String,String> remoteNameToUrl) {
        this.remoteNameToUrl = remoteNameToUrl;
    }
}

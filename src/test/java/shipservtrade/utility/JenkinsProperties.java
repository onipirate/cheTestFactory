package shipservtrade.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JenkinsProperties {

    private static Logger log = LogManager.getLogger(JenkinsProperties.class.getName());

    String browser;
    String weburl;

    public void setJenkinsWebAppURL(){
        try{
            weburl = System.getProperty("weburl");
        }
        catch (NullPointerException e){
            log.debug("No value passed from Jenkins. URL is set to {} ", weburl, e);
            weburl = null;
        }
    }

    public String getJenkinsWebAppURL() {
        setJenkinsWebAppURL();
        return weburl;
      }

    public void setJenkinsBrowser() {
        try{
            browser = System.getProperty("browser");
        }
        catch (NullPointerException e) {
            log.debug("No value passed from Jenkins. Browser is set to {} ", browser, e);
            browser = null;
        }
    }

    public String getJenkinsBrowser() {
        setJenkinsBrowser();
        return browser;
    }

}

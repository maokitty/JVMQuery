package tp.jdi;

import tp.jdi.domain.App;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by liwangchun on 17/4/27.
 */
public class AppListTarget {
    public static void main(String[] args) {
        AppListTarget alt = new AppListTarget();
        List<App> apps = new ArrayList<App>();
        int count=0;
        while(count++<1000){
            try {
                TimeUnit.SECONDS.sleep(1);
                if (apps == null || apps.size()<10)
                {
                    apps=alt.classPrepareTest(count);
                }
                if (count%10 == 0)
                {
                    System.out.println(apps.size());
                    for (App app:apps){
                        System.out.println(app.getSystem()+":"+app.getVersion());
                    }
                    apps = null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public List<App> classPrepareTest(int count){
        List<App> apps1 = new ArrayList<App>();
        App app = new App();
        app.setSystem(count);
        app.setVersion("4.3.5");
        apps1.add(app);
        App app2 = new App();
        app2.setSystem(-1);
        app2.setVersion("3.2.1");
        apps1.add(app2);
        return apps1;
    }
}

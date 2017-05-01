package tp.jdi;

import tp.jdi.domain.App;

import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTarget
{
//    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9999
    public static void main(String[] args) {
        AppTarget appTest = new AppTarget();
        System.out.println(appTest.getClass().getName());
        String str = "HelloWorld";
        System.out.println(str);
        int count=0;
        while(count++<1000){
            try {
                TimeUnit.SECONDS.sleep(1);
                App app=appTest.classPrepareTest(count);
                System.out.println(app.getSystem());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
}
    public App classPrepareTest(int count){
        App app = new App();
        app.setSystem(count);
        app.setVersion("4.3.5");
        return app;
    }
}

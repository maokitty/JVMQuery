package tp.jdi;

import org.apache.log4j.PropertyConfigurator;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liwangchun on 17/5/1.
 */
public class QueryTest {
    static {
        PropertyConfigurator.configure("./src/test/java/resources/log4j.properties");
    }
    public static void main(String[] args) {
        localVarTest();
//        methodTrace();
//        localVarMethodTest();
//        listTest();
    }

    public static void localVarTest(){
        QConnection qConn = new QConnection.Param().host("localhost").port(9999).timeout(1000).create();
        Coordinate coordinate = new Coordinate.Param().className("tp.jdi.AppTarget")
                .lineNumber(23).sourceFileName("AppTarget.java").varName("app").objField("system").build();
        Query query = new Query(qConn,coordinate);
        query.lovalVar();
    }
    public static void methodTrace(){
        QConnection qConn = new QConnection.Param().host("localhost").port(9999).timeout(1000).create();
        Coordinate coordinate = new Coordinate.Param().entryClassName("tp.jdi.domain.App").entryClasssMethods("getSystem")
                .exitClasssMethods("getSystem").exitMethodClass("tp.jdi.domain.App").build();
        Query query = new Query(qConn,coordinate);
        query.methodTrace();
    }

    public static void localVarMethodTest(){
        QConnection qConn = new QConnection.Param().host("localhost").port(9999).timeout(1000).create();
        Coordinate coordinate = new Coordinate.Param().entryClassName("tp.jdi.domain.App").entryClasssMethods("getSystem")
                .exitClasssMethods("getSystem").exitMethodClass("tp.jdi.domain.App")
                .className("tp.jdi.AppTarget")
                .lineNumber(23).sourceFileName("AppTarget.java").varName("app").build();
        Query query = new Query(qConn,coordinate);
        query.localVarMethodTrace();

    }

    public static void listTest(){
        QConnection qConn = new QConnection.Param().host("localhost").port(9999).timeout(1000).create();
        Coordinate q = new Coordinate.Param().sourceFileName("AppListTarget.java")
            .className("tp.jdi.AppListTarget")
            .lineNumber(26).varName("apps").build();
        Query query = new Query(qConn,q);
        query.lovalVar();
    }
}

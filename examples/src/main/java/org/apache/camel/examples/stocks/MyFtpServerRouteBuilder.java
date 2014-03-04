package org.apache.camel.examples.stocks;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.util.jndi.JndiContext;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;


/**
 * Server route
 */
public class MyFtpServerRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // configure properties component
        PropertiesComponent pc = getContext().getComponent("properties", PropertiesComponent.class);
        pc.setLocation("classpath:ftp.properties");

        // lets shutdown faster in case of in-flight messages stack up
        getContext().getShutdownStrategy().setTimeout(10);

        from("{{ftp.server}}")
                .to("file:target/download")
                .to("sql:{{sql.insertTicker}}")
                .log("Downloaded file ${file:name} complete.");


        // use system out so it stand out
        System.out.println("*********************************************************************************");
        System.out.println("Camel will route files from the FTP server: "
                + getContext().resolvePropertyPlaceholders("{{ftp.server}}") + " to the target/download directory.");
        System.out.println("You can configure the location of the ftp server in the src/main/resources/ftp.properties file.");
        System.out.println("Use ctrl + c to stop this application.");
        System.out.println("*********************************************************************************");
    }

    private DataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("{{database.driver}}");
        ds.setUsername("{{database.name}}");
        ds.setPassword("{{database.password}}");
        ds.setUrl("{{database.url}}");
        return ds;
    }


}

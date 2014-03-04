package org.apache.camel.examples.stocks;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.commons.dbcp.BasicDataSource;

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

        //Database configuration
        DataSource dataSource = setupDataSource();
        SimpleRegistry reg = new SimpleRegistry();
        reg.put("myDataSource", dataSource);
        CamelContext context = new DefaultCamelContext(reg);

        from("{{ftp.server}}")
                .setBody(constant("insert into closing_price(symbol, trading_date) values(#,#)"))
                .to("file:target/download")
                .to("jdbc:myDataSource")
                .log("Downloaded file ${file:name} complete.");


        // use system out so it stand out
        System.out.println("*********************************************************************************");
        System.out.println("Camel will route files from the FTP server: "
                + getContext().resolvePropertyPlaceholders("{{ftp.server}}") + " to the target/download directory.");
        System.out.println("You can configure the location of the ftp server in the src/main/resources/ftp.properties file.");
        System.out.println("Use ctrl + c to stop this application.");
        System.out.println("*********************************************************************************");
    }

    private DataSource setupDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("{{database.driver}}");
        ds.setUsername("{{database.name}}");
        ds.setPassword("{{database.password}}");
        ds.setUrl("{{database.url}}");
        return ds;
    }


}

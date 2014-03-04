package org.apache.camel.examples.stocks;

import org.apache.camel.main.Main;

/**
 * Main class that can download files from an existing FTP server.
 */
public final class MyFtpServer {

    private MyFtpServer() {
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.addRouteBuilder(new MyFtpServerRouteBuilder());
        main.enableHangupSupport();
        main.run();
    }

}
package net.tinybrick.web.configure;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.print.attribute.standard.PrinterLocation;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ji.wang on 2017-08-02.
 */
@Service
public class ShutdownManager {
    public final static String shutdown_command="shutdown";
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ApplicationContext appContext;

    @Value("${service.shutdown.listenPort:}")  Integer portNumber;
    @Value("${service.shutdown.localOnly:true}")  Boolean localOnly;
    @Value("${server.port}")  int servicePort;

    ServerSocket serverSocket = null;
    InetAddress localAddress = null;

    @PostConstruct
    public void listen() {
        if (null == portNumber){
            portNumber = 10000 + servicePort;
            logger.info("Command port is: " + portNumber);
        }

        new Thread() {
            @Override
            public void run() {
                Socket socket = null;
                InputStreamReader isr = null;
                PrintWriter pw = null;

                try
                {
                    if (localOnly) {
                        localAddress = InetAddress.getLocalHost();
                        logger.info("Command bind to: " + localAddress);
                        serverSocket = new ServerSocket(portNumber, 50, localAddress);
                    } else {
                        serverSocket = new ServerSocket(portNumber);
                    }

                    socket = serverSocket.accept();
                    isr = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    OutputStream os = socket.getOutputStream();
                    pw = new PrintWriter(os, true);
                    while (true) {
                        String str = br.readLine();
                        if (str.toLowerCase().equals(shutdown_command)) {
                            teardown(0);
                            pw.println("Bye!");
                            break;
                        } else {
                            pw.println("Invalid command: " + str);
                        }
                    }
                } catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                } finally{
                    try {
                        socket.close();
                        isr.close();
                        pw.close();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }.start();
    }
    private void teardown(int returnCode){
        SpringApplication.exit(appContext, () -> returnCode);
    }

    public static void shutdown(String hostname, int portNumber) throws IOException {
        InetAddress serverAddress = InetAddress.getByName(hostname);
        Socket socket = new Socket(hostname/*serverAddress*/, portNumber);
        while(socket.isBound()) {
            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);
            out.write(shutdown_command);
            out.flush();

            BufferedReader isr =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            BufferedReader br = new BufferedReader(isr);
            System.out.println(br.readLine());
            br.close();
            isr.close();
            out.close();
        }
    }
}

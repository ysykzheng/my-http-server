package com.dadazhishi.my_http_server;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.PathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyHttpServer {

  private final Integer port;
  private final String basePath;
  private Server server;

  public MyHttpServer(Integer port, String basePath) {
    this.port = port;
    this.basePath = basePath;
  }

  public static void main(final String[] args) throws Exception {
    ArgumentParser parser = ArgumentParsers.newFor("java -jar my-http-server-*-jar-with-dependencies.jar").build()
        .defaultHelp(true)
        .description("simple http server for serving static files");
    parser.addArgument("-p", "--port").setDefault(8899)
        .required(false)
        .help("http port");
    parser.addArgument("-d", "--dir")
        .required(true)
        .help("static file dir");
    Namespace ns;
    try {
      ns = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
      return;
    }
    Integer port = ns.getInt("port");
    String dir = ns.getString("dir");

    MyHttpServer server = new MyHttpServer(port, dir);
    server.start();
  }

  public void stop() throws Exception {
    server.stop();
  }

  public void start() throws Exception {
    Path path = Paths.get(basePath);
    server = new Server(port);
    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setEtags(true);
    resourceHandler.setAcceptRanges(true);

    PathResource pathResource = new PathResource(path);
    resourceHandler.setBaseResource(pathResource);
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
    server.setHandler(handlers);
    server.start();
    server.join();
  }
}

package com.andyadc.jerrymouse;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class SimpleHttpServer implements HttpHandler, AutoCloseable {

    final static Logger logger = LoggerFactory.getLogger(SimpleHttpServer.class);
    final HttpServer httpServer;
    final String host;
    final int port;
    public SimpleHttpServer(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

        InetSocketAddress addr = new InetSocketAddress(host, port);
        this.httpServer = HttpServer.create();
        this.httpServer.bind(addr, 0);
        this.httpServer.createContext("/", this);

        this.httpServer.start();
        logger.info("Start jerrymouse http server at {}:{}", host, port);
    }

    public static void main(String[] args) {
        String host = "0.0.0.0";
        int port = 8081;
        try (SimpleHttpServer connector = new SimpleHttpServer(host, port)) {
            for (; ; ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("SimpleHttpServer error", e);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String query = uri.getRawQuery();
        logger.info("{}: {}?{}", method, path, query);
        Headers respHeaders = exchange.getResponseHeaders();
        respHeaders.set("Content-Type", "text/html; charset=utf-8");
        respHeaders.set("Cache-Control", "no-cache");
        // 设置200响应:
        exchange.sendResponseHeaders(200, 0);
        String s = "<h1>Hello, world.</h1><p>" + LocalDateTime.now().withNano(0) + "</p>";
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(s.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void close() throws Exception {
        this.httpServer.stop(3);
    }

}

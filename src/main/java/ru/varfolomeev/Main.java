package ru.varfolomeev;

import io.reactivex.netty.protocol.http.server.HttpServer;
import ru.varfolomeev.controller.Controller;
import rx.Observable;

public class Main {
    public static void main(final String[] args) {
        HttpServer.newServer(8080)
                .start((req, resp) -> switch (req.getHttpMethod().name()) {
                    case "GET" -> Controller.handleGET(req, resp);
                    case "POST" -> Controller.handlePOST(req, resp);
                    default -> Observable.empty();
                })
                .awaitShutdown();
    }
}

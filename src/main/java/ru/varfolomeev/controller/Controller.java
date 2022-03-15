package ru.varfolomeev.controller;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import ru.varfolomeev.MongoDriver;
import ru.varfolomeev.domain.Product;
import ru.varfolomeev.domain.User;
import ru.varfolomeev.view.HTMLRenderer;
import rx.Observable;

import ru.varfolomeev.domain.Currency;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {
    public static Observable<Void> handleGET(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp) {
        switch (req.getDecodedPath()) {
            case "/":
                Optional<Cookie> login = Stream.ofNullable(req.getCookies().get("login")).flatMap(Set::stream).findFirst();

                if (login.isEmpty()) {
                    return resp.writeString(Observable.just(HTMLRenderer.loginPage()));
                } else {
                    return resp.writeString(renderProductPage(login.get().value()));
                }
            case "/register":
                return resp.writeString(Observable.just(HTMLRenderer.registerPage()));
            case "/login":
                return resp.writeString(Observable.just(HTMLRenderer.loginPage()));
            default:
                return Observable.empty();
        }
    }

    public static Observable<Void> handlePOST(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp) {
        Observable<Map<String, String>> parameters =
                req.getContent().map(bb -> getParameters(bb.toString(StandardCharsets.UTF_8)));
        switch (req.getDecodedPath()) {
            case "/register":
                return parameters.map(m -> {
                    String login = m.get("login");
                    String password = m.get("password");
                    Currency currency = Currency.valueOf(m.get("currency").toUpperCase(Locale.ROOT));
                    return new User(login, password, currency);
                }).flatMap(u ->
                        MongoDriver.getUserByLogin(u.getLogin()).isEmpty().flatMap(check -> {
                            if (check) {
                                MongoDriver.save(u).subscribe();
                                resp.addCookie(new DefaultCookie("login", u.getLogin()));
                                return resp.writeString(renderProductPage(u.getLogin()));
                            } else {
                                return resp.writeString(Observable.just(HTMLRenderer.registerPage()));
                            }
                        })
                );
            case "/login":
                return parameters.flatMap(m -> {
                    String login = m.get("login");
                    String password = m.get("password");
                    Observable<User> user = MongoDriver.getUserByLogin(login)
                            .filter(u -> u.getPassword().equals(password));
                    return user.isEmpty().flatMap(check -> {
                        if (check) {
                            return resp.writeString(Observable.just(HTMLRenderer.loginPage()));
                        } else {
                            Cookie cookie = new DefaultCookie("login", login);
                            return resp.addCookie(cookie).writeString(renderProductPage(login));
                        }
                    });
                });
            case "/add-product":
                Optional<Cookie> login = Stream.ofNullable(req.getCookies().get("login")).flatMap(Set::stream).findFirst();

                if (login.isEmpty()) {
                    return resp.writeString(Observable.just(HTMLRenderer.loginPage()));
                } else {
                    return resp.writeString(
                            parameters.flatMap(m -> {
                                String name = m.get("name");
                                double price = Double.parseDouble(m.get("price"));
                                return MongoDriver.getUserByLogin(login.get().value()).flatMap(u -> {
                                    MongoDriver.save(new Product(name, u.getCurrency().toRubles(price))).subscribe();
                                    return renderProductPage(u.getLogin());
                                });
                            })
                    );
                }
            default:
                return Observable.empty();
        }
    }

    private static Observable<String> renderProductPage(String login) {
        Observable<User> user = MongoDriver.getUserByLogin(login);
        return MongoDriver.getAllProducts().toList()
                .flatMap(ps -> user.map(u -> HTMLRenderer.productPage(ps, u)));
    }

    private static Map<String, String> getParameters(String query) {
        return Arrays.stream(query.split("&"))
                .filter(Predicate.not(String::isEmpty))
                .collect(Collectors.toMap(p -> p.substring(0, p.indexOf("=")), p -> p.substring(p.indexOf("=") + 1)));
    }
}

package ru.varfolomeev.view;

import j2html.tags.specialized.ButtonTag;
import j2html.tags.specialized.InputTag;
import ru.varfolomeev.domain.Product;
import ru.varfolomeev.domain.User;

import java.util.List;

import static j2html.TagCreator.*;

public class HTMLRenderer {
    public static String productPage(List<Product> products, User user) {
        return html(
                body(
                        table(
                                tbody(
                                        tr(
                                                th("Name"),
                                                th(String.format("Price(%s)", user.getCurrency().name()))
                                        ),
                                        each(
                                                products,
                                                p -> tr(
                                                        td(p.getName()),
                                                        td(Double.toString(user.getCurrency().fromRubles(p.getRublePrice())))
                                                )
                                        )
                                )
                        ),
                        form().withMethod("post").withAction("/add-product").with(
                                textInput("name"),
                                numberInput("price"),
                                submitButton("Add Product")
                        )
                )
        ).renderFormatted();
    }

    public static String loginPage() {
        return html(
                body(
                        h1("Please, log in"),
                        form().withMethod("post").withAction("/login").with(
                                textInput("login"),
                                passwordInput("password"),
                                submitButton("Login")
                        ),
                        p(
                                a("Register").withHref("/register")
                        )
                )
        ).renderFormatted();
    }

    public static String registerPage() {
        return html(
                body(
                        h1("Please, sign up"),
                        form().withMethod("post").withAction("/register").with(
                                textInput("login"),
                                passwordInput("password"),
                                textInput("currency").withPlaceholder("Preferable currency (RUB, USD or EUR)"),
                                submitButton("Register")
                        )
                )
        ).renderFormatted();
    }

    public static InputTag textInput(String id) {
        return genericInput("text", id);
    }

    public static InputTag numberInput(String id) {
        return genericInput("number", id);
    }

    public static InputTag passwordInput(String id) {
        return genericInput("password", id);
    }

    public static InputTag genericInput(String type, String id) {
        return input()
                .withType(type)
                .withName(id)
                .withId(id)
                .isRequired();
    }

    public static ButtonTag submitButton(String text) {
        return button(text).withType("submit");
    }
}

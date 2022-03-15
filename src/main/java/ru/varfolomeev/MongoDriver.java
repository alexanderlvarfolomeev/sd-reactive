package ru.varfolomeev;

import com.mongodb.rx.client.*;
import org.bson.Document;
import ru.varfolomeev.domain.Product;
import ru.varfolomeev.domain.User;
import rx.Observable;

import static com.mongodb.client.model.Filters.eq;

public class MongoDriver {
    public static MongoDatabase database = createMongoClient().getDatabase("reactive");

    public static MongoCollection<Document> users = database.getCollection("users");

    public static MongoCollection<Document> products = database.getCollection("products");

    public static Observable<Product> getAllProducts() {
        return products.find().toObservable().map(d -> Product.fromDocument(d, Product.class));
    }

    public static Observable<User> getUserByLogin(String login) {
        return users.find(eq("login", login)).first().map(d -> User.fromDocument(d, User.class));
    }

    public static Observable<Success> save(User user) {
        return users.insertOne(user.toDocument());
    }

    public static Observable<Success> save(Product product) {
        return products.insertOne(product.toDocument());
    }


    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}

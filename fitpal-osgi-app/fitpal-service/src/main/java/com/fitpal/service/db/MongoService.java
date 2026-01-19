package com.fitpal.service.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import java.util.Map;

// This service is available to ALL repositories
@Component(service = MongoService.class, configurationPid = "com.fitpal.app")
public class MongoService {

    private MongoClient mongoClient;
    private MongoDatabase database;

    @Activate
    public void activate(Map<String, Object> properties) {
        String uri = (String) properties.get("mongo.uri");
        String dbName = (String) properties.get("mongo.database");

        this.mongoClient = MongoClients.create(uri);
        this.database = mongoClient.getDatabase(dbName);
    }

    @Deactivate
    public void deactivate() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    // Public method for Repositories to call
    public MongoDatabase getDatabase() {
        return this.database;
    }
}
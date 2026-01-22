package com.fitpal.service.db;

import com.fitpal.api.Badge;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

@Component(service = BadgeRepository.class)
public class BadgeRepository {

    @Reference
    private MongoService mongoService;

    private MongoCollection<Document> getCollection() {
        return mongoService.getDatabase().getCollection("badges");
    }

    public Badge save(Badge badge) {
        Document doc = mapBadgeToDocument(badge);
        if (badge.getId() == null) {
            getCollection().insertOne(doc);
            badge.setId(doc.getObjectId("_id").toString());
        } else {
            getCollection().replaceOne(new Document("_id", badge.getId()), doc);
        }
        return badge;
    }

    public List<Badge> findAll() {
        List<Badge> list = new ArrayList<>();
        getCollection().find().forEach(doc -> list.add(mapDocumentToBadge(doc)));
        return list;
    }

    private Badge mapDocumentToBadge(Document doc) {
        Badge badge = new Badge();
        badge.setId(doc.getObjectId("_id").toString());
        badge.setName(doc.getString("name"));
        badge.setDescription(doc.getString("description"));
        badge.setThreshold(doc.getDouble("threshold"));
        return badge;
    }

    private Document mapBadgeToDocument(Badge badge) {
        Document doc = new Document();
        // Don't map ID back, as it's managed by MongoDB
        doc.append("name", badge.getName());
        doc.append("description", badge.getDescription());
        doc.append("threshold", badge.getThreshold());
        return doc;
    }
}

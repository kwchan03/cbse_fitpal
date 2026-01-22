package com.fitpal.service.db;

import com.fitpal.api.Preference;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = PreferenceRepository.class)
public class PreferenceRepository {

    @Reference
    private MongoService mongoService;

    private MongoCollection<Document> getCollection() {
        return mongoService.getDatabase().getCollection("notification_preferences");
    }

    public Preference findByUserId(String userId) {
        Document doc = getCollection().find(Filters.eq("userId", userId)).first();
        return (doc != null) ? mapDocToPref(doc) : null;
    }

    public Preference save(Preference pref) {
        Document doc = mapPrefToDoc(pref);
        getCollection().replaceOne(
                Filters.eq("userId", pref.getUserId()),
                doc,
                new ReplaceOptions().upsert(true)
        );
        return findByUserId(pref.getUserId());
    }

    private Document mapPrefToDoc(Preference p) {
        Document doc = new Document();
        if (p.getId() != null) doc.append("_id", new ObjectId(p.getId()));
        doc.append("userId", p.getUserId());
        doc.append("pushEnabled", p.getPushEnabled());
        doc.append("emailEnabled", p.getEmailEnabled());
        doc.append("doNotDisturb", p.getDoNotDisturb());
        return doc;
    }

    private Preference mapDocToPref(Document doc) {
        Preference p = new Preference();
        p.setId(doc.getObjectId("_id").toString());
        p.setUserId(doc.getString("userId"));
        p.setPushEnabled(doc.getBoolean("pushEnabled", true));
        p.setEmailEnabled(doc.getBoolean("emailEnabled", false));
        p.setDoNotDisturb(doc.getBoolean("doNotDisturb", false));
        return p;
    }
}
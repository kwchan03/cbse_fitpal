package com.fitpal.service.db;

import com.fitpal.api.Steps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

@Component(service = StepsRepository.class)
public class StepsRepository {

    @Reference
    private MongoService mongoService;

    private MongoCollection<Document> getCollection() {
        return mongoService.getDatabase().getCollection("steps");
    }

    public Steps save(Steps steps) {
        Document doc = mapStepsToDocument(steps);
        if (steps.getId() == null) {
            ObjectId newId = new ObjectId();
            doc.append("_id", newId);
            steps.setId(newId.toString());
            getCollection().insertOne(doc);
        } else {
            getCollection().replaceOne(Filters.eq("_id", new ObjectId(steps.getId())), doc);
        }
        return steps;
    }

    public List<Steps> findByUserIdAndDate(String userId, String date) {
        List<Steps> list = new ArrayList<>();
        getCollection()
                .find(Filters.and(
                        Filters.eq("userId", userId),
                        Filters.eq("date", date)
                ))
                .forEach(doc -> list.add(mapDocumentToSteps(doc)));
        return list;
    }

    public List<Steps> findByUserIdAndDateBetween(String userId, String startDate, String endDate) {
        List<Steps> list = new ArrayList<>();
        getCollection()
                .find(Filters.and(
                        Filters.eq("userId", userId),
                        Filters.gte("date", startDate),
                        Filters.lte("date", endDate)
                ))
                .forEach(doc -> list.add(mapDocumentToSteps(doc)));
        return list;
    }

    public List<Steps> findByUserId(String userId) {
        List<Steps> list = new ArrayList<>();
        getCollection()
                .find(Filters.eq("userId", userId))
                .forEach(doc -> list.add(mapDocumentToSteps(doc)));
        return list;
    }

    private Document mapStepsToDocument(Steps steps) {
        Document doc = new Document();
        if (steps.getId() != null) {
            doc.append("_id", new ObjectId(steps.getId()));
        }
        doc.append("userId", steps.getUserId());
        doc.append("date", steps.getDate());
        doc.append("steps", steps.getSteps());
        doc.append("distance", steps.getDistance());
        doc.append("calories", steps.getCalories());
        return doc;
    }

    private Steps mapDocumentToSteps(Document doc) {
        Steps steps = new Steps();
        steps.setId(doc.getObjectId("_id").toString());
        steps.setUserId(doc.getString("userId"));
        steps.setDate(doc.getString("date"));
        steps.setSteps(doc.getInteger("steps"));
        
        // Handle distance - safely convert Integer or Double
        Object distanceObj = doc.get("distance");
        double distance = 0;
        if (distanceObj != null) {
            if (distanceObj instanceof Double) {
                distance = (Double) distanceObj;
            } else if (distanceObj instanceof Integer) {
                distance = ((Integer) distanceObj).doubleValue();
            } else if (distanceObj instanceof Number) {
                distance = ((Number) distanceObj).doubleValue();
            }
        }
        steps.setDistance(distance);
        
        // Handle calories - safely convert Integer or Double
        Object caloriesObj = doc.get("calories");
        double calories = 0;
        if (caloriesObj != null) {
            if (caloriesObj instanceof Double) {
                calories = (Double) caloriesObj;
            } else if (caloriesObj instanceof Integer) {
                calories = ((Integer) caloriesObj).doubleValue();
            } else if (caloriesObj instanceof Number) {
                calories = ((Number) caloriesObj).doubleValue();
            }
        }
        steps.setCalories(calories);
        
        return steps;
    }
}

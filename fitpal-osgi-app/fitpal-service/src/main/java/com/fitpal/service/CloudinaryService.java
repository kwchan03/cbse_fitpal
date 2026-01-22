package com.fitpal.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fitpal.service.db.MongoService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import java.util.HashMap;
import java.util.Map;

@Component(service = CloudinaryService.class, configurationPid = "com.fitpal.app")
public class CloudinaryService {
    private Cloudinary cloudinary;

    @Activate
    @Modified
    protected void activate(Map<String, Object> properties) {
        String cloudName = (String) properties.get("cloudinary.cloud-name");
        String apiKey = (String) properties.get("cloudinary.api-key");
        String apiSecret = (String) properties.get("cloudinary.api-secret");
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
        System.out.println("Cloudinary Service Initialized");
    }

    public String uploadImage(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) return null;

        try {
            Map params = ObjectUtils.asMap("resource_type", "auto");
            Map uploadResult = cloudinary.uploader().upload(imageBytes, params);
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            System.err.println("Cloudinary Upload Failed: " + e.getMessage());
            return null;
        }
    }
}

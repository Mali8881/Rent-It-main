package com.example.rent_it.Model;

public class Post {

    private String postId;
    private String image;
    private String description;
    private String publisher;
    private String title;
    private String email;
    private String price;
    private String location;
    private String bedrooms;
    private String bathrooms;
    private String area;
    private String type;
    private String amenities;
    private String rating;     // изменено с float на String
    private long timestamp;

    public Post() {}

    public Post(String postId, String image, String description, String publisher,
                String title, String email, String price, String location,
                String bedrooms, String bathrooms, String area, String type,
                String amenities, String rating, long timestamp) {
        this.postId = postId;
        this.image = image;
        this.description = description;
        this.publisher = publisher;
        this.title = title;
        this.email = email;
        this.price = price;
        this.location = location;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.area = area;
        this.type = type;
        this.amenities = amenities;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public Post(String postId, String image, String description, String publisher,
                String title, String email) {
        this.postId = postId;
        this.image = image;
        this.description = description;
        this.publisher = publisher;
        this.title = title;
        this.email = email;
        this.price = "0";
        this.location = "Не указано";
        this.bedrooms = "1";
        this.bathrooms = "1";
        this.area = "25";
        this.type = "Квартира";
        this.amenities = "Wi-Fi, Парковка";
        this.rating = "0.0";
        this.timestamp = System.currentTimeMillis();
    }

    // --- Getters and Setters ---

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getBedrooms() { return bedrooms; }
    public void setBedrooms(String bedrooms) { this.bedrooms = bedrooms; }

    public String getBathrooms() { return bathrooms; }
    public void setBathrooms(String bathrooms) { this.bathrooms = bathrooms; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

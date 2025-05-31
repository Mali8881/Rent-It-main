// Post.java — обновлённая модель с полями для фильтрации
package com.example.rent_it.Model;

public class Post {
    private long floor;
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
    private String buildingType;
    private String heating;         // Новое поле
    private String wifi;       // Новое поле
    private String amenities;

    private String rating;
    private long timestamp;

    public Post() {}

    public Post(String postId, String image, String description, String publisher, String title, String email, String price,
                String location, String bedrooms, String bathrooms, String area, String type, String buildingType,
                String heating,String wifi,  String amenities, String rating, long timestamp) {
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
        this.buildingType = buildingType;
        this.heating = heating;
        this.wifi = wifi;
        this.amenities = amenities;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public Post(String postId, String s, String s1, String s2, String s3, String s4, String number, String бишкек, String number1, String number2, String number3, String квартира, String многоэтажка, String центральное, boolean b, String s5, String s6, long l) {
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
    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getBuildingType() { return buildingType; }
    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }

    public String getHeating() { return heating; }
    public void setHeating(String heating) { this.heating = heating; }



    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }


    public long getFloor() { return floor; }

    public void setFloor(long floor) { this.floor = floor; }
}

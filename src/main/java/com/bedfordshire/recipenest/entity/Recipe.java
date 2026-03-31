package com.bedfordshire.recipenest.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "recipes")
public class Recipe {

    /** /
     * This is my chef recipe implementation which will be
     * tied to chefs and chefs photos via s3 storage XD
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false , length = 5000)
    private String instructions;

    @Column(nullable = false)
    private Integer cookingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false)
    private Integer servings;

    @Column(nullable = false)
    private String cuisineType;


    private String mainImage;

    private Integer viewCount = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)// Lazy loading
    @JoinColumn(name = "chef_id", nullable = false)
    private User chef;

    //Element Collection allows you to store simple values in a separate table
    // without creating a full entity
    @ElementCollection
    @CollectionTable(
            name = "recipe_ingredients", // Table name
            joinColumns = @JoinColumn(name = "recipe_id") // Foreign key to recipe
    )
    @Column(name = "ingredient", nullable = false) // Column for value
    private List<String> ingredients = new ArrayList<>();


    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecipePhoto> photos = new ArrayList<>();

    public Recipe(){}

    public Recipe(String title, String description,
                  String instructions,  User user){
        this.title = title;
        this.description = description;
        this.instructions = instructions;
        this.chef = user;
    }

    // Helper Methods
    public void addIngredient(String ingredients){
        this.ingredients.add(ingredients);
    }

    public void addPhoto(RecipePhoto photo){
        if(photo == null){
            throw new IllegalArgumentException("Photo cannot be null");
        }
        this.photos.add(photo);
        photo.setRecipe(this);
        if(this.photos.size() == 1)
        {
            photo.setPrimary(true);
            this.mainImage = photo.getImageUrl();
        }
    }

    public void removePhoto(RecipePhoto photo) {

        if(photo == null){
            return ;
        }
        this.photos.remove(photo);
        photo.setRecipe(null);

        // If removed photo was primary, set new primary
        if (photo.isPrimary() && !this.photos.isEmpty()) {
            RecipePhoto newPrimary = this.photos.get(0);
            newPrimary.setPrimary(true);
            this.mainImage = newPrimary.getImageUrl();
        } else if (this.photos.isEmpty()) {
            this.mainImage = null;
        }
    }


    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<RecipePhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<RecipePhoto> photos) {
        this.photos = photos;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public User getChef() {
        return chef;
    }

    public void setChef(User chef) {
        this.chef = chef;
    }

    public void incrementViewCount(){
        this.viewCount++;
    }


}

public class Ingredient {
    
    private Long id;
    private String name;
    private Double amount;
    private String unit; // e.g., "cups", "grams" 
    private String originalString;
        
    public Ingredient(Long id, String name, Double amount, String unit, String originalString) {
            this.id = id;
            this.name = name;
            this.amount = amount;
            this.unit = unit;
            this.originalString = originalString;
        }

    // Getters 和 Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getOriginalString() { return originalString; }
    public void setOriginalString(String originalString) { this.originalString = originalString; }

    @Override
    public String toString() {
        return originalString;
    }
}

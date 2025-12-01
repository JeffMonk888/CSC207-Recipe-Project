package domain.entity;
import org.json.JSONObject;

public class Ingredient {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AMOUNT = "amount";
    public static final String UNIT = "unit";
    public static final String ORIGINAL_STRING = "originalString";
    
    private Long id;
    private String name;
    private Double amount;
    private String unit;
    private String originalString;
        
    public Ingredient(Long id, String name, Double amount, String unit, String originalString) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.originalString = originalString;
    }

    public Ingredient(JSONObject jsonObject) {
        this.id = null;
        this.name = "";
        this.amount = null;
        this.unit = "";
        this.originalString = "";

        if (jsonObject.has(ID)) {
            this.id = jsonObject.getLong(ID);
        }

        if (jsonObject.has(NAME)) {
            this.name = jsonObject.getString(NAME);
        }

        if (jsonObject.has(AMOUNT)) {
            this.amount = jsonObject.getDouble(AMOUNT);
        }

        if (jsonObject.has(UNIT)) {
            this.unit = jsonObject.getString(UNIT);
        }

        if (jsonObject.has(ORIGINAL_STRING)) {
            this.originalString = jsonObject.getString(ORIGINAL_STRING);
        }
    }

    /**
     * Converts the Ingredients into a JSON file.
     *
     * @return JSONObject that can be stored in a JSON file
     */
    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, this.id);
        jsonObject.put(NAME, this.name);
        jsonObject.put(AMOUNT, this.amount);
        jsonObject.put(UNIT, this.unit);
        jsonObject.put(ORIGINAL_STRING, this.originalString);
        return jsonObject;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getOriginalString() {
        return originalString;
    }

    public void setOriginalString(String originalString) {
        this.originalString = originalString;
    }

    @Override
    public String toString() {
        return originalString;
    }
}

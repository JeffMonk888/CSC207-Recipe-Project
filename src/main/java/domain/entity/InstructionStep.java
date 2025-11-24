package domain.entity;
import org.json.JSONObject;

public class InstructionStep {

    private Long id;
    private Integer stepNumber;
    private String description;

    public InstructionStep(Long id, Integer stepNumber, String description) {
        this.id = id;
        this.stepNumber = stepNumber;
        this.description = description;
    }

    // Situation for getting recipe from JSON
    public InstructionStep(JSONObject jsonObject) {
        this.id = jsonObject.has("id") ? jsonObject.getLong("id") : null;
        this.stepNumber = jsonObject.has("stepNumber") ? jsonObject.getInt("stepNumber") : null;
        this.description = jsonObject.has("description") ? jsonObject.getString("description") : ""; // 描述不应为 null
    }

    // create a JSON for recipe
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("stepNumber", stepNumber);
        jsonObject.put("description", description);
        return jsonObject;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getStepNumber() { return stepNumber; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return stepNumber + ". " + description;
    }
}

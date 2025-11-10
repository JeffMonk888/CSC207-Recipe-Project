package domain.entity;

public class InstructionStep {

    private Long id;
    private Integer stepNumber;
    private String description;

    public InstructionStep(Long id, Integer stepNumber, String description) {
        this.id = id;
        this.stepNumber = stepNumber;
        this.description = description;
    }

    // Getters 和 Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getStepNumber() { return stepNumber; }
    public void setStepNumber(Integer stepNumber) { this.stepNumber = stepNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return stepNumber + ". " + description;
    }
}

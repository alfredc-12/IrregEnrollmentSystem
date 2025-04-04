package GettersSetters;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class Guardian {
    private final StringProperty fullName;
    private final StringProperty contactNo;
    private final StringProperty relationship;

    // Constructor
    public Guardian(String fullName, String contactNo, String relationship) {
        this.fullName = new SimpleStringProperty(fullName);
        this.contactNo = new SimpleStringProperty(contactNo);
        this.relationship = new SimpleStringProperty(relationship);
    }

    // Getters and setters
    public String getFullName() {
        return fullName.get();
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public String getContactNo() {
        return contactNo.get();
    }

    public StringProperty contactNoProperty() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo.set(contactNo);
    }

    public String getRelationship() {
        return relationship.get();
    }

    public StringProperty relationshipProperty() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship.set(relationship);
    }
}

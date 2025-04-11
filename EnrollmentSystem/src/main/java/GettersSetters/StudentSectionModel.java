package GettersSetters;

import javafx.beans.property.*;

public class StudentSectionModel {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty sex;
    private final StringProperty srCode;
    private final StringProperty section;
    private final StringProperty yearLevel;

    public StudentSectionModel(int id, String lastName, String firstName, String middleName,
                               String sex, String srCode, String section, String yearLevel) {
        this.id = new SimpleIntegerProperty(id);

        // Format the name as "lastName, firstName middleInitial"
        String middleInitial = (middleName != null && !middleName.isEmpty()) ?
                middleName.charAt(0) + "." : "";
        String formattedName = lastName + ", " + firstName + " " + middleInitial;

        this.name = new SimpleStringProperty(formattedName);
        this.sex = new SimpleStringProperty(sex);
        this.srCode = new SimpleStringProperty(srCode);
        this.section = new SimpleStringProperty(section);
        this.yearLevel = new SimpleStringProperty(yearLevel);
    }

    // Getters
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getSex() { return sex.get(); }
    public String getSrCode() { return srCode.get(); }
    public String getSection() { return section.get(); }
    public String getYearLevel() { return yearLevel.get(); }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty sexProperty() { return sex; }
    public StringProperty srCodeProperty() { return srCode; }
    public StringProperty sectionProperty() { return section; }
    public StringProperty yearLevelProperty() { return yearLevel; }
}
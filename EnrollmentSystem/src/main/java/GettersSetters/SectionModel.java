package GettersSetters;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SectionModel {
    private final StringProperty section;
    private final StringProperty department;

    public SectionModel(String section, String department) {
        this.section = new SimpleStringProperty(section);
        this.department = new SimpleStringProperty(department);
    }

    public String getSection() {
        return section.get();
    }

    public void setSection(String section) {
        this.section.set(section);
    }

    public StringProperty sectionProperty() {
        return section;
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public StringProperty departmentProperty() {
        return department;
    }
}
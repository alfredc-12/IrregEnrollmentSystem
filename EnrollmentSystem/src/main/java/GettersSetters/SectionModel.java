package GettersSetters;

public class SectionModel {
    private String section;
    private String department;

    public SectionModel(String section, String department) {
        this.section = section;
        this.department = department;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

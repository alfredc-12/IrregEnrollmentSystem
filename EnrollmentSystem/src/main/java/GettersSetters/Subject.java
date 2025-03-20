package GettersSetters;

public class Subject {
    private int id;
    private String subjectName;
    private String code;
    private String department;

    public Subject(int id, String subjectName, String code, String department) {
        this.id = id;
        this.subjectName = subjectName;
        this.code = code;
        this.department = department;
    }

    // Getters
    public int getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public String getCode() { return code; }
    public String getDepartment() { return department; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public void setCode(String code) { this.code = code; }
    public void setDepartment(String department) { this.department = department; }
}

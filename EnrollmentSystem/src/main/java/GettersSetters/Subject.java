package GettersSetters;

public class Subject {
    private int id;
    private String subjectName;
    private int creditHours;
    private boolean isMajor;
    private String preferredRoom;

    public Subject(int id, String subjectName, int creditHours, boolean isMajor, String preferredRoom) {
        this.id = id;
        this.subjectName = subjectName;
        this.creditHours = creditHours;
        this.isMajor = isMajor;
        this.preferredRoom = preferredRoom;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public boolean getIsMajor() { // Updated to follow JavaBeans convention
        return isMajor;
    }

    public String getPreferredRoom() {
        return preferredRoom;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public void setIsMajor(boolean isMajor) { // Updated to match getter
        this.isMajor = isMajor;
    }

    public void setPreferredRoom(String preferredRoom) {
        this.preferredRoom = preferredRoom;
    }
}

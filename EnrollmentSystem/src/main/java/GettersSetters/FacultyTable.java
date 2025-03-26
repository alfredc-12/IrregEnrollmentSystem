package GettersSetters;

public class FacultyTable {
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String role;
    private String contactNo;
    private String personalEmail;
    private String bsuEmail;
    private String password;
    private String picLink;
    private String signLink;
    private int maxSubjects;

    // Constructor
    public FacultyTable(int id, String firstName, String middleName, String lastName,
                        String role, String contactNo, String personalEmail, String bsuEmail,
                        String password, String picLink, String signLink, int maxSubjects) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.role = role;
        this.contactNo = contactNo;
        this.personalEmail = personalEmail;
        this.bsuEmail = bsuEmail;
        this.password = password;
        this.picLink = picLink;
        this.signLink = signLink;
        this.maxSubjects = maxSubjects;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRole() {
        return role;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getBsuEmail() {
        return bsuEmail;
    }

    public String getPassword() {
        return password;
    }

    public String getPicLink() {
        return picLink;
    }

    public String getSignLink() {
        return signLink;
    }

    public int getMaxSubjects() {
        return maxSubjects;
    }

    // Computed property for Full Name
    public String getFullname() {
        StringBuilder sb = new StringBuilder();
        sb.append(firstName);
        if (middleName != null && !middleName.isEmpty()) {
            sb.append(" ").append(middleName);
        }
        sb.append(" ").append(lastName);
        return sb.toString();
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public void setBsuEmail(String bsuEmail) {
        this.bsuEmail = bsuEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPicLink(String picLink) {
        this.picLink = picLink;
    }

    public void setSignLink(String signLink) {
        this.signLink = signLink;
    }

    public void setMaxSubjects(int maxSubjects) {
        this.maxSubjects = maxSubjects;
    }
}

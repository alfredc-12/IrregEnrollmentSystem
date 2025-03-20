package GettersSetters;

public class FacultyTable {
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String role;
    private String email;
    private String picLink;
    private String signLink;

    public FacultyTable(int id, String firstName, String middleName, String lastName,
                        String role, String email, String picLink, String signLink) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.role = role;
        this.email = email;
        this.picLink = picLink;
        this.signLink = signLink;
    }

    // Getters (and optionally setters)
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPicLink() { return picLink; }
    public String getSignLink() { return signLink; }
}

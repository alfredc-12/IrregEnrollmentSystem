package GettersSetters;

import javafx.beans.property.*;

public class Student {
    private final SimpleIntegerProperty studID;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty middleName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty srCode;
    private final SimpleStringProperty yearLevel;
    private final SimpleStringProperty program;
    private final SimpleStringProperty major;
    private final SimpleStringProperty contact;
    private final SimpleStringProperty email;
    private final SimpleStringProperty address;
    private final SimpleStringProperty status;

    public Student(int studID, String firstName, String middleName, String lastName, String srCode,
                   String yearLevel, String program, String major, String contact, String email,
                   String address, String status) {
        this.studID = new SimpleIntegerProperty(studID);
        this.firstName = new SimpleStringProperty(firstName);
        this.middleName = new SimpleStringProperty(middleName);
        this.lastName = new SimpleStringProperty(lastName);
        this.srCode = new SimpleStringProperty(srCode);
        this.yearLevel = new SimpleStringProperty(yearLevel);
        this.program = new SimpleStringProperty(program);
        this.major = new SimpleStringProperty(major);
        this.contact = new SimpleStringProperty(contact);
        this.email = new SimpleStringProperty(email);
        this.address = new SimpleStringProperty(address);
        this.status = new SimpleStringProperty(status);
    }

    // Getter methods for TableView
    public SimpleIntegerProperty studIDProperty() { return studID; }
    public SimpleStringProperty firstNameProperty() { return firstName; }
    public SimpleStringProperty middleNameProperty() { return middleName; }
    public SimpleStringProperty lastNameProperty() { return lastName; }
    public SimpleStringProperty srCodeProperty() { return srCode; }
    public SimpleStringProperty yearLevelProperty() { return yearLevel; }
    public SimpleStringProperty programProperty() { return program; }
    public SimpleStringProperty majorProperty() { return major; }
    public SimpleStringProperty contactProperty() { return contact; }
    public SimpleStringProperty emailProperty() { return email; }
    public SimpleStringProperty addressProperty() { return address; }
    public SimpleStringProperty statusProperty() { return status; }


    public int getStudID() { return studID.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getMiddleName() { return middleName.get(); }
    public String getLastName() { return lastName.get(); }
    public String getSrCode() { return srCode.get(); }
    public String getYearLevel() { return yearLevel.get(); }
    public String getProgram() { return program.get(); }
    public String getMajor() { return major.get(); }
    public String getContact() { return contact.get(); }
    public String getEmail() { return email.get(); }
    public String getAddress() { return address.get(); }
    public String getStatus() { return status.get(); }

}


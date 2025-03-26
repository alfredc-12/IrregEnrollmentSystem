package GettersSetters;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ScheduleModel {
    private final SimpleIntegerProperty schedId;
    private final SimpleStringProperty subject;
    private final SimpleStringProperty day;
    private final SimpleStringProperty timeIn;
    private final SimpleStringProperty timeOut;
    private final SimpleStringProperty room;
    private final SimpleStringProperty faculty;
    private final SimpleStringProperty section;

    public ScheduleModel(int schedId, String subject, String day, String timeIn, String timeOut, String room, String faculty, String section) {
        this.schedId = new SimpleIntegerProperty(schedId);
        this.subject = new SimpleStringProperty(subject);
        this.day = new SimpleStringProperty(day);
        this.timeIn = new SimpleStringProperty(timeIn);
        this.timeOut = new SimpleStringProperty(timeOut);
        this.room = new SimpleStringProperty(room);
        this.faculty = new SimpleStringProperty(faculty);
        this.section = new SimpleStringProperty(section);
    }

    public int getSchedId() {
        return schedId.get();
    }

    public SimpleIntegerProperty schedIdProperty() {
        return schedId;
    }

    public String getSubject() {
        return subject.get();
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public String getDay() {
        return day.get();
    }

    public SimpleStringProperty dayProperty() {
        return day;
    }

    public String getTimeIn() {
        return timeIn.get();
    }

    public SimpleStringProperty timeInProperty() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut.get();
    }

    public SimpleStringProperty timeOutProperty() {
        return timeOut;
    }

    public String getRoom() {
        return room.get();
    }

    public SimpleStringProperty roomProperty() {
        return room;
    }

    public String getFaculty() {
        return faculty.get();
    }

    public SimpleStringProperty facultyProperty() {
        return faculty;
    }

    public String getSection() {
        return section.get();
    }

    public SimpleStringProperty sectionProperty() {
        return section;
    }
}
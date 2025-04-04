package GettersSetters;

public class Subject {
    private int subId;
    private String subjCode;
    private String subjectName;
    private Integer lecture;
    private int units;
    private Integer lab;
    private String yearLevel;
    private String semester;
    private String prerequisite;
    private String acadTrack;

    public Subject() {
    }

    public int getSubId() {
        return subId;
    }

    public void setSubId(int subId) {
        this.subId = subId;
    }

    public String getSubjCode() {
        return subjCode;
    }

    public void setSubjCode(String subjCode) {
        this.subjCode = subjCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getLecture() {
        return lecture;
    }

    public void setLecture(Integer lecture) {
        this.lecture = lecture;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public Integer getLab() {
        return lab;
    }

    public void setLab(Integer lab) {
        this.lab = lab;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }

    public String getAcadTrack() {
        return acadTrack;
    }

    public void setAcadTrack(String acadTrack) {
        this.acadTrack = acadTrack;
    }
}
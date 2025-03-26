package ExtraSources;

import com.google.ortools.Loader;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ORToolsScheduler {

    static {
        try {
            Loader.loadNativeLibraries();
        } catch (Exception e) {
            System.err.println("Failed to load OR‑Tools native libraries: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static class ScheduleItem {
        public int sectionId;
        public String day;
        public String timeIn;
        public String timeOut;

        public ScheduleItem(int sectionId, String day, String timeIn, String timeOut) {
            this.sectionId = sectionId;
            this.day = day;
            this.timeIn = timeIn;
            this.timeOut = timeOut;
        }
    }

    // Existing method for single session scheduling remains unchanged.
    public ObservableList<ScheduleItem> generateSchedule(int durationMinutes, ObservableList<Integer> sectionIds) {
        ObservableList<ScheduleItem> scheduleItems = FXCollections.observableArrayList();
        int numSections = sectionIds.size();
        int startTime = 8 * 60;
        int endTime = 16 * 60;
        int latestStart = endTime - durationMinutes;

        CpModel model = new CpModel();
        IntVar[] dayVars = new IntVar[numSections];
        IntVar[] startVars = new IntVar[numSections];

        for (int i = 0; i < numSections; i++) {
            dayVars[i] = model.newIntVar(0, 4, "day_" + i);
            startVars[i] = model.newIntVar(startTime, latestStart, "start_" + i);
        }

        for (int i = 0; i < numSections; i++) {
            for (int j = i + 1; j < numSections; j++) {
                BoolVar sameDay = model.newBoolVar("sameDay_" + i + "_" + j);
                model.addEquality(dayVars[i], dayVars[j]).onlyEnforceIf(sameDay);
                model.addDifferent(dayVars[i], dayVars[j]).onlyEnforceIf(sameDay.not());

                BoolVar iBeforeJ = model.newBoolVar("iBeforeJ_" + i + "_" + j);
                BoolVar jBeforeI = model.newBoolVar("jBeforeI_" + i + "_" + j);
                model.addLessOrEqual(LinearExpr.sum(new IntVar[]{startVars[i], model.newConstant(durationMinutes)}), startVars[j])
                        .onlyEnforceIf(iBeforeJ);
                model.addLessOrEqual(LinearExpr.sum(new IntVar[]{startVars[j], model.newConstant(durationMinutes)}), startVars[i])
                        .onlyEnforceIf(jBeforeI);
                model.addBoolOr(new BoolVar[]{iBeforeJ, jBeforeI}).onlyEnforceIf(sameDay);
            }
        }

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);
        if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
            return scheduleItems;
        }

        String[] dayMapping = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < numSections; i++) {
            int assignedDay = (int) solver.value(dayVars[i]);
            int assignedStart = (int) solver.value(startVars[i]);
            int assignedEnd = assignedStart + durationMinutes;
            String dayStr = dayMapping[assignedDay];
            String timeIn = String.format("%02d:%02d:00", assignedStart / 60, assignedStart % 60);
            String timeOut = String.format("%02d:%02d:00", assignedEnd / 60, assignedEnd % 60);
            scheduleItems.add(new ScheduleItem(sectionIds.get(i), dayStr, timeIn, timeOut));
        }

        return scheduleItems;
    }

    // New method for dual scheduling ensuring lecture and lab are on different days.
    public DualSchedule generateDualSchedule(int durationMinutes, ObservableList<Integer> sectionIds) {
        int numSections = sectionIds.size();
        int startTime = 8 * 60;
        int endTime = 16 * 60;
        int latestStart = endTime - durationMinutes;

        CpModel model = new CpModel();
        // Variables for lecture sessions.
        IntVar[] lectureDayVars = new IntVar[numSections];
        IntVar[] lectureStartVars = new IntVar[numSections];
        // Variables for lab sessions.
        IntVar[] labDayVars = new IntVar[numSections];
        IntVar[] labStartVars = new IntVar[numSections];

        for (int i = 0; i < numSections; i++) {
            lectureDayVars[i] = model.newIntVar(0, 4, "lecture_day_" + i);
            lectureStartVars[i] = model.newIntVar(startTime, latestStart, "lecture_start_" + i);
            labDayVars[i] = model.newIntVar(0, 4, "lab_day_" + i);
            labStartVars[i] = model.newIntVar(startTime, latestStart, "lab_start_" + i);
            // Ensure lecture and lab are on different days.
            model.addDifferent(lectureDayVars[i], labDayVars[i]);
        }

        // Add no overlap constraints within lecture sessions.
        for (int i = 0; i < numSections; i++) {
            for (int j = i + 1; j < numSections; j++) {
                // Lecture constraints.
                BoolVar lectureSameDay = model.newBoolVar("lecture_sameDay_" + i + "_" + j);
                model.addEquality(lectureDayVars[i], lectureDayVars[j]).onlyEnforceIf(lectureSameDay);
                model.addDifferent(lectureDayVars[i], lectureDayVars[j]).onlyEnforceIf(lectureSameDay.not());
                BoolVar lecIBeforeJ = model.newBoolVar("lecIBeforeJ_" + i + "_" + j);
                BoolVar lecJBeforeI = model.newBoolVar("lecJBeforeI_" + i + "_" + j);
                model.addLessOrEqual(LinearExpr.sum(new IntVar[]{lectureStartVars[i], model.newConstant(durationMinutes)}), lectureStartVars[j])
                        .onlyEnforceIf(lecIBeforeJ);
                model.addLessOrEqual(LinearExpr.sum(new IntVar[]{lectureStartVars[j], model.newConstant(durationMinutes)}), lectureStartVars[i])
                        .onlyEnforceIf(lecJBeforeI);
                model.addBoolOr(new BoolVar[]{lecIBeforeJ, lecJBeforeI}).onlyEnforceIf(lectureSameDay);

                // Lab constraints.
                BoolVar labSameDay = model.newBoolVar("lab_sameDay_" + i + "_" + j);
                model.addEquality(labDayVars[i], labDayVars[j]).onlyEnforceIf(labSameDay);
                model.addDifferent(labDayVars[i], labDayVars[j]).onlyEnforceIf(labSameDay.not());
                BoolVar labIBeforeJ = model.newBoolVar("labIBeforeJ_" + i + "_" + j);
                BoolVar labJBeforeI = model.newBoolVar("labJBeforeI_" + i + "_" + j);
                model.addLessOrEqual(LinearExpr.sum(new IntVar[]{labStartVars[i], model.newConstant(durationMinutes)}), labStartVars[j])
                        .onlyEnforceIf(labIBeforeJ);
                model.addLessOrEqual(LinearExpr.sum(new IntVar[]{labStartVars[j], model.newConstant(durationMinutes)}), labStartVars[i])
                        .onlyEnforceIf(labJBeforeI);
                model.addBoolOr(new BoolVar[]{labIBeforeJ, labJBeforeI}).onlyEnforceIf(labSameDay);
            }
        }

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);
        if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
            return null;
        }

        String[] dayMapping = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        ObservableList<ScheduleItem> lectureSchedule = FXCollections.observableArrayList();
        ObservableList<ScheduleItem> labSchedule = FXCollections.observableArrayList();

        for (int i = 0; i < numSections; i++) {
            int lecDay = (int) solver.value(lectureDayVars[i]);
            int lecStart = (int) solver.value(lectureStartVars[i]);
            int lecEnd = lecStart + durationMinutes;
            String lecDayStr = dayMapping[lecDay];
            String lecTimeIn = String.format("%02d:%02d:00", lecStart / 60, lecStart % 60);
            String lecTimeOut = String.format("%02d:%02d:00", lecEnd / 60, lecEnd % 60);
            lectureSchedule.add(new ScheduleItem(sectionIds.get(i), lecDayStr, lecTimeIn, lecTimeOut));

            int labDay = (int) solver.value(labDayVars[i]);
            int labStart = (int) solver.value(labStartVars[i]);
            int labEnd = labStart + durationMinutes;
            String labDayStr = dayMapping[labDay];
            String labTimeIn = String.format("%02d:%02d:00", labStart / 60, labStart % 60);
            String labTimeOut = String.format("%02d:%02d:00", labEnd / 60, labEnd % 60);
            labSchedule.add(new ScheduleItem(sectionIds.get(i), labDayStr, labTimeIn, labTimeOut));
        }

        return new DualSchedule(lectureSchedule, labSchedule);
    }

    // Helper class to hold dual schedule results.
    public static class DualSchedule {
        public final ObservableList<ScheduleItem> lectureSchedule;
        public final ObservableList<ScheduleItem> labSchedule;

        public DualSchedule(ObservableList<ScheduleItem> lectureSchedule, ObservableList<ScheduleItem> labSchedule) {
            this.lectureSchedule = lectureSchedule;
            this.labSchedule = labSchedule;
        }
    }
}
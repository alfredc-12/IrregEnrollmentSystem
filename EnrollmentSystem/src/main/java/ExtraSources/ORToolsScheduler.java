package ExtraSources;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class ORToolsScheduler {

    static {
        try {
            Loader.loadNativeLibraries();
        } catch (Exception e) {
            System.err.println("Failed to load OR-Tools native libraries: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static class TimeSlot {
        public String day;
        public LocalTime startTime;
        public LocalTime endTime;
        public int roomId;  // Added room ID for conflict detection

        public TimeSlot(String day, LocalTime startTime, LocalTime endTime) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.roomId = -1;
        }

        public TimeSlot(String day, LocalTime startTime, LocalTime endTime, int roomId) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.roomId = roomId;
        }
    }

    public static class ScheduleSubject {
        public int subjectId;
        public int lectureDuration; // in minutes
        public int labDuration;     // in minutes
        public boolean hasLab;

        public ScheduleSubject(int subjectId, int lectureDuration, int labDuration) {
            this.subjectId = subjectId;
            this.lectureDuration = lectureDuration;
            this.labDuration = labDuration;
            this.hasLab = labDuration > 0;
        }
    }

    // Generate time slots appropriate for different durations
    public List<TimeSlot> generateTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        // Create time slots for each day with different durations
        for (String day : days) {
            // Morning slots - 1.5 hour slots
            slots.add(new TimeSlot(day, LocalTime.of(8, 0), LocalTime.of(9, 30)));
            slots.add(new TimeSlot(day, LocalTime.of(9, 30), LocalTime.of(11, 0)));
            slots.add(new TimeSlot(day, LocalTime.of(11, 0), LocalTime.of(12, 30)));

            // Morning slots - 2 hour slots
            slots.add(new TimeSlot(day, LocalTime.of(8, 0), LocalTime.of(10, 0)));
            slots.add(new TimeSlot(day, LocalTime.of(10, 0), LocalTime.of(12, 0)));

            // Morning slots - 3 hour slots
            slots.add(new TimeSlot(day, LocalTime.of(8, 0), LocalTime.of(11, 0)));
            slots.add(new TimeSlot(day, LocalTime.of(9, 0), LocalTime.of(12, 0)));

            // Afternoon slots - 1.5 hour slots
            slots.add(new TimeSlot(day, LocalTime.of(13, 0), LocalTime.of(14, 30)));
            slots.add(new TimeSlot(day, LocalTime.of(14, 30), LocalTime.of(16, 0)));
            slots.add(new TimeSlot(day, LocalTime.of(16, 0), LocalTime.of(17, 30)));

            // Afternoon slots - 2 hour slots
            slots.add(new TimeSlot(day, LocalTime.of(13, 0), LocalTime.of(15, 0)));
            slots.add(new TimeSlot(day, LocalTime.of(15, 0), LocalTime.of(17, 0)));

            // Afternoon slots - 3 hour slots
            slots.add(new TimeSlot(day, LocalTime.of(13, 0), LocalTime.of(16, 0)));
            slots.add(new TimeSlot(day, LocalTime.of(14, 0), LocalTime.of(17, 0)));
        }

        return slots;
    }

    public Map<Integer, List<TimeSlot>> generateOptimizedSchedule(
            List<ScheduleSubject> subjects,
            List<TimeSlot> existingSchedules,
            int lectureRoomId,
            int labRoomId) {

        CpModel model = new CpModel();

        // Define time slots
        List<TimeSlot> availableSlots = generateTimeSlots();
        int numSlots = availableSlots.size();

        // Create variables for each subject and slot
        Map<Integer, List<BoolVar>> lectureAssignments = new HashMap<>();
        Map<Integer, List<BoolVar>> labAssignments = new HashMap<>();

        // For each subject, create boolean variables for each slot
        for (ScheduleSubject subject : subjects) {
            List<BoolVar> lectureSlotVars = new ArrayList<>();
            List<BoolVar> labSlotVars = new ArrayList<>();

            for (int i = 0; i < numSlots; i++) {
                // Only create variables for slots with appropriate duration
                TimeSlot slot = availableSlots.get(i);
                int slotDuration = (int) slot.startTime.until(slot.endTime, java.time.temporal.ChronoUnit.MINUTES);

                // For lecture slots, check if duration matches
                if (slotDuration >= subject.lectureDuration && slotDuration <= subject.lectureDuration + 30) {
                    lectureSlotVars.add(model.newBoolVar("subject_" + subject.subjectId + "_lecture_slot_" + i));
                } else {
                    // Add a dummy variable that's always 0
                    BoolVar dummy = model.newBoolVar("subject_" + subject.subjectId + "_lecture_dummy_" + i);
                    model.addEquality(dummy, 0);
                    lectureSlotVars.add(dummy);
                }

                // For lab slots, check if duration matches
                if (subject.hasLab) {
                    if (slotDuration >= subject.labDuration && slotDuration <= subject.labDuration + 30) {
                        labSlotVars.add(model.newBoolVar("subject_" + subject.subjectId + "_lab_slot_" + i));
                    } else {
                        // Add a dummy variable that's always 0
                        BoolVar dummy = model.newBoolVar("subject_" + subject.subjectId + "_lab_dummy_" + i);
                        model.addEquality(dummy, 0);
                        labSlotVars.add(dummy);
                    }
                }
            }

            lectureAssignments.put(subject.subjectId, lectureSlotVars);
            if (subject.hasLab) {
                labAssignments.put(subject.subjectId, labSlotVars);
            }
        }

        // Constraint 1: Each lecture is assigned to exactly one slot
        for (ScheduleSubject subject : subjects) {
            List<BoolVar> lectureVars = lectureAssignments.get(subject.subjectId);
            model.addExactlyOne(lectureVars.toArray(new BoolVar[0]));

            // If subject has lab, it must be assigned exactly one slot
            if (subject.hasLab) {
                List<BoolVar> labVars = labAssignments.get(subject.subjectId);
                model.addExactlyOne(labVars.toArray(new BoolVar[0]));
            }
        }

        // Constraint 2: No overlap for slots in the same room
        Map<String, Map<Integer, List<BoolVar>>> roomDaySlotToVars = new HashMap<>();

        // Initialize for lecture and lab rooms
        roomDaySlotToVars.put("lecture", new HashMap<>());
        roomDaySlotToVars.put("lab", new HashMap<>());

        // Populate the map
        for (int slotIndex = 0; slotIndex < numSlots; slotIndex++) {
            TimeSlot slot = availableSlots.get(slotIndex);

            // For lecture room
            roomDaySlotToVars.get("lecture").computeIfAbsent(slotIndex, k -> new ArrayList<>());

            // For lab room
            roomDaySlotToVars.get("lab").computeIfAbsent(slotIndex, k -> new ArrayList<>());

            for (ScheduleSubject subject : subjects) {
                // Add lecture assignments for this slot to the lecture room
                roomDaySlotToVars.get("lecture").get(slotIndex).add(lectureAssignments.get(subject.subjectId).get(slotIndex));

                // Add lab assignments for this slot to the lab room if subject has lab
                if (subject.hasLab) {
                    roomDaySlotToVars.get("lab").get(slotIndex).add(labAssignments.get(subject.subjectId).get(slotIndex));
                }
            }
        }

        // Add constraints to ensure no more than one assignment per room per time slot
        for (Map.Entry<String, Map<Integer, List<BoolVar>>> roomEntry : roomDaySlotToVars.entrySet()) {
            for (Map.Entry<Integer, List<BoolVar>> slotEntry : roomEntry.getValue().entrySet()) {
                List<BoolVar> varsInSlot = slotEntry.getValue();
                if (varsInSlot.size() > 1) {
                    model.addAtMostOne(varsInSlot.toArray(new BoolVar[0]));
                }
            }
        }

        // Constraint 3: For subjects with both lecture and lab, they must be on different days
        for (ScheduleSubject subject : subjects) {
            if (subject.hasLab) {
                Map<String, List<BoolVar>> lectureSlotsByDay = new HashMap<>();
                Map<String, List<BoolVar>> labSlotsByDay = new HashMap<>();

                // Group slots by day
                for (int i = 0; i < numSlots; i++) {
                    String day = availableSlots.get(i).day;

                    lectureSlotsByDay.computeIfAbsent(day, k -> new ArrayList<>())
                            .add(lectureAssignments.get(subject.subjectId).get(i));

                    labSlotsByDay.computeIfAbsent(day, k -> new ArrayList<>())
                            .add(labAssignments.get(subject.subjectId).get(i));
                }

                // For each day, ensure lecture and lab are not on the same day
                for (String day : lectureSlotsByDay.keySet()) {
                    List<BoolVar> lectureSlotsForDay = lectureSlotsByDay.get(day);
                    List<BoolVar> labSlotsForDay = labSlotsByDay.get(day);

                    BoolVar lectureOnDay = model.newBoolVar("subject_" + subject.subjectId + "_lecture_on_" + day);
                    BoolVar labOnDay = model.newBoolVar("subject_" + subject.subjectId + "_lab_on_" + day);

                    // lectureOnDay is true if any lecture slot for this day is assigned
                    model.addMaxEquality(lectureOnDay, lectureSlotsForDay.toArray(new BoolVar[0]));
                    // labOnDay is true if any lab slot for this day is assigned
                    model.addMaxEquality(labOnDay, labSlotsForDay.toArray(new BoolVar[0]));

                    // Both cannot be true at the same time
                    model.addBoolOr(new Literal[]{lectureOnDay.not(), labOnDay.not()});
                }
            }
        }

        // Constraint 4: No conflicts with existing schedules
        if (existingSchedules != null && !existingSchedules.isEmpty()) {
            // Create a map to group existing schedules by day and time
            Map<String, List<TimeSlot>> existingSchedulesByDay = new HashMap<>();
            for (TimeSlot existingSlot : existingSchedules) {
                existingSchedulesByDay.computeIfAbsent(existingSlot.day, k -> new ArrayList<>()).add(existingSlot);
            }

            for (int i = 0; i < numSlots; i++) {
                TimeSlot slot = availableSlots.get(i);
                List<TimeSlot> slotsOnSameDay = existingSchedulesByDay.get(slot.day);

                if (slotsOnSameDay != null) {
                    for (TimeSlot existingSlot : slotsOnSameDay) {
                        // Check for time overlap
                        if (!(slot.endTime.isBefore(existingSlot.startTime) || slot.startTime.isAfter(existingSlot.endTime))) {
                            // If there's time overlap, check for room conflict
                            if (existingSlot.roomId == lectureRoomId) {
                                for (ScheduleSubject subject : subjects) {
                                    // Prevent scheduling in this slot due to room conflict
                                    model.addEquality(lectureAssignments.get(subject.subjectId).get(i), 0);
                                }
                            }
                            if (existingSlot.roomId == labRoomId) {
                                for (ScheduleSubject subject : subjects) {
                                    if (subject.hasLab) {
                                        model.addEquality(labAssignments.get(subject.subjectId).get(i), 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Constraint 5: Same subject for different sections must be on different days
        Map<Integer, Set<String>> subjectDays = new HashMap<>();

// First, query existing schedules in the database
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            for (ScheduleSubject subject : subjects) {
                // Get all days where this subject is already scheduled
                String query = "SELECT DISTINCT days FROM subsched WHERE subject_id = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, subject.subjectId);
                ResultSet rs = ps.executeQuery();

                Set<String> daysForSubject = new HashSet<>();
                while (rs.next()) {
                    daysForSubject.add(rs.getString("days"));
                }

                if (!daysForSubject.isEmpty()) {
                    subjectDays.put(subject.subjectId, daysForSubject);
                }

                rs.close();
                ps.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error querying existing subject days: " + e.getMessage());
        }

// Add constraints to prevent scheduling on days where the subject is already scheduled
        for (ScheduleSubject subject : subjects) {
            Set<String> existingDays = subjectDays.get(subject.subjectId);
            if (existingDays != null && !existingDays.isEmpty()) {
                for (int i = 0; i < numSlots; i++) {
                    TimeSlot slot = availableSlots.get(i);
                    if (existingDays.contains(slot.day)) {
                        // Cannot schedule this subject on this day - add hard constraint
                        model.addEquality(lectureAssignments.get(subject.subjectId).get(i), 0);

                        if (subject.hasLab) {
                            model.addEquality(labAssignments.get(subject.subjectId).get(i), 0);
                        }
                    }
                }
            }
        }

// Now add day tracking variables to ensure each subject is on exactly one day
        Map<Integer, Map<String, BoolVar>> subjectDayVars = new HashMap<>();
        for (ScheduleSubject subject : subjects) {
            Map<String, BoolVar> dayVars = new HashMap<>();
            for (String day : Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")) {
                dayVars.put(day, model.newBoolVar("subject_" + subject.subjectId + "_on_day_" + day));
            }
            subjectDayVars.put(subject.subjectId, dayVars);

            // Link day variables with slot assignments
            for (String day : dayVars.keySet()) {
                List<BoolVar> daySlotVars = new ArrayList<>();
                for (int i = 0; i < numSlots; i++) {
                    if (availableSlots.get(i).day.equals(day)) {
                        daySlotVars.add(lectureAssignments.get(subject.subjectId).get(i));
                    }
                }

                if (!daySlotVars.isEmpty()) {
                    // Day variable is true if any slot on this day is used
                    model.addMaxEquality(dayVars.get(day), daySlotVars.toArray(new BoolVar[0]));
                }
            }

            // Force exactly one day assignment per subject
            List<BoolVar> allDayVars = new ArrayList<>(dayVars.values());
            model.addExactlyOne(allDayVars.toArray(new BoolVar[0]));
        }

        // Add constraints to prevent scheduling on days where subject is already scheduled
        for (ScheduleSubject subject : subjects) {
            Set<String> existingDays = subjectDays.get(subject.subjectId);
            if (existingDays != null && !existingDays.isEmpty()) {
                for (int i = 0; i < numSlots; i++) {
                    TimeSlot slot = availableSlots.get(i);
                    if (existingDays.contains(slot.day)) {
                        // Cannot schedule this subject on this day
                        model.addEquality(lectureAssignments.get(subject.subjectId).get(i), 0);

                        if (subject.hasLab) {
                            model.addEquality(labAssignments.get(subject.subjectId).get(i), 0);
                        }
                    }
                }
            }
        }

        // Constraint 6: Prevent faculty time conflicts (same prof teaching different subjects at same time)
        // This is handled implicitly by room constraints and section conflicts

        // Constraint 7: Track which day each subject is scheduled on to avoid conflicts
        // NEW: Create a boolean variable for each subject and day
        Map<Integer, Map<String, BoolVar>> subjectDayAssignments = new HashMap<>();
        for (ScheduleSubject subject : subjects) {
            Map<String, BoolVar> dayAssignments = new HashMap<>();
            for (String day : Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")) {
                dayAssignments.put(day, model.newBoolVar("subject_" + subject.subjectId + "_day_" + day));
            }
            subjectDayAssignments.put(subject.subjectId, dayAssignments);
        }

        // For each subject, link the day variables with actual assignments
        for (ScheduleSubject subject : subjects) {
            for (String day : Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")) {
                List<BoolVar> slotsForDay = new ArrayList<>();
                for (int i = 0; i < numSlots; i++) {
                    if (availableSlots.get(i).day.equals(day)) {
                        slotsForDay.add(lectureAssignments.get(subject.subjectId).get(i));
                    }
                }
                if (!slotsForDay.isEmpty()) {
                    // dayVar is true if any slot on this day is assigned
                    model.addMaxEquality(subjectDayAssignments.get(subject.subjectId).get(day),
                            slotsForDay.toArray(new BoolVar[0]));
                }
            }
        }

        // Constraint 8: For all subjects, ensure they're scheduled on at most one day
        // This will prevent scheduling the same subject across multiple days
        // which could happen when no lab constraint forces different days
        for (ScheduleSubject subject : subjects) {
            List<BoolVar> dayVars = new ArrayList<>(subjectDayAssignments.get(subject.subjectId).values());
            model.addAtMostOne(dayVars.toArray(new BoolVar[0]));
            // We already have exactly-one constraint for the lectures, so we don't need another
        }

        // Constraint 9: Distribute subjects evenly across the week (soft constraint)
        for (String day : Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")) {
            List<BoolVar> subjectsOnDay = new ArrayList<>();
            for (ScheduleSubject subject : subjects) {
                subjectsOnDay.add(subjectDayAssignments.get(subject.subjectId).get(day));
            }

            if (!subjectsOnDay.isEmpty()) {
                LinearExprBuilder dayLoadExpr = LinearExpr.newBuilder();
                for (BoolVar var : subjectsOnDay) {
                    dayLoadExpr.add(var);
                }
                // Soft constraint - trying to minimize class load per day
                model.minimize(dayLoadExpr);
            }
        }

        // Solve the model
        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        // Extract the solution
        Map<Integer, List<TimeSlot>> scheduleSolution = new HashMap<>();

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            for (ScheduleSubject subject : subjects) {
                List<TimeSlot> subjectSlots = new ArrayList<>();

                // Check which slot is assigned for lecture
                for (int i = 0; i < numSlots; i++) {
                    if (solver.booleanValue(lectureAssignments.get(subject.subjectId).get(i))) {
                        TimeSlot assignedSlot = new TimeSlot(
                                availableSlots.get(i).day,
                                availableSlots.get(i).startTime,
                                availableSlots.get(i).endTime,
                                lectureRoomId
                        );
                        subjectSlots.add(assignedSlot);
                        break; // We only assign one lecture slot
                    }
                }

                // Check which slot is assigned for lab if applicable
                if (subject.hasLab) {
                    for (int i = 0; i < numSlots; i++) {
                        if (solver.booleanValue(labAssignments.get(subject.subjectId).get(i))) {
                            TimeSlot assignedSlot = new TimeSlot(
                                    availableSlots.get(i).day,
                                    availableSlots.get(i).startTime,
                                    availableSlots.get(i).endTime,
                                    labRoomId
                            );
                            subjectSlots.add(assignedSlot);
                            break; // We only assign one lab slot
                        }
                    }
                }

                scheduleSolution.put(subject.subjectId, subjectSlots);
            }
        }

        return scheduleSolution;
    }

    // Overloaded method for backward compatibility
    public Map<Integer, List<TimeSlot>> generateOptimizedSchedule(List<ScheduleSubject> subjects) {
        return generateOptimizedSchedule(subjects, null, -1, -1);
    }
}
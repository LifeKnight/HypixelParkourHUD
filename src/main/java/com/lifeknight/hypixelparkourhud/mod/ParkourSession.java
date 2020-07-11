package com.lifeknight.hypixelparkourhud.mod;

import com.google.gson.*;
import com.lifeknight.hypixelparkourhud.utilities.Stopwatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.lifeknight.hypixelparkourhud.mod.Core.*;

public class ParkourSession {
    private static final List<ParkourSession> parkourSessions = new ArrayList<>();
    private static ParkourSession currentParkourSession;
    private final int id;
    private boolean isDeleted = false;
    private final boolean type;
    private final String location;
    private long startTime;
    private Stopwatch stopwatch;
    private long millisecondsElapsed;
    private List<Long> checkpointTimes = new ArrayList<>();
    private long lastCheckpointTime = 0;

    public ParkourSession(boolean type, String location) {
        this.type = type;
        this.location = location;
        this.id = parkourSessions.size();
        parkourSessions.add(this);
    }

    private ParkourSession(int id, boolean type, String location, long startTime, long millisecondsElapsed, List<Long> checkpointTimes) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.startTime = startTime;
        this.millisecondsElapsed = millisecondsElapsed;
        this.checkpointTimes = checkpointTimes;
        parkourSessions.add(this);
        ParkourWorld.onParkourSessionCreate(type, location);
        ParkourWorld.addParkourSessionToAppropriateWorld(this);
    }

    public void activate() {
        startTime = System.currentTimeMillis();
        stopwatch = new Stopwatch();
        stopwatch.start();
        currentParkourSession = this;
        sessionIsRunning = true;
        ParkourWorld.onParkourSessionCreate(type, location);
    }

    public void end() {
        ParkourWorld.addParkourSessionToAppropriateWorld(this);
        onCheckpoint();
        stopwatch.end();
        millisecondsElapsed = stopwatch.getTotalMilliseconds();
        parkourLogger.plainLog(this.toString());
        sessionIsRunning = false;
    }

    public void onCheckpoint() {
        checkpointTimes.add(stopwatch.getTotalMilliseconds() - lastCheckpointTime);
        lastCheckpointTime = stopwatch.getTotalMilliseconds();
    }

    public static void createAndActivate(boolean type, String location) {
        if (sessionIsRunning) {
            cancelParkourSession();
        }
        new ParkourSession(type, location).activate();
    }

    public static void onCheckpointReached() {
        if (currentParkourSession != null) currentParkourSession.onCheckpoint();
    }

    public static void endCurrentSession() {
        if (currentParkourSession != null) currentParkourSession.end();
    }

    public static void cancelParkourSession() {
        if (currentParkourSession != null && sessionIsRunning) {
            sessionIsRunning = false;
            currentParkourSession.stopwatch.end();
            parkourSessions.remove(currentParkourSession);
        }
    }

    public boolean getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void toggleDelete() {
        isDeleted = !isDeleted;
        if (isDeleted) {
            try {
                deletedSessionIds.addElement(this.id);
            } catch (Exception ignored) {
            }
        } else {
            try {
                deletedSessionIds.removeElement(this.id);
            } catch (Exception ignored) {

            }
        }
    }

    public List<Long> getCheckpointTimes() {
        return checkpointTimes;
    }

    public long getCheckpointTime(int index) {
        if (timeDisplayType.getValue() == 0) {
            return index > checkpointTimes.size() - 1 ? getMillisecondsElapsed() : getTimeUpToCheckpoint(index + 1);
        }
        return index > checkpointTimes.size() - 1 ? getMillisecondsElapsed() - getTimeUpToCheckpoint(index + 1) : checkpointTimes.get(index);
    }

    public long getStartTime() {
        return startTime;
    }

    public long getMillisecondsElapsed() {
        return stopwatch != null && stopwatch.isRunning() ? stopwatch.getTotalMilliseconds() : millisecondsElapsed;
    }

    public long getTimeUpToCheckpoint(int checkpoint) {
        if (stopwatch != null && stopwatch.isRunning() && checkpoint > checkpointTimes.size()) return stopwatch.getTotalMilliseconds();
        long result = 0;
        for (int i = 0; i < checkpoint; i++) {
            result += checkpointTimes.get(i);
        }
        return result;
    }

    public String getFormattedDate() {
        return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(startTime);
    }

    public static ParkourSession getCurrentParkourSession() {
        return currentParkourSession;
    }

    @Override
    public String toString() {
        JsonObject asJsonObject = new JsonObject();

        asJsonObject.addProperty("id", id);
        asJsonObject.addProperty("type", type);
        asJsonObject.addProperty("location", location.replace("§", "\\u00A7"));
        asJsonObject.addProperty("startTime", startTime);
        asJsonObject.addProperty("millisecondsElapsed", millisecondsElapsed);
        JsonArray checkpointTimes = new JsonArray();
        for (long checkpointTime : this.checkpointTimes) {
            checkpointTimes.add(new JsonPrimitive(checkpointTime));
        }
        asJsonObject.add("checkpointTimes", checkpointTimes);

        return asJsonObject.toString();
    }

    public static void interpretParkourSessionFromString(String asString) {
        try {
            JsonObject asJsonObject = new JsonParser().parse(asString).getAsJsonObject();

            int id = asJsonObject.get("id").getAsInt();
            boolean type = asJsonObject.get("type").getAsBoolean();
            String location = asJsonObject.get("location").getAsString();
            long startTime = asJsonObject.get("startTime").getAsLong();
            long millisecondsElapsed = asJsonObject.get("millisecondsElapsed").getAsLong();
            List<Long> checkpointTimes = new ArrayList<>();
            for (JsonElement checkpointTime : asJsonObject.get("checkpointTimes").getAsJsonArray()) {
                checkpointTimes.add(checkpointTime.getAsLong());
            }

            ParkourSession parkourSession = new ParkourSession(id, type, location.replace("\\u00A7", "§"), startTime, millisecondsElapsed, checkpointTimes);

            parkourSession.isDeleted = deletedSessionIds.getValue().contains(parkourSession.id);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}

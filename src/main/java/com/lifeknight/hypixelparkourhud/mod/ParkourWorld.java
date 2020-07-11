package com.lifeknight.hypixelparkourhud.mod;

import com.lifeknight.hypixelparkourhud.utilities.Miscellaneous;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.lifeknight.hypixelparkourhud.mod.Core.sessionIsRunning;
import static com.lifeknight.hypixelparkourhud.mod.Core.timeToCompare;

public class ParkourWorld {
    private static final List<ParkourWorld> parkourWorlds = new ArrayList<>();
    private static ParkourWorld currentParkourWorld;
    private final List<ParkourSession> sessions = new ArrayList<>();
    private final boolean type;
    private final String location;

    public ParkourWorld(boolean type, String location) {
        this.type = type;
        this.location = location;
        parkourWorlds.add(this);
        currentParkourWorld = this;
    }

    public List<ParkourSession> getSessions() {
        return sessions;
    }

    public List<ParkourSession> getVisibleSessions() {
        List<ParkourSession> result = new ArrayList<>(sessions);
        result.removeIf(ParkourSession::isDeleted);
        return result;
    }

    public List<ParkourSession> getSessionsOrdered(boolean orderType) {
        List<ParkourSession> clone = new ArrayList<>(sessions);
        List<ParkourSession> result = new ArrayList<>();
        if (orderType) {
            while (clone.size() != 0) {
                ParkourSession nextParkourSession = clone.get(0);
                for (ParkourSession parkourSession : clone) {
                    if (parkourSession.getStartTime() > nextParkourSession.getStartTime()) {
                        nextParkourSession = parkourSession;
                    }
                }
                result.add(nextParkourSession);
                clone.remove(nextParkourSession);
            }
        } else {
            while (clone.size() != 0) {
                ParkourSession nextParkourSession = clone.get(0);
                for (ParkourSession parkourSession : clone) {
                    if (parkourSession.getMillisecondsElapsed() < nextParkourSession.getMillisecondsElapsed()) {
                        nextParkourSession = parkourSession;
                    }
                }
                result.add(nextParkourSession);
                clone.remove(nextParkourSession);
            }
        }
        return result;
    }

    public boolean isType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public static List<ParkourWorld> getParkourWorlds() {
        return parkourWorlds;
    }

    public ParkourSession getSessionToCompare() {
        List<ParkourSession> clone = getVisibleSessions();
        List<ParkourSession> result = new ArrayList<>();
        if (timeToCompare.getValue() == 0) {
            while (clone.size() != 0) {
                ParkourSession fastestParkourSession = null;
                for (ParkourSession parkourSession : clone) {
                    if (fastestParkourSession == null || parkourSession.getMillisecondsElapsed() < fastestParkourSession.getMillisecondsElapsed() && parkourSession.getCheckpointTimes().size() == getLatestParkourSession().getCheckpointTimes().size())
                        fastestParkourSession = parkourSession;
                }
                result.add(fastestParkourSession);
                clone.remove(fastestParkourSession);
                if (result.size() > 1) break;
            }
            return sessionIsRunning || result.size() == 1 ? result.get(0) : result.get(1);
        }
        while (clone.size() != 0) {
            ParkourSession latestParkourSession = null;
            for (ParkourSession parkourSession : clone) {
                if (latestParkourSession == null || parkourSession.getStartTime() > latestParkourSession.getStartTime() && parkourSession.getCheckpointTimes().size() == getLatestParkourSession().getCheckpointTimes().size())
                    latestParkourSession = parkourSession;
            }
            result.add(latestParkourSession);
            clone.remove(latestParkourSession);
            if (result.size() > 1) break;
        }
        return sessionIsRunning || result.size() == 1 ? result.get(0) : result.get(1);
    }

    public ParkourSession getLatestParkourSession() {
        ParkourSession latestParkourSession = null;
        for (ParkourSession parkourSession : sessions) {
            if (latestParkourSession == null || parkourSession.getStartTime() > latestParkourSession.getStartTime())
                latestParkourSession = parkourSession;
        }
        return latestParkourSession;
    }

    public static void addParkourSessionToAppropriateWorld(ParkourSession parkourSession) {
        for (ParkourWorld parkourWorld : parkourWorlds) {
            if (parkourWorld.type == parkourSession.getType() && parkourWorld.location.equals(parkourSession.getLocation())) {
                parkourWorld.sessions.add(parkourSession);
                break;
            }
        }
    }

    public static void onParkourSessionCreate(boolean type, String location) {
        if (!containsParkourWorld(type, location)) {
            new ParkourWorld(type, location);
        }
    }

    public static boolean containsParkourWorld(boolean type, String location) {
        for (ParkourWorld parkourWorld : parkourWorlds) {
            if (parkourWorld.type == type && parkourWorld.location.equals(location)) return true;
        }
        return false;
    }

    public static ParkourWorld getCurrentParkourWorld() {
        for (ParkourWorld parkourWorld : parkourWorlds) {
            if (parkourWorld.type == Core.type && parkourWorld.location.equals(Core.location)) return parkourWorld;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkourWorld that = (ParkourWorld) o;
        return type == that.type &&
                location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, location);
    }

    public void clearSessions() {
        for (ParkourSession parkourSession : sessions) {
            if (!parkourSession.isDeleted()) {
                parkourSession.toggleDelete();
            }
        }
    }

    public void toggleByLocation(String displayString, boolean displayType) {
        for (ParkourSession parkourSession : sessions) {
            if (displayType ? parkourSession.getFormattedDate().equals(displayString) :
                    Miscellaneous.formatTimeFromMilliseconds(parkourSession.getMillisecondsElapsed()).equals(displayString)) {
                parkourSession.toggleDelete();
                break;
            }
        }
    }
}

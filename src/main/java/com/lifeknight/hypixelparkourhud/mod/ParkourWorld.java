package com.lifeknight.hypixelparkourhud.mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public boolean isType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public ParkourSession getLatestSession() {
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
            if (parkourWorld.type == Mod.type && parkourWorld.location.equals(Mod.location)) return parkourWorld;
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
}

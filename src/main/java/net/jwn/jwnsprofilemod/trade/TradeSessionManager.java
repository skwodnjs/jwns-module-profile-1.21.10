package net.jwn.jwnsprofilemod.trade;

import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TradeSessionManager {
    private TradeSessionManager() {}

    public static final Set<UUID> tradingPlayerUUID = new HashSet<>();

    private static final Map<UUID, TradeSession> SESSIONS = new ConcurrentHashMap<>();

    public static TradeSession create(ServerLevel level, UUID a, UUID b) {
        TradeSession s = new TradeSession(level, a, b);
        SESSIONS.put(s.id(), s);
        return s;
    }

    public static TradeSession playerBIsJoined(UUID sessionId) {
        TradeSession session = SESSIONS.get(sessionId);
        session.playerBIsJoined();
        return session;
    }

    public static TradeSession get(UUID id) {
        return SESSIONS.get(id);
    }

    public static void remove(UUID id) {
        SESSIONS.remove(id);
    }
}

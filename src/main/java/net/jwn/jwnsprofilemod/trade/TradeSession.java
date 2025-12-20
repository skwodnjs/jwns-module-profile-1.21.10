package net.jwn.jwnsprofilemod.trade;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TradeSession implements MenuProvider {
    private final UUID id;
    private final ServerLevel level;
    private final UUID playerA;
    private final UUID playerB;
    private Boolean isPlayerBJoined = false;
    private final Container offerA = new SimpleContainer(9);

    public TradeSession(ServerLevel level, UUID playerA, UUID playerB) {
        this.id = UUID.randomUUID();
        this.level = level;
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public UUID id() { return id; }
    public ServerLevel level() { return level; }
    public UUID playerA() { return playerA; }
    public UUID playerB() { return playerB; }
    public Boolean isPlayerBJoined() { return isPlayerBJoined; }
    public Container offerA() { return offerA; }

    public void playerBIsJoined() {
        isPlayerBJoined = true;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("jwnsprofilemod.trade.title");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        UUID u = player.getUUID();
        if (!u.equals(playerA) && !u.equals(playerB)) return null;

        return new TradeMenu(containerId, inv, this);
    }
}

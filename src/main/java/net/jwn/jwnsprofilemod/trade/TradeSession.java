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
    private final UUID playerA;
    private final UUID playerB;
    private Boolean isPlayerBJoined = false;
    private final Container offerA = new SimpleContainer(9);
    private final Container offerB = new SimpleContainer(9);
    private Boolean playerAReady = false;
    private Boolean playerBReady = false;

    public TradeSession(UUID playerA, UUID playerB) {
        this.id = UUID.randomUUID();
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public UUID id() { return id; }
    public UUID playerA() { return playerA; }
    public UUID playerB() { return playerB; }
    public Boolean isPlayerBJoined() { return isPlayerBJoined; }
    public Container offerA() { return offerA; }
    public Container offerB() { return offerB; }
    public boolean playerAReady() { return playerAReady; }
    public boolean playerBReady() { return playerBReady; }

    public void playerAisReady() { playerAReady = true; }
    public void playerBisReady() { playerBReady = true; }
    public void playerBIsJoined() {
        isPlayerBJoined = true;
    }

    private int life = 20 * 20;

    public void tick() {
        if (!(isPlayerBJoined)) life -= 1;
    }

    public int life() {
        return this.life;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("jwnsprofilemod.trade.title");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        UUID u = player.getUUID();
        if (!u.equals(playerA) && !u.equals(playerB)) return null;

        return new TradeMenu(containerId, inventory, this, playerA, playerB);
    }
}

package net.jwn.jwnsprofilemod.trade;

import net.jwn.jwnsprofilemod.networking.packet.CloseTradeToastS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class TradeMenu extends AbstractContainerMenu {
    private final TradeSession session;
    public UUID playerAUUID;
    public UUID playerBUUID;
    public final DataSlot isPlayerBJoined = DataSlot.standalone();

    public TradeMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv);
        this.playerAUUID = extraData.readUUID();
        this.playerBUUID = extraData.readUUID();
    }

    private final MinecraftServer server;
    private final Player player;

    private TradeMenu(int containerId, Inventory inv) {
        super(ModMenuTypes.TRADE_MENU.get(), containerId);
        this.session = null;
        this.player = inv.player;
        this.server = player.level().getServer();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        Container aDummy = new SimpleContainer(9);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(aDummy, row * 3 + col, 38 + col * 18, 22 + row * 18));
            }
        }

        ContainerData data = new SimpleContainerData(9);
        addDataSlots(data);
        addDataSlot(isPlayerBJoined);
    }

    public TradeMenu(int containerId, Inventory inv, TradeSession session) {
        super(ModMenuTypes.TRADE_MENU.get(), containerId);
        this.session = session;
        this.player = inv.player;
        this.server = player.level().getServer();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        Container a = session.offerA();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new Slot(a, row * 3 + col, 38 + col * 18, 22 + row * 18));
            }
        }

        ContainerData data = new SimpleContainerData(9);
        addDataSlots(data);
        addDataSlot(isPlayerBJoined);
    }

    @Override
    public void broadcastChanges() {
        if (session != null) {
            session.tick();
            int s = session.isPlayerBJoined() ? 1 : 0;
            if (isPlayerBJoined.get() != s) {
                isPlayerBJoined.set(s);
            }
            if (session.life() == 0) {
                TradeSessionManager.sessionClose(session, server);
                ServerPlayer target = server.getPlayerList().getPlayer(session.playerB());
                if (target != null) {
                    CloseTradeToastS2CPacket packet = new CloseTradeToastS2CPacket(true);
                    PacketDistributor.sendToPlayer(target, packet);
                }
            }
        }
        super.broadcastChanges();
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 9;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 12 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 12 + i * 18, 142));
        }
    }
}

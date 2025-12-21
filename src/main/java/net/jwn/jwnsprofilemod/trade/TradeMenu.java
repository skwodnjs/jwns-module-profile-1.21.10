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

import java.util.Objects;
import java.util.UUID;

public class TradeMenu extends AbstractContainerMenu {
    private final TradeSession session;
    public UUID playerAUUID;
    public UUID playerBUUID;
    public final DataSlot isPlayerBJoined = DataSlot.standalone();
    public final DataSlot isPlayerAReady = DataSlot.standalone();
    public final DataSlot isPlayerBReady = DataSlot.standalone();

    private int position = -1;

    public TradeMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, (TradeSession) null, extraData.readUUID(), extraData.readUUID());
    }

    private final MinecraftServer server;

    public TradeMenu(int containerId, Inventory inv, TradeSession session, UUID playerAUUID, UUID playerBUUID) {
        super(ModMenuTypes.TRADE_MENU.get(), containerId);
        this.session = session;
        this.server = inv.player.level().getServer();

        this.playerAUUID = playerAUUID;
        this.playerBUUID = playerBUUID;

        if (Objects.equals(this.playerAUUID, inv.player.getUUID())) this.position = 0;
        else if (Objects.equals(this.playerBUUID, inv.player.getUUID())) this.position = 1;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        Container a = session != null ? session.offerA() : new SimpleContainer(9);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (this.position == -1 || this.position == 1) {
                    this.addSlot(new Slot(a, row * 3 + col, 38 + col * 18, 22 + row * 18) {
                        @Override
                        public boolean mayPickup(Player player) {
                            return false;
                        }

                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return false;
                        }
                    });
                } else {
                    this.addSlot(new Slot(a, row * 3 + col, 38 + col * 18, 22 + row * 18) {
                        @Override
                        public boolean mayPickup(Player player) {
                            return false;
                        }

                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return isPlayerAReady.get() == 0;
                        }
                    });
                }
            }
        }

        Container b = session != null ? session.offerB() : new SimpleContainer(9);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (this.position == -1 || this.position == 0) {
                    this.addSlot(new Slot(b, row * 3 + col, 116 + col * 18, 22 + row * 18) {
                        @Override
                        public boolean mayPickup(Player player) {
                            return false;
                        }

                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return false;
                        }
                    });
                } else {
                    this.addSlot(new Slot(b, row * 3 + col, 116 + col * 18, 22 + row * 18) {
                        @Override
                        public boolean mayPickup(Player player) {
                            return false;
                        }

                        @Override
                        public boolean mayPlace(ItemStack stack) {
                            return isPlayerBReady.get() == 0;
                        }
                    });
                }
            }
        }

        ContainerData data = new SimpleContainerData(18);
        addDataSlots(data);

        addDataSlot(isPlayerBJoined);
        addDataSlot(isPlayerAReady);
        addDataSlot(isPlayerBReady);
    }

    public int getPosition() {
        return position;
    }

    @Override
    public void broadcastChanges() {
        if (session != null) {
            int s = session.isPlayerBJoined() ? 1 : 0;
            if (isPlayerBJoined.get() != s) {
                isPlayerBJoined.set(s);
            }

            int a = session.playerAReady() ? 1 : 0;
            if (isPlayerAReady.get() != a) {
                isPlayerAReady.set(a);
            }
            int b = session.playerBReady() ? 1 : 0;
            if (isPlayerBReady.get() != b) {
                isPlayerBReady.set(b);
            }

            session.tick();
            if (session.life() == 0) {
                TradeSessionManager.sessionClose(session, server);
                ServerPlayer target = server.getPlayerList().getPlayer(session.playerB());
                if (target != null) {
                    CloseTradeToastS2CPacket packet = new CloseTradeToastS2CPacket(true);
                    PacketDistributor.sendToPlayer(target, packet);
                }
            }
            if (session.playerAReady() && session.playerBReady()) {
                TradeSessionManager.sessionSuccessed(session, server);
            }
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
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

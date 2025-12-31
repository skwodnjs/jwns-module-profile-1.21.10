package net.jwn.jwnprofile.command;

import net.jwn.jwnprofile.JWNsProfileMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = JWNsProfileMod.MOD_ID)
public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        new OpenProfileCommand(event.getDispatcher());
        new TradeRequestHandleCommand(event.getDispatcher());
        new TradeRequestCommand(event.getDispatcher());
    }
}

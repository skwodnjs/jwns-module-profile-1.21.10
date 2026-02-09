package net.jwn.jwnprofile;

import net.jwn.jwnprofile.screen.TradeScreen;
import net.jwn.jwnprofile.trade.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = JWNsProfileMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = JWNsProfileMod.MOD_ID, value = Dist.CLIENT)
public class JWNsProfileModClient {
    public JWNsProfileModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.TRADE_MENU.get(), TradeScreen::new);
    }
}

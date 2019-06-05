package muramasa.gtu.proxy;

import muramasa.gtu.api.util.SoundType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public interface IProxy {

    void preInit(FMLPreInitializationEvent e);

    void init(FMLInitializationEvent e);

    void postInit(FMLPostInitializationEvent e);

    void serverStarting(FMLServerStartingEvent e);

    void playSound(SoundType type);

    void sendDiggingPacket(BlockPos pos);

    String trans(String unlocalized);
}

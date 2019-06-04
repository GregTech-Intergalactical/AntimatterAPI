package muramasa.gtu.proxy;

import muramasa.gtu.api.util.SoundType;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ServerProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        //NOOP
    }

    @Override
    public void init(FMLInitializationEvent e) {
        //NOOP
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        //NOOP
    }

    @Override
    public void serverStarting(FMLServerStartingEvent e) {
        //NOOP
    }

    @Override
    public void playSound(SoundType type) {
        //NOOP
    }

    @Override
    public String trans(String unlocalized) { //TODO server side localization?
        return new TextComponentTranslation(unlocalized).toString();
    }
}

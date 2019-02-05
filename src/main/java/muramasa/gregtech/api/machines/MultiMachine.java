package muramasa.gregtech.api.machines;

import muramasa.gregtech.GregTech;
import muramasa.gregtech.api.enums.MachineFlag;
import muramasa.gregtech.common.utils.Ref;
import muramasa.gregtech.loaders.ContentLoader;
import net.minecraft.util.ResourceLocation;

import static muramasa.gregtech.api.enums.MachineFlag.MULTI;

public class MultiMachine extends Machine {

    public MultiMachine(String name, Class tileClass, MachineFlag... extraFlags) {
        super(name, ContentLoader.blockMultiMachine, tileClass);
        setTiers(Tier.getMulti());
        addFlags(MULTI);
        addFlags(extraFlags);
        addRecipeMap();
        addGUI(GregTech.INSTANCE, Ref.MULTI_MACHINE_ID);
        //TODO add structure pattern
    }

    @Override
    public ResourceLocation getBaseTexture(String tier) {
        return new ResourceLocation(Ref.MODID, "blocks/machines/base/" + name);
    }
}

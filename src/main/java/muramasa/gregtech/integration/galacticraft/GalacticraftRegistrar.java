package muramasa.gregtech.integration.galacticraft;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.interfaces.GregTechRegistrar;
import muramasa.gregtech.api.materials.Material;
import net.minecraftforge.fml.common.Loader;

import static muramasa.gregtech.api.materials.ItemFlag.ORE;
import static muramasa.gregtech.api.materials.MaterialSet.METALLIC;

public class GalacticraftRegistrar extends GregTechRegistrar {

    public static Material MeteoricIron, MeteoricSteel;

    @Override
    public boolean isEnabled() {
        return Loader.isModLoaded(Ref.MOD_GC) && Loader.isModLoaded(Ref.MOD_GC_PLANETS);
    }

    @Override
    public void onMaterialRegistration() {
        MeteoricIron = new Material("Meteoric Iron", 0x643250, METALLIC).asMetal(1811, 0, ORE).addTools(6.0f, 384, 2);
        MeteoricSteel = new Material("Meteoric Steel", 0x321928, METALLIC).asMetal(1811, 1000).addTools(6.0f, 768, 2);
    }
}

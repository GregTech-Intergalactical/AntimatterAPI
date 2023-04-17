package muramasa.antimatter.client.forge;

import muramasa.antimatter.Ref;
import muramasa.antimatter.client.model.loader.forge.AntimatterModelLoader;
import muramasa.antimatter.client.model.loader.forge.DefaultModelLoader;
import muramasa.antimatter.client.model.loader.forge.MachineModelLoader;
import net.minecraft.resources.ResourceLocation;

public class AntimatterModelManagerImpl {
    public static void initPlatform() {
        new DefaultModelLoader(new ResourceLocation(Ref.ID, "main"));
        new MachineModelLoader.CoverModelLoader(new ResourceLocation(Ref.ID, "cover"));
        new MachineModelLoader.SideModelLoader(new ResourceLocation(Ref.ID, "machine_side"));
        new AntimatterModelLoader.DynamicModelLoader(new ResourceLocation(Ref.ID, "dynamic"));
        new MachineModelLoader(new ResourceLocation(Ref.ID, "machine"));
        new AntimatterModelLoader.PipeModelLoader(new ResourceLocation(Ref.ID, "pipe"));
        new AntimatterModelLoader.ProxyModelLoader(new ResourceLocation(Ref.ID, "proxy"));
    }
}

package muramasa.antimatter.datagen.resources;

import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Map;

public class DynamicPackFinder implements IPackFinder {

    protected String packId;
    protected boolean hidden;
    protected DynamicResourcePack pack;

    public DynamicPackFinder(String packId, boolean hidden) {
        this.packId = packId;
        this.hidden = hidden;
        this.pack = new DynamicResourcePack(this);
    }

    public DynamicResourcePack getPack() {
        return pack;
    }

    @Override
    public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> packs, ResourcePackInfo.IFactory<T> factory) {
        final T packInfo = ResourcePackInfo.createResourcePack(packId, true, () -> pack, factory, ResourcePackInfo.Priority.TOP);
        if (packInfo != null) {
            ObfuscationReflectionHelper.setPrivateValue(ResourcePackInfo.class, packInfo, hidden, 10);
            packs.put(packId, packInfo);
        }
    }
}

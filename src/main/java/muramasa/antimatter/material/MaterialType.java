package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MaterialType<T> implements IMaterialTag, IAntimatterObject {

    protected String id;
    protected int unitValue, layers;
    protected boolean generating = true, blockType, visible;
    protected Set<Material> materials = new LinkedHashSet<>(); //Linked to preserve insertion order for JEI
    protected Map<MaterialType<?>, ITag.INamedTag<?>> tagMap = new Object2ObjectOpenHashMap<>();
    protected T getter;

    public MaterialType(String id, int layers, boolean visible, int unitValue) {
        this.id = id;
        this.visible = visible;
        this.unitValue = unitValue;
        this.layers = layers;
        this.tagMap.put(this, Utils.getForgeItemTag(Utils.getConventionalMaterialType(this)));
        register(MaterialType.class, getId());
    }

    public MaterialType<T> nonGen() {
        generating = false;
        return this;
    }

    public MaterialType<T> blockType() {
        blockType = true;
        this.tagMap.put(this, Utils.getForgeBlockTag(Utils.getConventionalMaterialType(this)));
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public int getUnitValue() {
        return unitValue;
    }

    public int getLayers() {
        return layers;
    }

    public <T> ITag.INamedTag<T> getTag() {
        return (ITag.INamedTag<T>) tagMap.get(this);
    }

    public MaterialType<T> set(T getter) {
        this.getter = getter;
        return this;
    }

    public T get() {
        return getter;
    }

    @Override
    public Set<Material> all() {
        return materials;
    }

    public boolean isVisible() {
        return visible || AntimatterConfig.JEI.SHOW_ALL_MATERIAL_ITEMS;
    }

    public boolean allowGen(Material material) {
        return generating && materials.contains(material);
    }

    @Override
    public String toString() {
        return getId();
    }
}

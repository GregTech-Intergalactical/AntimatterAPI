package muramasa.antimatter.integration.kubejs;

import dev.latvian.kubejs.event.EventJS;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Element;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.TextureSet;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class AMCreationEvent extends EventJS {
    public StoneType createStoneType(String id, String material, String texture, SoundType soundType, boolean generateBlock) {
        return AntimatterAPI.register(StoneType.class, new StoneType(Ref.MOD_KJS, id, Material.get(material), new Texture(texture), soundType, generateBlock));
    }

    public StoneType createStoneType(String id, String material, String texture, SoundType soundType, String stoneState) {
        return AntimatterAPI.register(StoneType.class, new StoneType(Ref.MOD_KJS, id, Material.get(material), new Texture(texture), soundType, false).setStateSupplier(() -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stoneState)).defaultBlockState()));
    }

    public Material createMaterial(String id, int rgb, String textureSet, String textureSetDomain) {
        TextureSet set = Objects.requireNonNull(AntimatterAPI.get(TextureSet.class, textureSet, textureSetDomain), "Specified texture set in Material created via kubejs event is null");
        return AntimatterAPI.register(Material.class, new Material(Ref.MOD_KJS, id, rgb, set));
    }

    public Material createMaterial(String id, int rgb, String textureSet, String textureSetDomain, String element) {
        TextureSet set = Objects.requireNonNull(AntimatterAPI.get(TextureSet.class, textureSet, textureSetDomain), "Specified texture set in Material created via kubejs event is null");
        return AntimatterAPI.register(Material.class, new Material(Ref.MOD_KJS, id, rgb, set, Element.getFromElementId(element)));
    }

    public void addFlagsToMaterial(String materialId, String... flags) {
        if (Material.get(materialId) != Data.NULL) {
            for (String flag : flags) {
                if (type(flag) != null) {
                    Material.get(materialId).flags(type(flag));
                }
            }
        }
    }

    public MaterialType type(String type) {
        return AntimatterAPI.get(MaterialType.class, type);
    }
}

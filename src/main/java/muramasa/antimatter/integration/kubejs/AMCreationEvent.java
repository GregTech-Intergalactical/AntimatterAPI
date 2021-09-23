package muramasa.antimatter.integration.kubejs;

import dev.latvian.kubejs.event.EventJS;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Element;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.TextureSet;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.SoundType;
import net.minecraft.client.audio.Sound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class AMCreationEvent extends EventJS {
    public StoneType createStoneType(String id, String material, String texture, SoundType soundType, boolean generateBlock){
        return new StoneType(Ref.ID, id, Material.get(material), new Texture(texture), soundType, generateBlock);
    }

    public StoneType createStoneType(String id, String material, String texture, SoundType soundType, String stoneState){
        return new StoneType(Ref.ID, id, Material.get(material), new Texture(texture), soundType, false).setStateSupplier(() -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stoneState)).getDefaultState());
    }

    public Material createMaterial(String id, int rgb, String textureSet){
        return new Material(Ref.ID, id, rgb, AntimatterAPI.get(TextureSet.class, textureSet));
    }

    public Material createMaterial(String id, int rgb, String textureSet, String element){
        return new Material(Ref.ID, id, rgb, AntimatterAPI.get(TextureSet.class, textureSet), Element.getFromElementId(element));
    }

    public MaterialType type(String type) {
        return AntimatterAPI.get(MaterialType.class, type);
    }
}

package muramasa.antimatter.data;

import muramasa.antimatter.Ref;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class AntimatterTags {
    public static final TagKey<Fluid> ACID = TagUtils.getFluidTag(new ResourceLocation(Ref.ID, "acid"));
    public static final TagKey<Item> RADIOACTIVE = TagUtils.getItemTag(new ResourceLocation(Ref.ID, "radioactive"));
    public static final TagKey<Item> RADIATION_PROOF = TagUtils.getItemTag(new ResourceLocation(Ref.ID, "radiation_proof"));
    static void init(){

    }
}

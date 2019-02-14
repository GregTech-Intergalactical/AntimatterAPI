package muramasa.gregtech.api.cover;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public abstract class CoverBehaviour {

    private static int lastInternalId = 0;

    private int internalId;

    public CoverBehaviour() {
        internalId = lastInternalId++;
    }

    public abstract String getName();

    public int getInternalId() {
        return internalId;
    }

    public void onUpdate(TileEntityBase tile) {
        //NOOP
    }

    public List<BakedQuad> onRender(List<BakedQuad> quads) {
        return quads;
    }

    public boolean needsNewInstance() {
        return false;
    }

    public CoverBehaviour getNewInstance(ItemStack stack) {
        //TODO avoid assigning a new internal id on a new instance creation
        return this;
    }

    public boolean isEqual(CoverBehaviour otherBehaviour) {
        return internalId == otherBehaviour.internalId;
    }

    public boolean isEmpty() {
        return internalId == GregTechAPI.CoverBehaviourNone.internalId;
    }

    public ModelResourceLocation getModelLoc() {
        return new ModelResourceLocation(Ref.MODID + ":machine_part/covers/" + getName());
    }

    public ResourceLocation getTextureLoc() {
        return new ResourceLocation(Ref.MODID, "blocks/machines/covers/" + getName());
    }

    public boolean retextureToMachineTier() {
        return false;
    }

    public static int getLastInternalId() {
        return lastInternalId;
    }
}

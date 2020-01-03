package muramasa.antimatter.tileentities.multi;

import muramasa.gtu.Ref;
import muramasa.antimatter.capability.GTCapabilities;
import muramasa.antimatter.capability.impl.HatchComponentHandler;
import muramasa.antimatter.capability.impl.MachineFluidHandler;
import muramasa.gtu.data.Machines;
import muramasa.antimatter.machines.ContentEvent;
import muramasa.antimatter.machines.Tier;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.texture.TextureData;
import muramasa.antimatter.tileentities.TileEntityMachine;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Optional;

import static muramasa.antimatter.machines.MachineFlag.FLUID;

public class TileEntityHatch extends TileEntityMachine implements IComponent {

    protected Optional<HatchComponentHandler> componentHandler = Optional.empty();
    protected int textureOverride = -1;

    @Override
    public void onLoad() {
        super.onLoad();
        componentHandler = Optional.of(new HatchComponentHandler(this));
        if (getMachineType().hasFlag(FLUID)) fluidHandler = Optional.of(new MachineFluidHandler(this, 8000 * getTierId(), fluidData));
    }

    @Override
    public Optional<HatchComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void onContentsChanged(ContentEvent type, int slot) {
        componentHandler.ifPresent(h -> h.getFirstController().ifPresent(controller -> {
            switch (type) {
                case ITEM_INPUT:
                    controller.onContentsChanged(type, slot);
                    break;
                case ITEM_OUTPUT:
                    controller.onContentsChanged(type, slot);
                case ITEM_CELL:
                    //TODO handle cells
                    break;
                case FLUID_INPUT:
                    //TODO
                    break;
            }
        }));
    }

    @Override
    public TextureData getTextureData() {
        TextureData data = super.getTextureData();
        if (textureOverride > -1) data.base(Machines.get(textureOverride / 1000).getBaseTexture(Tier.get(textureOverride % 1000)));
        return data;
    }

    @Override
    public void setTextureOverride(int textureOverride) {
        this.textureOverride = textureOverride;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == GTCapabilities.COMPONENT && componentHandler.isPresent()) return LazyOptional.of(() -> componentHandler.get()).cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT tag) {
        //TODO should texture be saved? it should be re-overridden when re-linked to a controller?
        super.read(tag);
        textureOverride = tag.contains(Ref.KEY_MACHINE_TILE_TEXTURE) ? tag.getInt(Ref.KEY_MACHINE_TILE_TEXTURE) : -1;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        if (textureOverride != -1) tag.putInt(Ref.KEY_MACHINE_TILE_TEXTURE, textureOverride);
        return tag;
    }
}

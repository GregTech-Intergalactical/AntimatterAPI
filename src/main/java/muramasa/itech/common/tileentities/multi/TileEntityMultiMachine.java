package muramasa.itech.common.tileentities.multi;

import muramasa.itech.api.capability.IComponent;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.api.structure.StructureResult;
import muramasa.itech.common.blocks.BlockMultiMachines;
import muramasa.itech.common.tileentities.TileEntityTickable;
import muramasa.itech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileEntityMultiMachine extends TileEntityTickable {

    private String typeFromNBT = "";
    public boolean shouldCheckRecipe, shouldCheckStructure;
    public boolean validStructure;

    public int curProgress, maxProgress;

    private Recipe activeRecipe;

    private ArrayList<IComponent> components = new ArrayList<>();

    public void init(String type) {
        typeFromNBT = type;
    }

    @Override
    public void onFirstTick() {
        if (typeFromNBT.isEmpty()) {
            typeFromNBT = MachineList.BLASTFURNACE.getName();
        }
        init(typeFromNBT);
        shouldCheckStructure = true;
    }

    @Override
    public void update() {



        super.update();
        if (isServerSide()) {
            if (shouldCheckStructure) {
                clearComponents();
                if (checkStructure()) {
                    validStructure = true;
                } else {
                    validStructure = false;
                    clearComponents();
                }
//                System.out.println("STRUCTURE: " + validStructure);
                shouldCheckStructure = false;
            }
        }
        if (shouldCheckRecipe) {
            checkRecipe();
            shouldCheckRecipe = false;
        }
        getMachineType().getBehaviour().onTick(this);
    }

    public void checkRecipe() {
        getMachineType().getBehaviour().onRecipe(this);
        System.out.println(maxProgress);
    }

    public void advanceRecipe() {
        getMachineType().getBehaviour().onAdvanceRecipe(this);
    }

    private boolean checkStructure() {
        StructurePattern pattern = getMachineType().getPattern();
        StructureResult result = pattern.evaluate(this);
        if (result.evaluate()) {
            components = result.getComponents();
            for (int i = 0; i < components.size(); i++) {
                components.get(i).linkController(this);
            }
            System.out.println("[Structure Debug] Valid Structure");
            return true;
        }
        System.out.println(result.getError());
        return false;
    }

    public void onComponentRemoved() {
        clearComponents();
        validStructure = false;
        System.out.println("INVALIDATED STRUCTURE");
    }

    public void clearComponents() {
        for (int i = 0; i < components.size(); i++) {
            TileEntity tile = world.getTileEntity(components.get(i).getPos());
            if (tile != null && tile instanceof IComponent) {
                ((IComponent) tile).unlinkController(this);
            } else {
                System.out.println("ESCAPED NULL COMPONENT");
            }
        }
        components.clear();
    }

    public Machine getMachineType() {
        return MachineList.get(getType());
    }

    public String getType() {
        return typeFromNBT;
    }

    public EnumFacing getFacing() {
        return getState().getValue(BlockMultiMachines.FACING);
    }

//    @Override
//    public String getId() {
//        return typeFromNBT;
//    }
//
//    @Override
//    public BlockPos getPos() {
//        return pos;
//    }
//
//    @Override
//    public void linkController(TileEntityMultiMachine tile) {
//        //NOOP
//    }
//
//    @Override
//    public void unlinkController(TileEntityMultiMachine tile) {
//        //NOOP
//    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.TAG_MULTIMACHINE_TILE_DATA)) {
            if (compound.hasKey(Ref.KEY_MULTIMACHINE_TILE_TYPE)) {
                typeFromNBT = compound.getString(Ref.KEY_MULTIMACHINE_TILE_TYPE);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagCompound tileData = new NBTTagCompound();
        tileData.setString(Ref.KEY_MULTIMACHINE_TILE_TYPE, getType());
        compound.setTag(Ref.TAG_MULTIMACHINE_TILE_DATA, tileData);
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return super.getCapability(capability, facing);
    }
}

package muramasa.itech.common.tileentities.base.multi;

import muramasa.itech.api.capability.IComponent;
import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.capability.implementations.ControllerComponentHandler;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.api.structure.StructureResult;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileEntityMultiMachine extends TileEntityMachine {

    public boolean shouldCheckRecipe, shouldCheckStructure;
    public boolean validStructure;
    public int curProgress, maxProgress;
    private Recipe activeRecipe;

    private ArrayList<IComponent> components;

    private ControllerComponentHandler componentHandler;

    @Override
    public void init(String type, String tier) {
        super.init(type, tier);
        components = new ArrayList<>();
        componentHandler = new ControllerComponentHandler(type, this);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        shouldCheckStructure = true;
    }

    @Override
    public void onServerUpdate() {
        if (shouldCheckStructure) {
            clearComponents();
            if (checkStructure()) {
                validStructure = true;
            } else {
                validStructure = false;
                clearComponents();
            }
            shouldCheckStructure = false;
        }
        if (shouldCheckRecipe) {
            checkRecipe();
            shouldCheckRecipe = false;
        }
    }

    public void checkRecipe() {

    }

    public void advanceRecipe() {

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

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ITechCapabilities.COMPONENT) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITechCapabilities.COMPONENT) {
            return ITechCapabilities.COMPONENT.cast(componentHandler);
        }
        return super.getCapability(capability, facing);
    }
}

package muramasa.itech.common.tileentities.base.multi;

import muramasa.itech.api.capability.IComponent;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.api.structure.StructureResult;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class TileEntityMultiMachine extends TileEntityMachine implements IComponent {

    public boolean shouldCheckRecipe, shouldCheckStructure;
    public boolean validStructure;
    public int curProgress, maxProgress;
    private Recipe activeRecipe;
    private ArrayList<IComponent> components = new ArrayList<>();

    @Override
    public void init(String type, String tier) {
        super.init(type, tier);
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

    public EnumFacing getFacing() {
        return getState().getValue(ITechProperties.FACING);
    }

    @Override
    public String getId() {
        return getType();
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public ArrayList<BlockPos> getLinkedControllers() {
        return new ArrayList<>();
    }

    @Override
    public void linkController(TileEntityMultiMachine tile) {
        //NOOP
    }

    @Override
    public void unlinkController(TileEntityMultiMachine tile) {
        //NOOP
    }
}

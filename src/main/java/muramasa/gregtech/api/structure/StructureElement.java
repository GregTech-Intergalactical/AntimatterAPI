package muramasa.gregtech.api.structure;

import muramasa.gregtech.api.capability.IComponent;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.enums.CasingType;
import muramasa.gregtech.api.enums.CoilType;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.api.util.int3;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;

import java.util.HashMap;

public class StructureElement {

    private static HashMap<String, StructureElement> elementLookup = new HashMap<>();

    /** Component Elements **/
    public static StructureElement EBF = new StructureElement(Machines.BLAST_FURNACE);
    public static StructureElement HATCH_OR_CASING_EBF = new StructureElement("hatchorcasingebf", CasingType.HEAT_PROOF, Machines.HATCH_ITEM_INPUT, Machines.HATCH_ITEM_OUTPUT);
    public static StructureElement ANY_COIL_EBF = new StructureElement("anycoilebf", CoilType.values());

    public static StructureElement FR_MACHINE = new StructureElement(Machines.FUSION_REACTOR);
    public static StructureElement FUSION_CASING = new StructureElement(CasingType.FUSION_3);
    public static StructureElement FUSION_COIL = new StructureElement(CoilType.FUSION);

    /** Custom Elements **/
    public static StructureElement X = new StructureElement("x", false); //Used to skip positions for non-cubic structures
    public static StructureElement AIR = new StructureElement("air") { //Air Block Check
        @Override
        public boolean evaluate(TileEntityMultiMachine machine, int3 pos, StructureResult result) {
            IBlockState state = machine.getWorld().getBlockState(pos.asBlockPos());
            return state.getBlock().isAir(state, machine.getWorld(), pos.asBlockPos());
        }
    };

    private String elementName;
    private String[] elementIds;
    private boolean addToList;

    public StructureElement(IStringSerializable elementName) {
        this(elementName.getName(), true, elementName);
    }

    public StructureElement(String elementName, IStringSerializable... elementIds) {
        this(elementName, true, elementIds);
    }

    public StructureElement(String elementName, boolean addToList, IStringSerializable... elementIds) {
        this.elementName = elementName;
        this.addToList = addToList;
        this.elementIds = new String[elementIds.length];
        for (int i = 0; i < elementIds.length; i++) {
            this.elementIds[i] = elementIds[i].getName();
            elementLookup.put(elementIds[i].getName(), this);
        }
        elementLookup.put(elementName, this);
    }

    public String getName() {
        return elementName;
    }

    public boolean shouldAddToList() {
        return addToList;
    }

    public boolean evaluate(TileEntityMultiMachine machine, int3 pos, StructureResult result) {
        TileEntity tile = Utils.getTile(machine.getWorld(), pos.asBlockPos());
        if (tile != null && tile.hasCapability(GTCapabilities.COMPONENT, null)) {
            IComponent component = tile.getCapability(GTCapabilities.COMPONENT, null);
            for (int i = 0; i < elementIds.length; i++) {
                if (elementIds[i].equals(component.getId())) {
                    result.addComponent(component);
                    return true;
                }
            }
            result.withError("Expected: '" + elementName + "' Found: '" + component.getId() + "' @" + pos);
            return false;
        }
        result.withError("No valid component found @" + pos);
        return false;
    }

    public static StructureElement get(String name) {
        return elementLookup.get(name);
    }
}

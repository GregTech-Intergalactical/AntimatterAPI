package muramasa.antimatter.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StructureResult {

    private final Structure structure;
    private boolean hasError;
    private String error = "";

    //TODO compile list of positions

    public Object2ObjectMap<String, List<IComponentHandler>> components = new Object2ObjectOpenHashMap<>();
    public Object2ObjectMap<String, List<BlockState>> states = new Object2ObjectOpenHashMap<>();
    public LongList positions = new LongArrayList();
    //Used to quickly find the element in StructureCache lookup.
    private final Map<BlockPos, StructureElement> ELEMENT_LOOKUP = new Object2ObjectOpenHashMap<>();
    private final Map<BlockPos, StructureElement> TICKING = new Object2ObjectOpenHashMap<>();

    public StructureResult(Structure structure) {
        this.structure = structure;
    }

    public void withError(String error) {
        this.error = error;
        hasError = true;
    }

    public StructureResult register(BlockPos pos, StructureElement el) {
        ELEMENT_LOOKUP.put(pos, el);
        if (el.ticks()) {
            TICKING.put(pos, el);
        }
        return this;
    }

    public StructureElement get(BlockPos pos) {
        return ELEMENT_LOOKUP.get(pos);
    }

    public String getError() {
        return "[Structure Debug] " + error;
    }

    public void addComponent(String elementId, IComponentHandler component) {
        List<IComponentHandler> existing = components.get(component.getId());
        if (existing == null) components.put(component.getId(), Lists.newArrayList(component));
        else existing.add(component);
        if (!elementId.isEmpty() && !elementId.equals(component.getId())) {
            existing = components.get(elementId);
            if (existing == null) components.put(elementId, Lists.newArrayList(component));
            else existing.add(component);
        }
        positions.add(component.getTile().getPos().toLong());
    }

    public void addState(String elementId, BlockPos pos, BlockState state) {
        if (!elementId.equals(StructureElement.IGNORE.elementId)) {
            List<BlockState> existing = states.get(elementId);
            if (existing == null) states.put(elementId, Lists.newArrayList(state));
            else existing.add(state);
            positions.add(pos.toLong());
        }
    }

    public boolean evaluate() {
        if (hasError) return false;
        for (Map.Entry<String, IRequirement> entry : structure.getRequirements().entrySet()) {
            if (!entry.getValue().test(this)) {
                withError("Failed Element Requirement: " + entry.getKey());
                return false;
            }
        }
        return true;
    }

    public void build(TileEntityBasicMultiMachine<?> machine, StructureResult result) {
        Direction h = null;
        if (machine.getMachineType().allowVerticalFacing() && machine.getFacing().getAxis() == Axis.Y) {
            h = machine.getBlockState().get(BlockMachine.HORIZONTAL_FACING);
        }
        for (Iterator<Structure.Point> it = structure.forAllElements(machine.getPos(), machine.getFacing(), h); it.hasNext(); ) {
            Structure.Point point = it.next();
            int count = StructureCache.refCount(machine.getWorld(), point.pos);
            point.el.onBuild(machine, point.pos, result, count);
        }
    }

    public void remove(TileEntityBasicMultiMachine<?> machine, StructureResult result) {
        Direction h = null;
        if (machine.getMachineType().allowVerticalFacing() && machine.getFacing().getAxis() == Axis.Y) {
            h = machine.getBlockState().get(BlockMachine.HORIZONTAL_FACING);
        }
        for (Iterator<Structure.Point> it = structure.forAllElements(machine.getPos(), machine.getFacing(), h); it.hasNext(); ) {
            Structure.Point point = it.next();
            int count = StructureCache.refCount(machine.getWorld(), point.pos);
            point.el.onRemove(machine, point.pos, result, count);
        }
    }

    public void updateState(TileEntityBasicMultiMachine<?> machine, StructureResult result) {
        Direction h = null;
        if (machine.getMachineType().allowVerticalFacing() && machine.getFacing().getAxis() == Axis.Y) {
            h = machine.getBlockState().get(BlockMachine.HORIZONTAL_FACING);
        }
        MachineState proper = machine.getMachineState().getTextureState();
        for (Iterator<Structure.Point> it = structure.forAllElements(machine.getPos(), machine.getFacing(), h); it.hasNext(); ) {
            Structure.Point point = it.next();
            int count = StructureCache.refCount(machine.getWorld(), point.pos);
            point.el.onStateChange(machine, proper, point.pos, result, count);
        }
    }

    public void tick(TileEntityBasicMultiMachine<?> machine) {
        if (TICKING.isEmpty()) return;
        for (Map.Entry<BlockPos, StructureElement> entry : TICKING.entrySet()) {
            entry.getValue().tick(machine, entry.getKey());
        }
    }
}

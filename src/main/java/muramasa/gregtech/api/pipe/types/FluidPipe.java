package muramasa.gregtech.api.pipe.types;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.pipe.PipeSize;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FluidPipe extends Pipe {

    protected static HashMap<String, FluidPipe> TYPE_LOOKUP = new HashMap<>();

    public static FluidPipe Wood = new FluidPipe(Materials.Wood, 30, 350, false);
    public static FluidPipe Copper = new FluidPipe(Materials.Copper, 10, 1000, true);
    public static FluidPipe Bronze = new FluidPipe(Materials.Bronze, 20, 2000, true);
    public static FluidPipe Steel = new FluidPipe(Materials.Steel, 40, 2500, true);
    public static FluidPipe StainlessSteel = new FluidPipe(Materials.StainlessSteel, 60, 3000, true);
    public static FluidPipe Titanium = new FluidPipe(Materials.Titanium, 80, 5000, true);
    public static FluidPipe TungstenSteel = new FluidPipe(Materials.TungstenSteel, 100, 7500, true);
    public static FluidPipe Plastic = new FluidPipe(Materials.Plastic, 60, 250, true);
    public static FluidPipe Polytetrafluoroethylene = new FluidPipe(Materials.Polytetrafluoroethylene, 480, 600, true);

    public static FluidPipe HighPressure = new FluidPipe("high_pressure", Materials.Redstone.getRGB(), 7200, 1500, true);
    public static FluidPipe PlasmaContainment = new FluidPipe("plasma_containment", Materials.Glowstone.getRGB(), 240, 100000, true);

    //Wood, Ultimate, Plasma

    static {
        Wood.setCapacities(10, 10, 30, 60, 60, 60);
        Wood.setValidSizes(PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE);
        HighPressure.setCapacities(4800, 4800, 4800, 7200, 9600, 9600);
        HighPressure.setValidSizes(PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE);
        PlasmaContainment.setCapacities(240, 240, 240, 240, 240, 240);
        PlasmaContainment.setValidSizes(PipeSize.NORMAL);
    }

    private int[] capacities;
    private int heatResistance;
    private boolean gasProof;

    public FluidPipe(Material material, int baseCapacity, int heatResistance, boolean gasProof) {
        super(material);
        this.heatResistance = heatResistance;
        this.gasProof = gasProof;
        capacities = new int[] {
            baseCapacity / 6, baseCapacity / 6, baseCapacity / 3, baseCapacity, baseCapacity * 2, baseCapacity * 4
        };
        setValidSizes(PipeSize.TINY, PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE, PipeSize.HUGE);
        TYPE_LOOKUP.put("fluid_pipe_" + getName(), this);
    }

    public FluidPipe(String name, int rgb, int baseCapacity, int heatResistance, boolean gasProof) {
        this(null, baseCapacity, heatResistance, gasProof);
        this.name = name;
        this.rgb = rgb;
    }

    public void setCapacities(int... capacities) {
        this.capacities = capacities;
    }

    public int getCapacity(PipeSize size) {
        return capacities[size.ordinal()];
    }

    public int getHeatResistance() {
        return heatResistance;
    }

    public boolean isGasProof() {
        return gasProof;
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            return size.getDisplayName() + " " + getDisplayName() + " Fluid Pipe";
        }
        return getName();
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltip = new LinkedList<>();
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            tooltip.add("Fluid Capacity: " + TextFormatting.BLUE + (capacities[size.ordinal()] * 20) + "L/s");
            tooltip.add("Heat Limit: " + TextFormatting.RED + heatResistance + " K");
        }
        return tooltip;
    }

    public static void add(FluidPipe type) {
        TYPE_LOOKUP.put(type.getName(), type);
    }

    public static FluidPipe get(FluidPipe cable) {
        return TYPE_LOOKUP.get("fluid_pipe_" + cable.getName());
    }

    public static Collection<FluidPipe> getAll() {
        return TYPE_LOOKUP.values();
    }
}

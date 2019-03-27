package muramasa.gregtech.api.pipe.types;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.pipe.PipeSize;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

public class ItemPipe extends Pipe {

    protected static HashMap<String, ItemPipe> TYPE_LOOKUP = new HashMap<>();

    public static ItemPipe WroughIron = new ItemPipe(Materials.WroughtIron, 1);
    public static ItemPipe Brass = new ItemPipe(Materials.Brass, 1);
    public static ItemPipe Nickel = new ItemPipe(Materials.Nickel, 1);
    public static ItemPipe Electrum = new ItemPipe(Materials.Electrum, 2);
    public static ItemPipe Cobalt = new ItemPipe(Materials.Cobalt, 2);
    public static ItemPipe Aluminium = new ItemPipe(Materials.Aluminium, 2);
    public static ItemPipe Platinum = new ItemPipe(Materials.Platinum, 4);
    public static ItemPipe Osmium = new ItemPipe(Materials.Osmium, 8);

    public static ItemPipe PolyvinylChloride = new ItemPipe("pvc", Materials.PolyvinylChloride.getRGB(), 4);

    private int[] slots, steps;

    public ItemPipe(Material material, int baseSlots) {
        super(material);
        slots = new int[] {
            baseSlots, baseSlots, baseSlots, baseSlots, baseSlots * 2, baseSlots * 4
        };
        steps = new int[] {
          32768 / baseSlots, 32768 / baseSlots, 32768 / baseSlots, 32768 / baseSlots, 16384 / baseSlots, 8192 / baseSlots
        };
        setValidSizes(PipeSize.NORMAL, PipeSize.LARGE, PipeSize.HUGE);
        TYPE_LOOKUP.put("item_pipe_" + getName(), this);
    }

    public ItemPipe(String name, int rgb, int baseSlots) {
        this(null, baseSlots);
        this.name = name;
        this.rgb = rgb;
    }

    public int getSlotCount(PipeSize size) {
        return slots[size.ordinal()];
    }

    public int getStepSize(PipeSize size, boolean restrictive) {
        return restrictive ? steps[size.ordinal()] * 1000 : steps[size.ordinal()];
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            boolean restrictive = stack.getTagCompound().getBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE);
            String name = (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + (restrictive ? "Restrictive " : "") + getDisplayName() + " Item Pipe";
            return name;
        }
        return getName();
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        if (stack.hasTagCompound()) {
            List<String> tooltip = new LinkedList<>();
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            tooltip.add("Item Capacity: " + TextFormatting.BLUE + getSlotCount(size) + " Stacks/s");
            tooltip.add("Routing Value: " + TextFormatting.YELLOW + getStepSize(size, stack.getTagCompound().getBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE)));
        }
        return Collections.emptyList();
    }

    public static Collection<ItemPipe> getAll() {
        return TYPE_LOOKUP.values();
    }
}

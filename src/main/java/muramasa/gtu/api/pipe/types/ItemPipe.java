package muramasa.gtu.api.pipe.types;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.pipe.PipeSize;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

public class ItemPipe extends Pipe {

    protected static HashMap<String, ItemPipe> TYPE_LOOKUP = new HashMap<>();

    public static ItemPipe Cupronickel = new ItemPipe(Materials.Cupronickel, 1);
    public static ItemPipe CobaltBrass = new ItemPipe(Materials.CobaltBrass, 1);
    public static ItemPipe Brass = new ItemPipe(Materials.Brass, 1);
    public static ItemPipe Electrum = new ItemPipe(Materials.Electrum, 2);
    public static ItemPipe RoseGold = new ItemPipe(Materials.RoseGold, 2);
    public static ItemPipe SterlingSilver = new ItemPipe(Materials.SterlingSilver, 2);
    public static ItemPipe Platinum = new ItemPipe(Materials.Platinum, 4);
    public static ItemPipe Ultimet = new ItemPipe(Materials.Ultimet, 4);
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
        TYPE_LOOKUP.put("item_pipe_" + getId(), this);
    }

    public ItemPipe(String name, int rgb, int baseSlots) {
        this(null, baseSlots);
        this.id = name;
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
        return getId();
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

package muramasa.gtu.api.pipe.types;

import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.pipe.PipeSize;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

public class Cable extends Pipe {

    protected static HashMap<String, Cable> TYPE_LOOKUP = new HashMap<>();

    private static boolean HC = Ref.HARDCORE_CABLES;

    public static Cable RedAlloy = new Cable(Materials.RedAlloy, 0, 1, 1, Tier.ULV);

    public static Cable Cobalt = new Cable(Materials.Cobalt, 2, 4, 2, Tier.LV);
    public static Cable Lead = new Cable(Materials.Lead, 2, 4, 2, Tier.LV);
    public static Cable Tin = new Cable(Materials.Tin, 1, 2, 1, Tier.LV);
    public static Cable Zinc = new Cable(Materials.Zinc, 1, 2, 1, Tier.LV);
    public static Cable SolderingAlloy = new Cable(Materials.SolderingAlloy, 1, 2, 1, Tier.LV);

    public static Cable Iron = new Cable(Materials.Iron, HC ? 3 : 4, HC ? 6 : 8, 2, Tier.MV);
    public static Cable Nickel = new Cable(Materials.Nickel, HC ? 3 : 5, HC ? 6 : 10, 3, Tier.MV);
    public static Cable Cupronickel = new Cable(Materials.Cupronickel, HC ? 3 : 4, HC ? 6 : 8, 2, Tier.MV);
    public static Cable Copper = new Cable(Materials.Copper, HC ? 2 : 3, HC ? 4 : 6, 1, Tier.MV);
    public static Cable AnnealedCopper = new Cable(Materials.AnnealedCopper, HC ? 1 : 2, HC ? 2 : 4, 1, Tier.MV);

    public static Cable Kanthal = new Cable(Materials.Kanthal, HC ? 3 : 8, HC ? 6 : 16, 4, Tier.HV);
    public static Cable Gold = new Cable(Materials.Gold, HC ? 2 : 6, HC ? 4 : 12, 3, Tier.HV);
    public static Cable Electrum = new Cable(Materials.Electrum, HC ? 2 : 5, HC ? 4 : 10, 2, Tier.HV);
    public static Cable Silver = new Cable(Materials.Silver, HC ? 1 : 4, HC ? 2 : 8, 1, Tier.HV);

    public static Cable Nichrome = new Cable(Materials.Nichrome, HC ? 4 : 32, HC ? 8 : 64, 3, Tier.EV);
    public static Cable Steel = new Cable(Materials.Steel, HC ? 2 : 16, HC ? 4 : 32, 2, Tier.EV);
    public static Cable BlackSteel = new Cable(Materials.BlackSteel, HC ? 2 : 14, HC ? 4 : 28, 3, Tier.EV);
    public static Cable Titanium = new Cable(Materials.Titanium, HC ? 2 : 12, HC ? 4 : 24, 4, Tier.EV);
    public static Cable Aluminium = new Cable(Materials.Aluminium, HC ? 1 : 8, HC ? 2 : 16, 1, Tier.EV);

    public static Cable Graphene = new Cable(Materials.Graphene, HC ? 1 : 16, HC ? 2 : 32, 1, Tier.IV);
    public static Cable Osmium = new Cable(Materials.Osmium, HC ? 2 : 32, HC ? 4 : 64, 4, Tier.IV);
    public static Cable Platinum = new Cable(Materials.Platinum, HC ? 1 : 16, HC ? 2 : 32, 2, Tier.IV);
    public static Cable TungstenSteel = new Cable(Materials.TungstenSteel, HC ? 1 : 14, HC ? 4 : 28, 3, Tier.IV);
    public static Cable Tungsten = new Cable(Materials.Tungsten, HC ? 2 : 12, HC ? 4 : 24, 1, Tier.IV);

    public static Cable HSSG = new Cable(Materials.HSSG, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV);
    public static Cable NiobiumTitanium = new Cable(Materials.NiobiumTitanium, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV);
    public static Cable VanadiumGallium = new Cable(Materials.VanadiumGallium, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV);
    public static Cable YttriumBariumCuprate = new Cable(Materials.YttriumBariumCuprate, HC ? 4 : 256, HC ? 8 : 512, 4, Tier.LUV);

    public static Cable Naquadah = new Cable(Materials.Naquadah, HC ? 2 : 64, HC ? 4 : 128, 2, Tier.ZPM);

    public static Cable NaquadahAlloy = new Cable(Materials.NaquadahAlloy, HC ? 4 : 64, HC ? 8 : 128, 2, Tier.ZPM);
    public static Cable Duranium = new Cable(Materials.Duranium, HC ? 8 : 64, HC ? 16 : 128, 1, Tier.ZPM);

//    public static Cable Superconductor = new Cable("superconductor", 1, 1, 4, Tier.MAX);

    private int loss, lossInsulated;
    private int[] amps;
    private Tier tier;

    public Cable(Material material, int loss, int lossInsulated, int baseAmps, Tier tier) {
        super(material);
        this.loss = loss;
        this.lossInsulated = lossInsulated;
        this.tier = tier;
        amps = new int[] {
            baseAmps, baseAmps * 2, baseAmps * 4, baseAmps * 8, baseAmps * 12, baseAmps * 16
        };
        TYPE_LOOKUP.put("cable_" + material.getName(), this);
    }

    public int getLoss() {
        return loss;
    }

    public int getLossInsulated() {
        return lossInsulated;
    }

    public int getAmps(PipeSize size) {
        return amps[size.ordinal()];
    }

    public Tier getTier() {
        return tier;
    }

    public long getVoltage() {
        return tier.getVoltage();
    }

    @Override
    public String getDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            PipeSize size = PipeSize.VALUES[compound.getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            if (compound.getBoolean(Ref.KEY_CABLE_STACK_INSULATED)) {
                size = PipeSize.VALUES[Math.max(size.ordinal() - 1, 0)];
                return  size.getCableThickness() + "x " + getDisplayName() + " Cable";
            } else {
                return size.getCableThickness() + "x " + getDisplayName() + " Wire";
            }
        }
        return stack.getUnlocalizedName();
    }

    @Override
    public List<String> getTooltip(ItemStack stack) {
        List<String> tooltip = new LinkedList<>();
        if (stack.hasTagCompound()) {
            PipeSize size = PipeSize.VALUES[stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE)];
            tooltip.add("Max Voltage: " + TextFormatting.GREEN + getVoltage() + " (" + getTier().getName().toUpperCase(Locale.ENGLISH) + ")");
            tooltip.add("Max Amperage: " + TextFormatting.YELLOW + getAmps(size));
            boolean insulated = stack.getTagCompound().getBoolean(Ref.KEY_CABLE_STACK_INSULATED);
            tooltip.add("Loss/Meter/Ampere: " + TextFormatting.RED + (insulated ? getLossInsulated() : getLoss()) + TextFormatting.GRAY + " EU-Volt");
        }
        return tooltip;
    }

    public static Collection<Cable> getAll() {
        return TYPE_LOOKUP.values();
    }
}

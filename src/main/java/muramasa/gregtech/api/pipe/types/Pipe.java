package muramasa.gregtech.api.pipe.types;

import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.pipe.PipeSize;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pipe {

    private List<PipeSize> validSizes;
    private Material material;
    private String name;
    private int rgb;

    public Pipe(Material material) {
        this.material = material;
    }

    public Pipe(String name, int rgb) {
        this.name = name;
        this.rgb = rgb;
    }

    public Collection<PipeSize> getValidSizes() {
        return validSizes != null ? validSizes : Arrays.asList(PipeSize.VALUES);
    }

    public void setValidSizes(PipeSize... validSizes) {
        this.validSizes = Arrays.asList(validSizes);
    }

    public String getName() {
        return material != null ? material.getName() : name;
    }

    public String getDisplayName() {
        return material != null ? material.getDisplayName() : name;
    }

    public int getRGB() {
        return material != null ? material.getRGB() : rgb;
    }

    public String getDisplayName(ItemStack stack) {
        return getName();
    }

    public List<String> getTooltip(ItemStack stack) {
        return Collections.emptyList();
    }
}

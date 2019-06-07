package muramasa.gtu.api.pipe.types;

import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.pipe.PipeSize;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class Pipe {

    protected PipeSize[] validSizes;
    protected Material material;
    protected String id;
    protected int rgb;

    public Pipe(Material material) {
        this.material = material;
    }

    public Pipe(String id, int rgb) {
        this.id = id;
        this.rgb = rgb;
    }

    public PipeSize[] getValidSizes() {
        return validSizes != null ? validSizes : PipeSize.VALUES;
    }

    public void setValidSizes(PipeSize... validSizes) {
        this.validSizes = validSizes;
    }


    //TODO GTObject?
    public String getId() {
        return material != null ? material.getId() : id;
    }

    public String getDisplayName() {
        return material != null ? material.getDisplayName() : id;
    }

    public int getRGB() {
        return material != null ? material.getRGB() : rgb;
    }

    public String getDisplayName(ItemStack stack) {
        return getId();
    }

    public List<String> getTooltip(ItemStack stack) {
        return Collections.emptyList();
    }
}

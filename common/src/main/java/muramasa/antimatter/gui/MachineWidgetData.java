package muramasa.antimatter.gui;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.util.int2;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class MachineWidgetData {
    public BarDir dir = BarDir.LEFT;
    public boolean barFill = true;
    protected int2 progressSize = new int2(20, 18), progressPos = new int2(72, 18);
    protected int2 ioPos = new int2(7, 62), machineStatePos = new int2(83, 43), machineStateSize = new int2(10, 11);
    protected Map<String, ResourceLocation> machineStateTextures = new Object2ObjectOpenHashMap<>();
    protected Map<String, ResourceLocation> progressTextures = new Object2ObjectOpenHashMap<>();

    private final GuiData parent;
    public MachineWidgetData(GuiData parent){
        this.parent = parent;
        this.machineStateTextures.put("", new ResourceLocation(Ref.ID, "textures/gui/button/machine_state.png"));
        this.progressTextures.put("", new ResourceLocation(Ref.ID, "textures/gui/progress_bars/default.png"));
    }

    public MachineWidgetData setProgressLocation(String name){
        this.progressTextures.put("", new ResourceLocation(parent.loc.getNamespace(), "textures/gui/progress_bars/" + name + ".png"));
        return this;
    }

    public MachineWidgetData setProgressLocation(Tier tier, String name){
        this.progressTextures.put(tier.getId(), new ResourceLocation(parent.loc.getNamespace(), "textures/gui/progress_bars/" + name + ".png"));
        return this;
    }

    public MachineWidgetData setProgressSize(int width, int height){
        this.progressSize = new int2(width, height);
        return this;
    }

    public MachineWidgetData setProgressPos(int x, int y){
        this.progressPos = new int2(x, y);
        return this;
    }

    public MachineWidgetData setIoPos(int x, int y){
        this.ioPos = new int2(x, y);
        return this;
    }

    public MachineWidgetData setMachineStatePos(int x, int y){
        this.machineStatePos = new int2(x, y);
        return this;
    }

    public MachineWidgetData setMachineStateSize(int width, int height){
        this.machineStateSize = new int2(width, height);
        return this;
    }

    public MachineWidgetData setMachineStateLocation(String name){
        this.progressTextures.put("", new ResourceLocation(parent.loc.getNamespace(), "textures/gui/button/" + name + ".png"));
        return this;
    }

    public MachineWidgetData setMachineStateLocation(Tier tier, String name){
        this.progressTextures.put(tier.getId(), new ResourceLocation(parent.loc.getNamespace(), "textures/gui/button/" + name + ".png"));
        return this;
    }

    public MachineWidgetData setDir(BarDir dir) {
        this.dir = dir;
        return this;
    }

    public MachineWidgetData setBarFill(boolean barFill) {
        this.barFill = barFill;
        return this;
    }

    public int2 getProgressSize() {
        return progressSize;
    }

    public ResourceLocation getProgressTexture(Tier tier) {
        if (tier != null && progressTextures.containsKey(tier.getId())) return progressTextures.get(tier.getId());
        return progressTextures.get("");
    }

    public int2 getProgressPos() {
        return progressPos;
    }

    public BarDir getDir() {
        return dir;
    }

    public GuiData getParent() {
        return parent;
    }

    public boolean doesBarFill() {
        return barFill;
    }

    public int2 getMachineStatePos() {
        return machineStatePos;
    }

    public int2 getMachineStateSize() {
        return machineStateSize;
    }

    public ResourceLocation getMachineStateTexture(Tier tier) {
        if (tier != null && machineStateTextures.containsKey(tier.getId())) return machineStateTextures.get(tier.getId());
        return machineStateTextures.get("");
    }

    public int2 getIoPos() {
        return ioPos;
    }
}

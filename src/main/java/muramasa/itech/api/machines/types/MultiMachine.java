package muramasa.itech.api.machines.types;

import muramasa.itech.ITech;
import muramasa.itech.api.behaviour.BehaviourMultiMachine;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.objects.MachineStack;
import muramasa.itech.api.machines.objects.Tier;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.loaders.ContentLoader;
import net.minecraft.util.ResourceLocation;

public class MultiMachine extends Machine {

    private BehaviourMultiMachine behaviour;
    private StructurePattern pattern;
    private ResourceLocation baseTexture, overlayTexture;

    public MultiMachine(String name, BehaviourMultiMachine behaviour) {
        super(name, true);
        this.behaviour = behaviour;
        baseTexture = new ResourceLocation(ITech.MODID + ":blocks/machines/base/" + name);
        overlayTexture = new ResourceLocation(ITech.MODID + ":blocks/machines/overlays/" + name);
        MachineList.multiStackLookup.put(name + Tier.MULTI.getName(), new MachineStack(this, Tier.MULTI, ContentLoader.blockMultiMachines));
        MachineList.multiTypeLookup.put(name, this);
    }

    public MultiMachine addPattern(StructurePattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public BehaviourMultiMachine getBehaviour() {
        return behaviour;
    }

    public StructurePattern getPattern() {
        return pattern;
    }

    public ResourceLocation getBaseTexture() {
        return baseTexture;
    }

    @Override
    public ResourceLocation getOverlayTexture() {
        return overlayTexture;
    }
}

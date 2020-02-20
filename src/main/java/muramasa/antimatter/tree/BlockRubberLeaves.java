package muramasa.antimatter.tree;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockRubberLeaves extends LeavesBlock implements IAntimatterObject {
    public BlockRubberLeaves() {
        super(Block.Properties.create(Material.LEAVES).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid());
        setRegistryName(Ref.ID + ":" + getId());
        AntimatterAPI.register(BlockRubberLeaves.class,this);
    }

    @Override
    public String getId() {
        return "rubber_leaves";
    }
}

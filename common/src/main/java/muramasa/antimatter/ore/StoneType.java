package muramasa.antimatter.ore;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class StoneType implements ISharedAntimatterObject, IRegistryEntryProvider {

    private final String domain, id;
    //private int harvestLevel;
    private boolean gravity, requiresTool;
    public boolean generateBlock = false;
    private final Material material;
    private Texture[] textures;
    private final SoundType soundType;
    private BlockState state;
    private Supplier<BlockState> stateSupplier;
    private int harvestLevel, fallingDustColor;
    private float hardness, resistence;
    private TagKey<Block> toolType;
    private net.minecraft.world.level.material.Material blockMaterial;

    public StoneType(String domain, String id, Material material, Texture texture, SoundType soundType, boolean generateBlock) {
        this.domain = domain;
        this.id = id;
        this.material = material;
        this.textures = new Texture[]{texture};
        this.soundType = soundType;
        this.generateBlock = generateBlock;
        this.gravity = false;
        this.harvestLevel = 0;
        this.requiresTool = true;
        this.hardness = 1.5F;
        this.resistence = 6.0F;
        this.toolType = BlockTags.MINEABLE_WITH_PICKAXE;
        this.fallingDustColor = -16777216;
        this.blockMaterial = net.minecraft.world.level.material.Material.STONE;
    }

    public StoneType setHarvestLevel(int harvestLevel) {
        this.harvestLevel = harvestLevel;
        return this;
    }

    public StoneType setHardnessAndResistance(float hardnessAndResistence) {
        this.hardness = hardnessAndResistence;
        this.resistence = hardnessAndResistence;
        return this;
    }

    public StoneType setHardnessAndResistance(float hardness, float resistence) {
        this.hardness = hardness;
        this.resistence = resistence;
        return this;
    }

    public StoneType setGravity(boolean gravity) {
        this.gravity = gravity;
        return this;
    }

    public StoneType setRequiresTool(boolean requiresTool) {
        this.requiresTool = requiresTool;
        return this;
    }

    public StoneType setFallingDustColor(int fallingDustColor) {
        this.fallingDustColor = fallingDustColor;
        return this;
    }

    public StoneType setType(TagKey<Block> type) {
        this.toolType = type;
        return this;
    }

    public StoneType setBlockMaterial(net.minecraft.world.level.material.Material material) {
        this.blockMaterial = material;
        return this;
    }

    public StoneType setTextures(Texture... textures){
        this.textures = textures;
        return this;
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {
        if (generateBlock && registry == RegistryType.BLOCKS) setState(new BlockStone(this));
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public BlockState getState() {
        return state;
    }

    public Texture getTexture() {
        return textures[0];
    }

    public Texture[] getTextures() {
        return textures;
    }

    public SoundType getSoundType() {
        return soundType;
    }

    public TagKey<Block> getToolType() {
        return toolType;
    }

    public int getFallingDustColor() {
        return fallingDustColor;
    }

    public net.minecraft.world.level.material.Material getBlockMaterial() {
        return blockMaterial;
    }

    public boolean doesGenerateBlock() {
        return generateBlock;
    }

    public boolean doesRequireTool() {
        return requiresTool;
    }

    public StoneType setState(Block block) {
        this.state = block.defaultBlockState();
        return this;
    }

    public StoneType setState(BlockState blockState) {
        this.state = blockState;
        return this;
    }

    public StoneType setStateSupplier(Supplier<BlockState> blockState) {
        this.stateSupplier = blockState;
        return this;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistence() {
        return resistence;
    }

    public boolean getGravity() {
        return gravity;
    }

    public static Collection<Collection<Texture>> getAllTextures() {
        List<Collection<Texture>> textures = new ObjectArrayList<>();
        for (StoneType type : getAllGeneratingBlock()) {
            textures.add(Arrays.asList(type.getTextures()));
        }
        return textures;
    }

    //TODO collection
    public static StoneType[] getAllGeneratingBlock() {
        return AntimatterAPI.all(StoneType.class).stream().filter(s -> s.generateBlock).toArray(StoneType[]::new);
    }

    public void initSuppliedState() {
        if (state == null && stateSupplier != null) {
            state = stateSupplier.get();
        }
    }

    public static StoneType get(String id) {
        return AntimatterAPI.get(StoneType.class, id);
    }
}

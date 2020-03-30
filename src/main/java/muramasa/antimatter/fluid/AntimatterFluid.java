package muramasa.antimatter.fluid;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;

import static net.minecraftforge.fluids.ForgeFlowingFluid.*;

/**
 * AntimatterFluid is an object that includes all essential information of what a normal fluid would compose of in Minecraft
 * Source + Flowing fluid instances, with a FlowingFluidBlock that handles the in-world block form of them.
 * Item instance is also provided, usually BucketItem or its derivatives. But is generified to an Item instance: {@link net.minecraftforge.fluids.ForgeFlowingFluid#getFilledBucket}
 *
 * TODO: BlockState jsons?...
 * TODO: See through fluids:
 * @see muramasa.antimatter.proxy.ClientHandler#setup
 * TODO: Make BucketItem default models: (will need something similar when we get cells)
 * @see net.minecraftforge.client.model.DynamicBucketModel
 */
public class AntimatterFluid implements IAntimatterObject {

    protected static final ResourceLocation OVERLAY = new ResourceLocation("block/water_overlay");

    private final String domain, id;

    protected Properties fluidProperties;
    protected Source source;
    protected Flowing flowing;
    protected Block.Properties blockProperties;
    protected FluidAttributes attributes;
    protected FlowingFluidBlock fluidBlock;
    protected Item containerItem;

    public AntimatterFluid(String domain, String id, FluidAttributes.Builder builder, Block.Properties blockProperties) {
        this.domain = domain;
        this.id = id;
        this.fluidProperties = new Properties(this::getFluid, this::getFlowingFluid, builder).bucket(this::getContainerItem).block(this::getFluidBlock);
        this.source = new Source(this.fluidProperties);
        this.flowing = new Flowing(this.fluidProperties);
        this.blockProperties = blockProperties;
        this.attributes = builder.translationKey("block." + domain + ".liquid." + id).build(this.source);
        this.fluidBlock = new FlowingFluidBlock(this::getFluid, blockProperties);
        this.fluidBlock.setRegistryName(domain, id);
        this.source.setRegistryName(domain, id);
        this.flowing.setRegistryName(domain, "flowing_" + id);
        this.containerItem = new BucketItem(this::getFluid, new Item.Properties().maxStackSize(1).containerItem(Items.BUCKET).group(ItemGroup.MISC));
        this.containerItem.setRegistryName(domain, id + "_bucket");
        AntimatterAPI.register(AntimatterFluid.class, this);
    }

    public AntimatterFluid(String domain, String id) {
        this(domain, id, getDefaultAttributesBuilder(), getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, FluidAttributes.Builder builder) {
        this(domain, id, builder, getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, ResourceLocation stillLoc, ResourceLocation flowLoc) {
        this(domain, id, FluidAttributes.builder(stillLoc, flowLoc), getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, Block.Properties properties) {
        this(domain, id, getDefaultAttributesBuilder(), properties);
    }

    public AntimatterFluid source(Source source) {
        this.source = source;
        return this;
    }

    public AntimatterFluid flowing(Flowing flowing) {
        this.flowing = flowing;
        return this;
    }

    public AntimatterFluid flowingBlock(FlowingFluidBlock fluidBlock) {
        this.fluidBlock = fluidBlock;
        return this;
    }

    public AntimatterFluid containerItem(Item item) {
        this.containerItem = item;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public Properties getFluidProperties() {
        return fluidProperties;
    }

    public Block.Properties getBlockProperties() {
        return blockProperties;
    }

    public FluidAttributes getAttributes() {
        return attributes;
    }

    public Source getFluid() {
        return source;
    }

    public Flowing getFlowingFluid() {
        return flowing;
    }

    public FlowingFluidBlock getFluidBlock() {
        return fluidBlock;
    }

    public Item getContainerItem() {
        return containerItem;
    }

    protected static Block.Properties getDefaultBlockProperties() {
        return Block.Properties.create(Material.WATER).hardnessAndResistance(100.0F).noDrops();
    }

    protected static FluidAttributes.Builder getDefaultAttributesBuilder() {
        // return FluidAttributes.builder(new ResourceLocation(Ref.ID, "block/liquid/still"), new ResourceLocation(Ref.ID, "block/liquid/flow")).overlay(OVERLAY);
        return FluidAttributes.builder(new ResourceLocation("block/water_still"), new ResourceLocation("block/water_flow")).overlay(OVERLAY);
    }

}

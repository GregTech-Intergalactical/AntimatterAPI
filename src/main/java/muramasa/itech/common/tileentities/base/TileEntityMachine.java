package muramasa.itech.common.tileentities.base;

import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.api.util.Utils;
import muramasa.itech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityMachine extends TileEntityTickable {

    /** Data from NBT **/
    private String typeFromNBT = "", tierFromNBT = "";
    private int facing = 2;

    public void init(String type, String tier) {
        if (type.isEmpty() || type.isEmpty()) {
            type = MachineList.ALLOYSMELTER.getName();
            tier = Tier.LV.getName();
        }
        typeFromNBT = type;
        tierFromNBT = tier;
    }

    @Override
    public void onFirstTick() { //Using first tick as this fires on both client & server, unlike onLoad
        if (!getClass().getName().equals(getMachineType().getTileClass().getName())) {
            try {
                world.setTileEntity(pos, (TileEntity) MachineList.get(typeFromNBT).getTileClass().newInstance());
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof TileEntityMachine) {
                    ((TileEntityMachine) tile).init(typeFromNBT, tierFromNBT);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            init(typeFromNBT, tierFromNBT);
        }
        if (facing > 2) {
            rotate(EnumFacing.VALUES[facing]);
        }
    }

    /** Helpers **/
    public boolean hasFlag(MachineFlag flag) {
        return Utils.hasFlag(getMachineType().getMask(), flag.getBit());
    }

    public void rotate(EnumFacing side) { //Rotate the front to face a given direction
        if (side.getAxis() != EnumFacing.Axis.Y) {
            setState(getState().withProperty(ITechProperties.FACING, side));
        }
    }

    public EnumFacing getFacing() {
        return getState().getValue(ITechProperties.FACING);
    }

    /** Events **/
    public void onContentsChanged(int slot) {
        //NOOP
    }

    /** Getters **/
    public Machine getMachineType() {
        return MachineList.get(getType());
    }

    public String getType() {
        return typeFromNBT;
    }

    public String getTier() {
        return tierFromNBT;
    }

    public int getCurProgress() {
        return 0;
    }

    public int getMaxProgress() {
        return 0;
    }

    public float getClientProgress() {
        return 0;
    }

    /** Setters **/
    public void setClientProgress(float newProgress) {
        //NOOP
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_TYPE) && compound.hasKey(Ref.KEY_MACHINE_TILE_TIER)) {
            typeFromNBT = compound.getString(Ref.KEY_MACHINE_TILE_TYPE);
            tierFromNBT = compound.getString(Ref.KEY_MACHINE_TILE_TIER);
        }
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_FACING)) {
            facing = compound.getInteger(Ref.KEY_MACHINE_TILE_FACING);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound); //TODO add tile data tag
        compound.setString(Ref.KEY_MACHINE_TILE_TYPE, getType());
        compound.setString(Ref.KEY_MACHINE_TILE_TIER, getTier());
        compound.setInteger(Ref.KEY_MACHINE_TILE_FACING, getState().getValue(ITechProperties.FACING).getIndex());
        return compound;
    }
}

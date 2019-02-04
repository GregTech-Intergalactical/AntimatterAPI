package muramasa.itech.common.tileentities.base;

import muramasa.itech.api.enums.MachineState;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityMachine extends TileEntityTickable {

    //TODO remove onFirstTick by using markForNBTSync

    /** Data from NBT **/
    private String typeFromNBT = "", tierFromNBT = "";
    private int typeId, tierId, facing, tint = -1;
    private MachineState machineState = MachineState.IDLE;

    public void init(String type, String tier, int facing) {
        if (type.isEmpty() || type.isEmpty()) {
            type = MachineList.ALLOY_SMELTER.getName();
            tier = Tier.LV.getName();
        }
        typeFromNBT = type;
        tierFromNBT = tier;
        typeId = getMachineType().getInternalId();
        tierId = Tier.get(tierFromNBT).getId();
        this.facing = facing;
    }

    @Override
    public void onFirstTick() { //Using first tick as this fires on both client & server, unlike onLoad
        if (!getClass().getName().equals(getMachineType().getTileClass().getName())) {
            try {
                world.setTileEntity(pos, (TileEntity) MachineList.get(typeFromNBT).getTileClass().newInstance());
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof TileEntityMachine) {
                    ((TileEntityMachine) tile).init(typeFromNBT, tierFromNBT, facing);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            init(typeFromNBT, tierFromNBT, facing);
        }
    }

    /** Events **/
    public void onContentsChanged(int slot) {
        //NOOP
    }

    /** Getters **/
    public Machine getMachineType() {
        return MachineList.get(typeFromNBT);
    }

    public String getType() {
        return typeFromNBT;
    }

    public String getTier() {
        return tierFromNBT;
    }

    public int getTypeId() {
        return typeId;
    }

    public int getTierId() {
        return tierId;
    }

    public int getFacing() {
        return facing;
    }

    public EnumFacing getEnumFacing() {
        return EnumFacing.VALUES[facing + 2];
    }

    public MachineState getMachineState() {
        return machineState;
    }

    public int getTint() {
        return tint;
    }

    public int getTextureId() {
        return 0;
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
    public void setFacing(EnumFacing side) {
        if (side.getAxis() != EnumFacing.Axis.Y) {
            setFacing(side.getIndex() - 2);
        }
    }

    public void setFacing(int newFacing) { //Rotate the front to face a given direction
        facing = newFacing;
        markForRenderUpdate();
    }

    public void setTint(int newTint) {
        tint = newTint;
    }

    public void setTextureId(int newId) {
        //NOOP
    }

    public void setMachineState(MachineState newState) {
        machineState = newState;
    }

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
        compound.setString(Ref.KEY_MACHINE_TILE_TYPE, typeFromNBT);
        compound.setString(Ref.KEY_MACHINE_TILE_TIER, tierFromNBT);
        compound.setInteger(Ref.KEY_MACHINE_TILE_FACING, facing);
        return compound;
    }
}

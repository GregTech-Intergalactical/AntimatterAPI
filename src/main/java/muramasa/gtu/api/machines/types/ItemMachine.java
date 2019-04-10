package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.tileentities.TileEntityItemMachine;

import static muramasa.gtu.api.machines.MachineFlag.ITEM;

public class ItemMachine extends BasicMachine {

    public ItemMachine(String name) {
        super(name, TileEntityItemMachine.class);
        addFlags(ITEM);
    }

    public ItemMachine(String name, Class tileClass) {
        this(name);
        setTileClass(tileClass);
    }
}

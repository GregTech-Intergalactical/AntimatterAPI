package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityMachine;

import static muramasa.antimatter.machine.MachineFlag.*;

public class SteamMachine extends Machine<SteamMachine> {

    public SteamMachine(String domain, String id, Object... data) {
        super(domain, id, data);
        setTile(() -> new TileEntityMachine(this));
        addFlags(BASIC, STEAM, COVERABLE, CONFIGURABLE);
        setGUI(Data.STEAM_MENU_HANDLER);
    }
}
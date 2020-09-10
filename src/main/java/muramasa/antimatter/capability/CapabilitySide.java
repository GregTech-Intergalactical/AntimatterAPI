package muramasa.antimatter.capability;

public enum CapabilitySide {
    SERVER, // Only Server-Side
    CLIENT, // Only Client-Side
    BOTH, // Client and Server
    SYNC // Client and Server, but Client will use the same tag for initializing as used on server
}

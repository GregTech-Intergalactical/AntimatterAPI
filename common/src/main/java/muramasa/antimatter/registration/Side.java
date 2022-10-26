package muramasa.antimatter.registration;

public enum Side {
    CLIENT,
    SERVER;

    public boolean isServer(){
        return this == SERVER;
    }

    public boolean isClient(){
        return this == CLIENT;
    }
}

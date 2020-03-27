package muramasa.antimatter.registration;

public enum RegistrationEvent {

    DATA_INIT, //When the initial data should be initialized (Material etc)
    DATA_POST_INIT, //When the initial data can be altered (Material tags from other addons etc)
    REGISTRY_BUILD, //When Objects can be created from the initial data (MaterialItem etc)
    READY, //When all data should be ready to use
    WORLDGEN_INIT, //When WorldGen objects need to initialize
    RECIPE
}

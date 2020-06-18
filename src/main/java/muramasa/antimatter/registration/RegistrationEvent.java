package muramasa.antimatter.registration;

//TODO convert to RegistryEvent to allow mod ordering?
public enum RegistrationEvent {

    DATA_INIT, //When the initial data should be initialized (Material etc)
    DATA_POST_INIT, //When the initial data can be altered (Material tags from other addons etc)
    DATA_READY, //When all data should be ready to use
    WORLDGEN_INIT, //When WorldGen objects need to initialize
}

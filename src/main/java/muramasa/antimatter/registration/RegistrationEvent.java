package muramasa.antimatter.registration;

public enum RegistrationEvent {

    DATA_INIT, //When the initial data should be initialized (Material etc)
    DATA_BUILD, //When Objects can be created from the initial data (MaterialItem etc)
    DATA_READY, //When all data should be ready to use
    WORLDGEN,
    RECIPE
}

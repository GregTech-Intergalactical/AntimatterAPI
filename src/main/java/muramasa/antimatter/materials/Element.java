package muramasa.antimatter.materials;

import muramasa.antimatter.registration.IAntimatterObject;

import java.util.Locale;

public enum Element implements IAntimatterObject {

    H(1, 0, 0, -1, null, "Hydrogen", false),
    D(1, 1, 0, -1, "H", "Deuterium", true),
    T(1, 2, 0, -1, "D", "Tritium", true),
    He(2, 2, 0, -1, null, "Helium", false),
    He_3(2, 1, 0, -1, "H&D", "Helium-3", true),
    Li(3, 4, 0, -1, null, "Lithium", false),
    Be(4, 5, 0, -1, null, "Beryllium", false),
    B(5, 5, 0, -1, null, "Boron", false),
    C(6, 6, 0, -1, null, "Carbon", false),
    N(7, 7, 0, -1, null, "Nitrogen", false),
    O(8, 8, 0, -1, null, "Oxygen", false),
    F(9, 9, 0, -1, null, "Fluorine", false),
    Ne(10, 10, 0, -1, null, "Neon", false),
    Na(11, 11, 0, -1, null, "Sodium", false),
    Mg(12, 12, 0, -1, null, "Magnesium", false),
    Al(13, 13, 0, -1, null, "Aluminium", false),
    Si(14, 14, 0, -1, null, "Silicon", false),
    P(15, 15, 0, -1, null, "Phosphorus", false),
    S(16, 16, 0, -1, null, "Sulfur", false),
    Cl(17, 18, 0, -1, null, "Chlorine", false),
    Ar(18, 22, 0, -1, null, "Argon", false),
    K(19, 20, 0, -1, null, "Potassium", false),
    Ca(20, 20, 0, -1, null, "Calcium", false),
    Sc(21, 24, 0, -1, null, "Scandium", false),
    Ti(22, 26, 0, -1, null, "Titanium", false),
    V(23, 28, 0, -1, null, "Vanadium", false),
    Cr(24, 28, 0, -1, null, "Chrome", false),
    Mn(25, 30, 0, -1, null, "Manganese", false),
    Fe(26, 30, 0, -1, null, "Iron", false),
    Co(27, 32, 0, -1, null, "Cobalt", false),
    Ni(28, 30, 0, -1, null, "Nickel", false),
    Cu(29, 34, 0, -1, null, "Copper", false),
    Zn(30, 35, 0, -1, null, "Zinc", false),
    Ga(31, 39, 0, -1, null, "Gallium", false),
    Ge(32, 40, 0, -1, null, "Germanium", false),
    As(33, 42, 0, -1, null, "Arsenic", false),
    Se(34, 45, 0, -1, null, "Selenium", false),
    Br(35, 45, 0, -1, null, "Bromine", false),
    Kr(36, 48, 0, -1, null, "Krypton", false),
    Rb(37, 48, 0, -1, null, "Rubidium", false),
    Sr(38, 49, 0, -1, null, "Strontium", false),
    Y(39, 50, 0, -1, null, "Yttrium", false),
    Zr(40, 51, 0, -1, null, "Zirconium", false),
    Nb(41, 53, 0, -1, null, "Niobium", false),
    Mo(42, 53, 0, -1, null, "Molybdenum", false),
    Tc(43, 55, 0, -1, null, "Technetium", false),
    Ru(44, 57, 0, -1, null, "Ruthenium", false),
    Rh(45, 58, 0, -1, null, "Rhodium", false),
    Pd(46, 60, 0, -1, null, "Palladium", false),
    Ag(47, 60, 0, -1, null, "Silver", false),
    Cd(48, 64, 0, -1, null, "Cadmium", false),
    In(49, 65, 0, -1, null, "Indium", false),
    Sn(50, 68, 0, -1, null, "Tin", false),
    Sb(51, 70, 0, -1, null, "Antimony", false),
    Te(52, 75, 0, -1, null, "Tellurium", false),
    I(53, 74, 0, -1, null, "Iodine", false),
    Xe(54, 77, 0, -1, null, "Xenon", false),
    Cs(55, 77, 0, -1, null, "Caesium", false),
    Ba(56, 81, 0, -1, null, "Barium", false),
    La(57, 81, 0, -1, null, "Lantanium", false),
    Ce(58, 82, 0, -1, null, "Cerium", false),
    Pr(59, 81, 0, -1, null, "Praseodymium", false),
    Nd(60, 84, 0, -1, null, "Neodymium", false),
    Pm(61, 83, 0, -1, null, "Promethium", false),
    Sm(62, 88, 0, -1, null, "Samarium", false),
    Eu(63, 88, 0, -1, null, "Europium", false),
    Gd(64, 93, 0, -1, null, "Gadolinium", false),
    Tb(65, 93, 0, -1, null, "Terbium", false),
    Dy(66, 96, 0, -1, null, "Dysprosium", false),
    Ho(67, 97, 0, -1, null, "Holmium", false),
    Er(68, 99, 0, -1, null, "Erbium", false),
    Tm(69, 99, 0, -1, null, "Thulium", false),
    Yb(70, 103, 0, -1, null, "Ytterbium", false),
    Lu(71, 103, 0, -1, null, "Lutetium", false),
    Hf(72, 106, 0, -1, null, "Hafnium", false),
    Ta(73, 107, 0, -1, null, "Tantalum", false),
    W(74, 109, 0, -1, null, "Wolframium", false),
    Re(75, 111, 0, -1, null, "Rhenium", false),
    Os(76, 114, 0, -1, null, "Osmium", false),
    Ir(77, 115, 0, -1, null, "Iridium", false),
    Pt(78, 117, 0, -1, null, "Platinum", false),
    Au(79, 117, 0, -1, null, "Gold", false),
    Hg(80, 120, 0, -1, null, "Mercury", false),
    Tl(81, 123, 0, -1, null, "Thallium", false),
    Pb(82, 125, 0, -1, null, "Lead", false),
    Bi(83, 125, 0, -1, null, "Bismuth", false),
    Po(84, 124, 0, -1, null, "Polonium", false),
    At(85, 124, 0, -1, null, "Astatine", false),
    Rn(86, 134, 0, -1, null, "Radon", false),
    Fr(87, 134, 0, -1, null, "Francium", false),
    Ra(88, 136, 0, -1, null, "Radium", false),
    Ac(89, 136, 0, -1, null, "Actinium", false),
    Th(90, 140, 0, -1, null, "Thorium", false),
    Pa(91, 138, 0, -1, null, "Protactinium", false),
    U(92, 146, 0, -1, null, "Uranium", false),
    U235(92, 143, 0, -1, null, "Uranium-235", true),
    Np(93, 144, 0, -1, null, "Neptunium", false),
    Pu(94, 152, 0, -1, null, "Plutonium", false),
    Pu241(94, 149, 0, -1, null, "Plutonium-241", true),
    Am(95, 150, 0, -1, null, "Americium", false),
    Cm(96, 153, 0, -1, null, "Curium", false),
    Bk(97, 152, 0, -1, null, "Berkelium", false),
    Cf(98, 153, 0, -1, null, "Californium", false),
    Es(99, 153, 0, -1, null, "Einsteinium", false),
    Fm(100, 157, 0, -1, null, "Fermium", false),
    Md(101, 157, 0, -1, null, "Mendelevium", false),
    No(102, 157, 0, -1, null, "Nobelium", false),
    Lr(103, 159, 0, -1, null, "Lawrencium", false),
    Rf(104, 161, 0, -1, null, "Rutherfordium", false),
    Db(105, 163, 0, -1, null, "Dubnium", false),
    Sg(106, 165, 0, -1, null, "Seaborgium", false),
    Bh(107, 163, 0, -1, null, "Bohrium", false),
    Hs(108, 169, 0, -1, null, "Hassium", false),
    Mt(109, 167, 0, -1, null, "Meitnerium", false),
    Ds(110, 171, 0, -1, null, "Darmstadtium", false),
    Rg(111, 169, 0, -1, null, "Roentgenium", false),
    Cn(112, 173, 0, -1, null, "Copernicium", false),
    Uut(113, 171, 0, -1, null, "Ununtrium", false),
    Fl(114, 175, 0, -1, null, "Flerovium", false),
    Uup(115, 173, 0, -1, null, "Ununpentium", false),
    Lv(116, 177, 0, -1, null, "Livermorium", false),
    Fa(117, 177, 0, -1, null, "Farnsium", false), // Uus, Ununseptium
    Uuo(118, 176, 0, -1, null, "Ununoctium", false),

    Ma(0, 0, 100, -1, null, "Magic", false),
    Nt(0, 100000, 0, -1, null, "Neutronium", false);

    public final int mProtons, mNeutrons, mAdditionalMass, mHalfLifeSeconds;
    public final String mName, mDecayTo;
    public final boolean mIsIsotope;

    /**
     * @param aProtons         Amount of Protons. Antiprotons if negative.
     * @param aNeutrons        Amount of Neutrons. Antineutrons if negative. (I could have made mistakes with the Neutron amount calculation, please tell me if I did something wrong)
     * @param aHalfLifeSeconds Amount of Half Life this Material has in Seconds. -1 for stable Materials.
     * @param aDecayTo         String representing the Elements it decays to. Separated by an '&' Character.
     * @param aName            Name of the Element
     */
    Element(int aProtons, int aNeutrons, int aAdditionalMass, int aHalfLifeSeconds, String aDecayTo, String aName, boolean aIsIsotope) {
        mProtons = aProtons;
        mNeutrons = aNeutrons;
        mAdditionalMass = aAdditionalMass;
        mHalfLifeSeconds = aHalfLifeSeconds;
        mDecayTo = aDecayTo;
        mName = aName;
        mIsIsotope = aIsIsotope;
    }

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public String getDisplayName() {
        return name();
    }

    public int getProtons() {
        return mProtons;
    }

    public int getNeutrons() {
        return mNeutrons;
    }

    public int getMass() {
        return mProtons + mNeutrons + mAdditionalMass;
    }
}

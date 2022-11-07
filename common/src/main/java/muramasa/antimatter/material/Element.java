package muramasa.antimatter.material;

import muramasa.antimatter.registration.IAntimatterObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Element implements IAntimatterObject {
    public static final Map<String, Element> ELEMENTS = new LinkedHashMap<>();

    public static Element H = new Element(1, 0, 0, -1,1,90, null, "hydrogen", "H", false);
    public static Element D = new Element(1, 1, 0, -1,1,90, "H", "deuterium", "D", true);
    public static Element T = new Element(1, 2, 0, -1,1,90, "D", "tritium", "T", true);
    public static Element He = new Element(2, 2, 0, -1,1,179, null, "helium", "He", false);
    public static Element He_3 = new Element(2, 1, 0, -1,1,179, "H&D", "helium_3", "He3", true);
    public static Element Li = new Element(3, 4, 0, -1,60,534, null, "lithium", "Li", false);
    public static Element Be = new Element(4, 5, 0, -1,550,1848, null, "beryllium", "Be", false);
    public static Element B = new Element(5, 5, 0, -1,930,2460, null, "boron", "B", false);
    public static Element C = new Element(6, 6, 0, -1,50,2260, null, "carbon", "C", false);
    public static Element N = new Element(7, 7, 0, -1,1,1250, null, "nitrogen", "N", false);
    public static Element O = new Element(8, 8, 0, -1,1,1429, null, "oxygen", "O", false);
    public static Element F = new Element(9, 9, 0, -1,1,1695, null, "fluorine", "F", false);
    public static Element Ne = new Element(10, 10, 0, -1,1,900, null, "neon", "Ne", false);
    public static Element Na = new Element(11, 11, 0, -1,50,968, null, "sodium", "Na", false);
    public static Element Mg = new Element(12, 12, 0, -1, 250,1738,null, "magnesium", "Mg", false);
    public static Element Al = new Element(13, 13, 0, -1,275,2698, null, "aluminium", "Al", false);
    public static Element Si = new Element(14, 14, 0, -1,650,2336, null, "silicon", "Si", false);
    public static Element P = new Element(15, 15, 0, -1,1,1830, null, "phosphorus", "P", false);
    public static Element S = new Element(16, 16, 0, -1,200,2070, null, "sulfur", "S", false);
    public static Element Cl = new Element(17, 18, 0, -1,1,3215, null, "chlorine", "Cl", false);
    public static Element Ar = new Element(18, 22, 0, -1,1,1784, null, "argon", "Ar", false);
    public static Element K = new Element(19, 20, 0, -1,40,856, null, "potassium", "K", false);
    public static Element Ca = new Element(20, 20, 0, -1,175,1550, null, "calcium", "Ca", false);
    public static Element Sc = new Element(21, 24, 0, -1,250,2985, null, "scandium", "Sc", false);
    public static Element Ti = new Element(22, 26, 0, -1,600,4500, null, "titanium", "Ti", false);
    public static Element V = new Element(23, 28, 0, -1,700,6110, null, "vanadium", "V", false);
    public static Element Cr = new Element(24, 28, 0, -1,850,7140, null, "chrome", "Cr", false);
    public static Element Mn = new Element(25, 30, 0, -1,600,7430, null, "manganese", "Mn", false);
    public static Element Fe = new Element(26, 30, 0, -1,400,7874, null, "iron", "Fe", false);
    public static Element Co = new Element(27, 32, 0, -1,500,8900, null, "cobalt", "Co", false);
    public static Element Ni = new Element(28, 30, 0, -1,400,8900, null, "nickel", "Ni", false);
    public static Element Cu = new Element(29, 34, 0, -1,300,8900, null, "copper", "Cu", false);
    public static Element Zn = new Element(30, 35, 0, -1,250,7140, null, "zinc", "Zn", false);
    public static Element Ga = new Element(31, 39, 0, -1,150,5904, null, "gallium", "Ga", false);
    public static Element Ge = new Element(32, 40, 0, -1,600,5323, null, "germanium", "Ge", false);
    public static Element As = new Element(33, 42, 0, -1,350,5730, null, "arsenic", "As", false);
    public static Element Se = new Element(34, 45, 0, -1,200,4819, null, "selenium", "Se", false);
    public static Element Br = new Element(35, 45, 0, -1,1,3120, null, "bromine", "Br", false);
    public static Element Kr = new Element(36, 48, 0, -1,1,3749, null, "krypton", "Kr", false);
    public static Element Rb = new Element(37, 48, 0, -1,30,1532, null, "rubidium", "Rb", false);
    public static Element Sr = new Element(38, 49, 0, -1,150,2630, null, "strontium", "Sr", false);
    public static Element Y = new Element(39, 50, 0, -1,1,4472, null, "yttrium", "Y", false);
    public static Element Zr = new Element(40, 51, 0, -1,500,6501, null, "zirconium", "Zr", false);
    public static Element Nb = new Element(41, 53, 0, -1,600,8570, null, "niobium", "Nb", false);
    public static Element Mo = new Element(42, 53, 0, -1,550,10280, null, "molybdenum", "Mo", false);
    public static Element Tc = new Element(43, 55, 0, -1,1,11500, null, "technetium", "Tc", false);
    public static Element Ru = new Element(44, 57, 0, -1,650,12370, null, "ruthenium", "Ru", false);
    public static Element Rh = new Element(45, 58, 0, -1,600,12380, null, "rhodium", "Rh", false);
    public static Element Pd = new Element(46, 60, 0, -1,475,11990, null, "palladium", "Pd", false);
    public static Element Ag = new Element(47, 60, 0, -1,275,10490, null, "silver", "Ag", false);
    public static Element Cd = new Element(48, 64, 0, -1,200,8650, null, "cadmium", "Cd", false);
    public static Element In = new Element(49, 65, 0, -1,250,7310, null, "indium", "In", false);
    public static Element Sn = new Element(50, 68, 0, -1,150,5769, null, "tin", "Sn", false);
    public static Element Sb = new Element(51, 70, 0, -1,300,6697, null, "antimony", "Sb", false);
    public static Element Te = new Element(52, 75, 0, -1,225,6240, null, "tellurium", "Te", false);
    public static Element I = new Element(53, 74, 0, -1,1,4940, null, "iodine", "I", false);
    public static Element Xe = new Element(54, 77, 0, -1,1,5898, null, "xenon", "Xe", false);
    public static Element Cs = new Element(55, 77, 0, -1,20,1900, null, "caesium", "Cs", false);
    public static Element Ba = new Element(56, 81, 0, -1,125,3620, null, "barium", "Ba", false);
    public static Element La = new Element(57, 81, 0, -1,250,6170, null, "lantanium", "La", false);
    public static Element Ce = new Element(58, 82, 0, -1,250,6773, null, "cerium", "Ce", false);
    public static Element Pr = new Element(59, 81, 0, -1,1,6475, null, "praseodymium", "Pr", false);
    public static Element Nd = new Element(60, 84, 0, -1,1,7003, null, "neodymium", "Nd", false);
    public static Element Pm = new Element(61, 83, 0, -1,1,7200, null, "promethium", "Pm", false);
    public static Element Sm = new Element(62, 88, 0, -1,1,7536, null, "samarium", "Sm", false);
    public static Element Eu = new Element(63, 88, 0, -1,1,5245, null, "europium", "Eu", false);
    public static Element Gd = new Element(64, 93, 0, -1,1,7886, null, "gadolinium", "Gd", false);
    public static Element Tb = new Element(65, 93, 0, -1,1,8253, null, "terbium", "Tb", false);
    public static Element Dy = new Element(66, 96, 0, -1,1,8559, null, "dysprosium", "Dy", false);
    public static Element Ho = new Element(67, 97, 0, -1,1,8780, null, "holmium", "Ho", false);
    public static Element Er = new Element(68, 99, 0, -1,1,9045, null, "erbium", "Er", false);
    public static Element Tm = new Element(69, 99, 0, -1,1,9318, null, "thulium", "Tm", false);
    public static Element Yb = new Element(70, 103, 0, -1,1,6973, null, "ytterbium", "Yb", false);
    public static Element Lu = new Element(71, 103, 0, -1,1,9840, null, "lutetium", "Lu", false);
    public static Element Hf = new Element(72, 106, 0, -1,550,13280, null, "hafnium", "Hf", false);
    public static Element Ta = new Element(73, 107, 0, -1,650,16650, null, "tantalum", "Ta", false);
    public static Element W = new Element(74, 109, 0, -1,750,19250, null, "tungsten", "W", false);
    public static Element Re = new Element(75, 111, 0, -1,750,21000, null, "rhenium", "Re", false);
    public static Element Os = new Element(76, 114, 0, -1,700,22590, null, "osmium", "Os", false);
    public static Element Ir = new Element(77, 115, 0, -1,650,22560, null, "iridium", "Ir", false);
    public static Element Pt = new Element(78, 117, 0, -1,350,21450, null, "platinum", "Pt", false);
    public static Element Au = new Element(79, 117, 0, -1,275,19320, null, "gold", "Au", false);
    public static Element Hg = new Element(80, 120, 0, -1,1,13456, null, "mercury", "Hg", false);
    public static Element Tl = new Element(81, 123, 0, -1,120,11850, null, "thallium", "Tl", false);
    public static Element Pb = new Element(82, 125, 0, -1,150,11342, null, "lead", "Pb", false);
    public static Element Bi = new Element(83, 125, 0, -1,225,9780, null, "bismuth", "Bi", false);
    public static Element Po = new Element(84, 124, 0, -1,1,9196, null, "polonium", "Po", false);
    public static Element At = new Element(85, 124, 0, -1,1,1, null, "astatine", "At", false);
    public static Element Rn = new Element(86, 134, 0, -1,1,9730, null, "radon", "Rn", false);
    public static Element Fr = new Element(87, 134, 0, -1,1,1 ,null, "francium", "Fr", false);
    public static Element Ra = new Element(88, 136, 0, -1,1,5500, null, "radium", "Ra", false);
    public static Element Ac = new Element(89, 136, 0, -1,1,1 ,null, "actinium", "Ac", false);
    public static Element Th = new Element(90, 140, 0, -1,230,11724, null, "thorium", "Th", false);
    public static Element Pa = new Element(91, 138, 0, -1,1,15370, null, "protactinium", "Pa", false);
    public static Element U = new Element(92, 146, 0, -1,275,19160, null, "uranium", "U", false);
    public static Element U235 = new Element(92, 143, 0, -1,275,19160, null, "uranium_235", "U235", true);
    public static Element Np = new Element(93, 144, 0, -1,1,20450, null, "neptunium", "Np", false);
    public static Element Pu = new Element(94, 152, 0, -1,1,19816, null, "plutonium", "Pu", false);
    public static Element Pu241 = new Element(94, 149, 0, -1,1,19816, null, "plutonium_241", "Pu241", true);
    public static Element Pu239 = new Element(94, 145, 0, -1,1,19816, null, "plutonium_239", "Pu239", true);
    public static Element Am = new Element(95, 150, 0, -1,1,13670, null, "americium", "Am", false);
    public static Element Cm = new Element(96, 153, 0, -1,1,13510, null, "curium", "Cm", false);
    public static Element Bk = new Element(97, 152, 0, -1,1,14780, null, "berkelium", "Bk", false);
    public static Element Cf = new Element(98, 153, 0, -1,1,15100, null, "californium", "Cf", false);
    public static Element Es = new Element(99, 153, 0, -1,1,8840, null, "einsteinium", "Es", false);
    public static Element Fm = new Element(100, 157, 0, -1,1,1, null, "fermium", "Fm", false);
    public static Element Md = new Element(101, 157, 0, -1,1,1, null, "mendelevium", "Md", false);
    public static Element No = new Element(102, 157, 0, -1,1,1, null, "nobelium", "No", false);
    public static Element Lr = new Element(103, 159, 0, -1, 1,1,null, "lawrencium", "Lr", false);
    public static Element Rf = new Element(104, 161, 0, -1,1,17000, null, "rutherfordium", "Rf", false);
    public static Element Db = new Element(105, 163, 0, -1,1,1, null, "dubnium", "Db", false);
    public static Element Sg = new Element(106, 165, 0, -1,1,1, null, "seaborgium", "Sg", false);
    public static Element Bh = new Element(107, 163, 0, -1,1,1, null, "bohrium", "Bh", false);
    public static Element Hs = new Element(108, 169, 0, -1,1,1, null, "hassium", "Hs", false);
    public static Element Mt = new Element(109, 167, 0, -1,1,1, null, "meitnerium", "Mt", false);
    public static Element Ds = new Element(110, 171, 0, -1,1,1, null, "darmstadtium", "Ds", false);
    public static Element Rg = new Element(111, 169, 0, -1,1,1, null, "roentgenium", "Rg", false);
    public static Element Cn = new Element(112, 173, 0, -1,1,1, null, "copernicium", "Cn", false);
    public static Element Nh = new Element(113, 171, 0, -1,1,1, null, "nihonium", "Nh", false);
    public static Element Fl = new Element(114, 175, 0, -1,1,1, null, "flerovium", "Fl", false);
    public static Element Mc = new Element(115, 173, 0, -1,1,1, null, "moscovium", "Mc", false);
    public static Element Lv = new Element(116, 177, 0, -1,1,1, null, "livermorium", "Lv", false);
    public static Element Ts = new Element(117, 177, 0, -1,1,1, null, "tennessine", "Ts", false);
    public static Element Og = new Element(118, 176, 0, -1,1,1, null, "oganesson", "Og", false);

    public static Element Ma = new Element(0, 0, 100, -1,1,1, null, "Magic", "Ma", false);
    public static Element Nt = new Element(0, 100000, 0, -1,1,1, null, "Neutronium", "Nt", false);

    public final int protons, neutrons, additionalMass, halfLifeSeconds,hardness,density;
    public final String id, element, decayInto;
    public final boolean isIsotope;

    /**
     * @param protons         Amount of Protons. Antiprotons if negative.
     * @param neutrons        Amount of Neutrons. Antineutrons if negative. (I could have made mistakes with the Neutron amount calculation, please tell me if I did something wrong)
     * @param halfLifeSeconds Amount of Half Life this Material has in Seconds. -1 for stable Materials.
     * @param hardness        100*Mohs-Hardness of the Element
     * @param density         Density of the Element in g/m^3
     * @param decayInto       String representing the Elements it decays to. Separated by an '&' Character.
     * @param id              Name of the Element
     */
    public Element(int protons, int neutrons, int additionalMass, int halfLifeSeconds, int hardness, int density, String decayInto, String id, String element, boolean isIsotope) {
        this.protons = protons;
        this.neutrons = neutrons;
        this.additionalMass = additionalMass;
        this.halfLifeSeconds = halfLifeSeconds;
        this.decayInto = decayInto;
        this.hardness = hardness;
        this.density = density;
        this.id = id;
        this.element = element;
        this.isIsotope = isIsotope;
        ELEMENTS.put(element, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public String getElement() {
        return element;
    }

    public int getProtons() {
        return protons;
    }

    public int getNeutrons() {return neutrons;}

    public int getMass() {
        return protons + neutrons + additionalMass;
    }

    public int getHardness(){return hardness;}

    public int getDensity(){return density;}

    public static Element getFromElementId(String element) {
        return ELEMENTS.get(element);
    }
}

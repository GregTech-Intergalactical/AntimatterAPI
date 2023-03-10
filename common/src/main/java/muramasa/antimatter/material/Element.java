package muramasa.antimatter.material;

import muramasa.antimatter.registration.IAntimatterObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class Element implements IAntimatterObject {
    public static final Map<String, Element> ELEMENTS = new LinkedHashMap<>();
    public static Element H = new Element(1, 0, 0, 1,90,"hydrogen", "H", -1,null,null,null, false);
    public static Element He = new Element(2, 2, 0, 1,179,"helium", "He", -1,null,null,null, false);
    public static Element Li = new Element(3, 4, 0,60,534, "lithium", "Li" , -1,null,null,null, false);
    public static Element Be = new Element(4, 4, 0, 550,1848, "beryllium", "Be",-1,null,null,null, false);
    public static Element B = new Element(5, 6, 0, 930,2460, "boron", "B",-1,null,null,null, false);
    public static Element C = new Element(6, 6, 0, 50,2260, "carbon", "C",-1,null,null,null,  false);
    public static Element N = new Element(7, 7, 0,1,1250, "nitrogen", "N",-1,null,null,null, false);
    public static Element O = new Element(8, 8, 0, 1,1429, "oxygen", "O",-1,null,null,null, false);
    public static Element F = new Element(9, 10, 0, 1,1695,"fluorine", "F",-1,null,null,null, false);
    public static Element Ne = new Element(10, 10, 0, 1,900,"neon", "Ne",-1,null,null, null, false);
    public static Element Na = new Element(11, 12, 0, 50,968,"sodium", "Na",-1,null,null, null, false);
    public static Element Mg = new Element(12, 12, 0,  250,1738,"magnesium", "Mg",-1,null,null,null, false);
    public static Element Al = new Element(13, 14, 0, 275,2698,"aluminium", "Al",-1,null,null, null, false);
    public static Element Si = new Element(14, 14, 0, 650,2336,"silicon", "Si",-1,null,null, null, false);
    public static Element P = new Element(15, 16, 0, 1,1830,"phosphorus", "P",-1,null,null, null, false);
    public static Element S = new Element(16, 16, 0, 200,2070,"sulfur", "S",-1,null,null, null, false);
    public static Element Cl = new Element(17, 17, 0, 1,3215,"chlorine", "Cl",-1,null,null, null, false);
    public static Element Ar = new Element(18, 22, 0, 1,1784,"argon", "Ar",-1,null,null, null,  false);
    public static Element K = new Element(19, 20, 0, 40,856,"potassium", "K",-1,null,null, null, false);
    public static Element Ca = new Element(20, 20, 0, 175,1550,"calcium", "Ca",-1,null,null,null, false);
    public static Element Sc = new Element(21, 24, 0, 250,2985,"scandium", "Sc",-1,null,null, null, false);
    public static Element Ti = new Element(22, 26, 0, 600,4500,"titanium", "Ti",-1,null,null, null, false);
    public static Element V = new Element(23, 28, 0, 700,6110,"vanadium", "V",-1,null,null, null, false);
    public static Element Cr = new Element(24, 28, 0, 850,7140,"chrome", "Cr",-1,null,null, null, false);
    public static Element Mn = new Element(25, 30, 0, 600,7430,"manganese", "Mn",-1,null,null, null,  false);
    public static Element Fe = new Element(26, 30, 0, 400,7874,"iron", "Fe",-1,null,null, null,  false);
    public static Element Co = new Element(27, 32, 0, 500,8900,"cobalt", "Co",-1,null,null, null,  false);
    public static Element Ni = new Element(28, 31, 0, 400,8900,"nickel", "Ni",-1,null,null, null,  false);
    public static Element Cu = new Element(29, 35, 0, 300,8900,"copper", "Cu",-1,null,null, null,  false);
    public static Element Zn = new Element(30, 35, 0, 250,7140,"zinc", "Zn",-1,null,null, null,  false);
    public static Element Ga = new Element(31, 39, 0, 150,5904,"gallium", "Ga",-1,null,null, null, false);
    public static Element Ge = new Element(32, 41, 0, 600,5323,"germanium", "Ge",-1,null,null, null,  false);
    public static Element As = new Element(33, 42, 0, 350,5730,"arsenic", "As",-1,null,null, null,  false);
    public static Element Se = new Element(34, 45, 0, 200,4819,"selenium", "Se",-1,null,null, null,  false);
    public static Element Br = new Element(35, 45, 0, 1,3120,"bromine", "Br",-1,null,null, null,  false);
    public static Element Kr = new Element(36, 48, 0, 1,3749,"krypton", "Kr",-1,null,null, null,  false);
    public static Element Rb = new Element(37, 48, 0, 30,1532,"rubidium", "Rb",-1,null,null, null,  false);
    public static Element Sr = new Element(38, 50, 0, 150,2630,"strontium", "Sr",-1,null,null, null,  false);
    public static Element Y = new Element(39, 50, 0, 1,4472,"yttrium", "Y",-1,null,null, null,  false);
    public static Element Zr = new Element(40, 51, 0, 500,6501,"zirconium", "Zr",-1,null,null, null,  false);
    public static Element Nb = new Element(41, 52, 0, 600,8570,"niobium", "Nb",-1,null,null, null,  false);
    public static Element Mo = new Element(42, 54, 0, 550,10280,"molybdenum", "Mo",-1,null,null, null,  false);
    public static Element Tc = new Element(43, 55, 0, 1,11500,"technetium", "Tc",-1,null,null, null,  false);
    public static Element Ru = new Element(44, 57, 0, 650,12370,"ruthenium", "Ru",-1,null,null, null,  false);
    public static Element Rh = new Element(45, 58, 0, 600,12380,"rhodium", "Rh",-1,null,null, null,  false);
    public static Element Pd = new Element(46, 60, 0, 475,11990,"palladium", "Pd",-1,null,null, null,  false);
    public static Element Ag = new Element(47, 61, 0, 275,10490,"silver", "Ag",-1,null,null, null,  false);
    public static Element Cd = new Element(48, 64, 0, 200,8650,"cadmium", "Cd",-1,null,null, null,  false);
    public static Element In = new Element(49, 66, 0, 250,7310,"indium", "In",-1,null,null, null,  false);
    public static Element Sn = new Element(50, 69, 0,150,5769,"tin", "Sn",-1,null,null, null,  false);
    public static Element Sb = new Element(51, 71, 0, 300,6697,"antimony", "Sb",-1,null,null, null,  false);
    public static Element Te = new Element(52, 76, 0, 225,6240,"tellurium", "Te",-1,null,null, null,  false);
    public static Element I = new Element(53, 74, 0, 1,4940,"iodine", "I",-1,null,null, null,  false);
    public static Element Xe = new Element(54, 77, 0, 1,5898,"xenon", "Xe",-1,null,null, null,  false);
    public static Element Cs = new Element(55, 78, 0, 20,1900,"caesium", "Cs",-1,null,null, null,  false);
    public static Element Ba = new Element(56, 81, 0, 125,3620,"barium", "Ba",-1,null,null, null,  false);
    public static Element La = new Element(57, 82, 0, 250,6170,"lanthanum", "La",-1,null,null, null,  false);
    public static Element Ce = new Element(58, 82, 0, 250,6773,"cerium", "Ce",-1,null,null, null,  false);
    public static Element Pr = new Element(59, 82, 0, 1,6475,"praseodymium", "Pr",-1,null,null, null,  false);
    public static Element Nd = new Element(60, 84, 0, 1,7003,"neodymium", "Nd",-1,null,null, null,  false);
    public static Element Pm = new Element(61, 84, 0, 1,7200,"promethium", "Pm",-1,null,null, null,  false);
    public static Element Sm = new Element(62, 88, 0, 1,7536,"samarium", "Sm",-1,null,null, null,  false);
    public static Element Eu = new Element(63, 89, 0, 1,5245,"europium", "Eu",-1,null,null, null,  false);
    public static Element Gd = new Element(64, 93, 0, 1,7886,"gadolinium", "Gd",-1,null,null, null,  false);
    public static Element Tb = new Element(65, 94, 0, 1,8253,"terbium", "Tb",-1,null,null, null,  false);
    public static Element Dy = new Element(66, 97, 0, 1,8559,"dysprosium", "Dy",-1,null,null, null,  false);
    public static Element Ho = new Element(67, 98, 0, 1,8780,"holmium", "Ho",-1,null,null, null,  false);
    public static Element Er = new Element(68, 99, 0, 1,9045,"erbium", "Er",-1,null,null, null,  false);
    public static Element Tm = new Element(69, 100, 0, 1,9318,"thulium", "Tm",-1,null,null, null,  false);
    public static Element Yb = new Element(70, 103, 0, 1,6973,"ytterbium", "Yb",-1,null,null, null,  false);
    public static Element Lu = new Element(71, 104, 0, 1,9840,"lutetium", "Lu",-1,null,null, null,  false);
    public static Element Hf = new Element(72, 106, 0, 550,13280,"hafnium", "Hf",-1,null,null, null,  false);
    public static Element Ta = new Element(73, 108, 0, 650,16650,"tantalum", "Ta",-1,null,null, null,  false);
    public static Element W = new Element(74, 110, 0, 750,19250,"tungsten", "W",-1,null,null, null,  false);
    public static Element Re = new Element(75, 111, 0, 750,21000,"rhenium", "Re",-1,null,null, null,  false);
    public static Element Os = new Element(76, 114, 0, 700,22590,"osmium", "Os",-1,null,null, null,  false);
    public static Element Ir = new Element(77, 115, 0, 650,22560,"iridium", "Ir",-1,null,null, null,  false);
    public static Element Pt = new Element(78, 117, 0, 350,21450,"platinum", "Pt",-1,null,null, null,  false);
    public static Element Au = new Element(79, 118, 0, 275,19320,"gold", "Au",-1,null,null, null,  false);
    public static Element Hg = new Element(80, 121, 0, 1,13456,"mercury", "Hg",-1,null,null, null,  false);
    public static Element Tl = new Element(81, 123, 0, 120,11850,"thallium", "Tl",-1,null,null, null,  false);
    public static Element Pb = new Element(82, 125, 0, 150,11342,"lead", "Pb",-1,null,null, null,  false);
    public static Element Bi = new Element(83, 126, 0, 225,9780,"bismuth", "Bi",-1,null,null, null,  false);
    public static Element Po = new Element(84, 125, 0, 1,9196,"polonium", "Po",-1,null,null, null,  false);
    public static Element At = new Element(85, 125, 0, 1,1,"astatine", "At",-1,null,null, null,  false);
    public static Element Rn = new Element(86, 136, 0, 1,9730,"radon", "Rn",-1,null,null, null,  false);
    public static Element Fr = new Element(87, 136, 0, 1,1,"francium", "Fr",-1,null,null,null,  false);
    public static Element Ra = new Element(88, 138, 0, 1,5500,"radium", "Ra",-1,null,null, null,  false);
    public static Element Ac = new Element(89, 138, 0, 1,1,"actinium", "Ac",-1,null,null,null,  false);
    public static Element Th = new Element(90, 142, 0, 230,11724,"thorium", "Th",-1,null,null, null,  false);
    public static Element Pa = new Element(91, 140, 0, 1,15370,"protactinium", "Pa",-1,null,null, null,  false);
    public static Element U = new Element(92, 146, 0, 275,19160,"uranium", "U",-1,null,null, null,  false);
    public static Element Np = new Element(93, 144, 0, 1,20450,"neptunium", "Np",-1,null,null, null,  false);
    public static Element Pu = new Element(94, 150, 0, 1,19816,"plutonium", "Pu",-1,null,null, null,  false);
    public static Element Am = new Element(95, 148, 0, 1,13670,"americium", "Am",-1,null,null, null,  false);
    public static Element Cm = new Element(96, 151, 0, 1,13510,"curium", "Cm",-1,null,null, null,  false);
    public static Element Bk = new Element(97, 150, 0, 1,14780,"berkelium", "Bk",-1,null,null, null,  false);
    public static Element Cf = new Element(98, 153, 0, 1,15100,"californium", "Cf",-1,null,null, null,  false);
    public static Element Es = new Element(99, 153, 0, 1,8840,"einsteinium", "Es",-1,null,null, null,  false);
    public static Element Fm = new Element(100, 157, 0, 1,1,"fermium", "Fm",-1,null,null, null,  false);
    public static Element Md = new Element(101, 157, 0, 1,1,"mendelevium", "Md",-1,null,null, null,  false);
    public static Element No = new Element(102, 157, 0, 1,1,"nobelium", "No",-1,null,null, null,  false);
    public static Element Lr = new Element(103, 153, 0,  1,1,"lawrencium", "Lr",-1,null,null,null,  false);
    public static Element Rf = new Element(104, 157, 0, 1,17000,"rutherfordium", "Rf",-1,null,null, null,  false);
    public static Element Db = new Element(105, 157, 0, 1,1,"dubnium", "Db",-1,null,null, null,  false);
    public static Element Sg = new Element(106, 157, 0, 1,1,"seaborgium", "Sg",-1,null,null, null,  false);
    public static Element Bh = new Element(107, 155, 0, 1,1,"bohrium", "Bh",-1,null,null, null,  false);
    public static Element Hs = new Element(108, 162, 0, 1,1,"hassium", "Hs",-1,null,null, null,  false);
    public static Element Mt = new Element(109, 159, 0, 1,1,"meitnerium", "Mt",-1,null,null, null,  false);
    public static Element Ds = new Element(110, 171, 0, 1,1,"darmstadtium", "Ds",-1,null,null, null,  false);
    public static Element Rg = new Element(111, 169, 0, 1,1,"roentgenium", "Rg",-1,null,null, null,  false);
    public static Element Cn = new Element(112, 165, 0, 1,1,"copernicium", "Cn",-1,null,null, null,  false);
    public static Element Nh = new Element(113, 174, 0, 1,1,"nihonium", "Nh",-1,null,null, null,  false);
    public static Element Fl = new Element(114, 175, 0, 1,1,"flerovium", "Fl",-1,null,null, null,  false);
    public static Element Mc = new Element(115, 173, 0, 1,1,"moscovium", "Mc",-1,null,null, null , false);
    public static Element Lv = new Element(116, 177, 0, 1,1,"livermorium", "Lv",-1,null,null, null,  false);
    public static Element Ts = new Element(117, 175, 0, 1,1,"tennessine", "Ts",-1,null,null, null,  false);
    public static Element Og = new Element(118, 176, 0, 1,1,"oganesson", "Og",-1,null,null, null,  false);
    public static Element Ma = new Element(0, 0, 100, 1,1,"Magic", "Ma",-1,null,null, null,  false);
    public static Element Nt = new Element(0, 100000, 0, 1,1,"Neutronium", "Nt",-1,null,null, null,  false);

    //Isotopes
    public static Element D = new Element(1, 1, 0,1,90,"deuterium", "D",-1,null,null,null,true);
    public static Element T = new Element(1, 2, 0,1,90, "tritium",  "T", 378432000,null,null,"helium_3",true);
    public static Element He3 = new Element(2, 1, 0,1,179,"helium_3", "He3", -1,null,null,null,true);
    public static Element Tl207 = new Element(81, 126, 0, 120,11850,"thallium_207", "Tl207",300,null,null, "lead",  true);
    public static Element Tl209 = new Element(81, 128, 0, 120,11850,"thallium_209", "Tl209",120,null,null, "lead",  true);
    public static Element Tl210 = new Element(81, 129, 0, 120,11850,"thallium_210", "Tl210",120,null,null, "lead",  true);
    public static Element Bi211 = new Element(83, 128, 0, 225,9780,"bismuth_211", "Bi211",120,"thallium_207",null, "polonium_211",  true);
    public static Element Bi213 = new Element(83, 130, 0, 225,9780,"bismuth_213", "Bi213",165600,"thallium_209",null, "polonium_213",  true);
    public static Element Bi214 = new Element(83, 131, 0, 225,9780,"bismuth_214", "Bi214",1200,"thallium_210",null, "polonium_214",  true);
    public static Element Bi215 = new Element(83, 132, 0, 225,9780,"bismuth_215", "Bi215",1200,null,null, "polonium_215",  true);
    public static Element Po211 = new Element(84, 127, 0, 1,9196,"polonium_211", "Po211",1,"lead",null, null,  true);
    public static Element Po213 = new Element(84, 129, 0, 1,9196,"polonium_213", "Po213",1,"lead",null, null,  true);
    public static Element Po214 = new Element(84, 130, 0, 1,9196,"polonium_214", "Po214",1,"lead",null, null,  true);
    public static Element Po215 = new Element(84, 131, 0, 1,9196,"polonium_215", "Po215",1,"lead",null, "astatine_215",  true);
    public static Element Po216 = new Element(84, 132, 0, 1,9196,"polonium_216", "Po216",1,"lead",null, null,  true);
    public static Element Po218 = new Element(84, 134, 0, 1,9196,"polonium_218", "Po218",180,"lead",null, "astatine_218",  true);
    public static Element At215 = new Element(85, 130, 0, 1,1,"astatine_215", "At215",1,"bismuth_211",null, null,  true);
    public static Element At217 = new Element(85, 132, 0, 1,1,"astatine_217", "At217",1,"bismuth_213",null, "radon_217",  true);
    public static Element At218 = new Element(85, 133, 0, 1,1,"astatine_218", "At218",2,"bismuth_214",null, "radon_218",  true);
    public static Element At219 = new Element(85, 134, 0, 1,1,"astatine_219", "At219",1,"bismuth_215",null, "radon_219",  true);
    public static Element Rn217 = new Element(86, 131, 0, 1,9730,"radon_217", "Rn217",1,"polonium_213",null, null,  true);
    public static Element Rn218 = new Element(86, 132, 0, 1,9730,"radon_218", "Rn218",1,"polonium_214",null, null,  true);
    public static Element Rn219 = new Element(86, 133, 0, 1,9730,"radon_219", "Rn219",4,"polonium_215",null, null,  true);
    public static Element Rn220 = new Element(86, 134, 0, 1,9730,"radon_220", "Rn220",56,"polonium_216",null, null,  true);
    public static Element Rn222 = new Element(86, 136, 0,1,9730,"radon_222", "Rn222",345600,"polonium_218",null,null,  true);
    public static Element Fr221 = new Element(87, 134, 0, 1,1,"francium_221", "Fr221",300,"astatine_217",null,"radium_221",  true);
    public static Element Fr223 = new Element(87, 136, 0, 1,1,"francium_223", "Fr223",1320,"astatine_219",null,"radium_223",  true);
    public static Element Ra221 = new Element(88, 137, 0,1,5500,"radium_221", "Ra221",28,"radon_217",null,null,  true);
    public static Element Ra223 = new Element(88, 139, 0,1,5500,"radium_223", "Ra223",950400,"radon_219",null,null,  true);
    public static Element Ra224 = new Element(88, 140, 0,1,5500,"radium_224", "Ra224",345600,"radon_220",null,null,  true);
    public static Element Ra225 = new Element(88, 141, 0,1,5500,"radium_225", "Ra225",1296000,null,null,"actinium_225",  true);
    public static Element Ra226 = new Element(88, 142, 0,1,5500,"radium_226", "Ra226",50520672000L,"radon_222",null,null,true);
    public static Element Ra228 = new Element(88, 143, 0,1,5500,"radium_228", "Ra228",189216000,null,null,"actinium_228",  true);
    public static Element Ac225 = new Element(89, 136, 0, 1,1,"actinium_225", "Ac225",864000,"francium_221",null,null,  true);
    public static Element Ac227 = new Element(89, 138, 0, 1,1,"actinium_227", "Ac227",693792000,"francium_223",null,"thorium_227",  true);
    public static Element Ac228 = new Element(89, 139, 0, 1,1,"actinium_228", "Ac228",21600,null,null,"thorium_228",  true);
    public static Element Th227 = new Element(90, 137, 0,230,11724,"thorium_227", "Th227",1641600,"radium_223",null,null,  true);
    public static Element Th228 = new Element(90, 138, 0,230,11724,"thorium_228", "Th228",63072000,"radium_224",null,null,  true);
    public static Element Th229 = new Element(90, 139, 0,230,11724,"thorium_229", "Th229",10402938000L,"radium_225",null,null,  true);
    public static Element Th230 = new Element(90, 140, 0,230,11724,"thorium_230", "Th230",2365200000L,"radium_226",null,null,  true);
    public static Element Th231 = new Element(90, 141, 0,230,11724,"thorium_231", "Th231",93600,"radium_227",null,"protactinium_231",  true);
    public static Element Th232 = new Element(90, 142, 0,230,11724,"thorium_232", "Th232",315360000000000000L,"radium_228",null,null,  true);
    public static Element Th233 = new Element(90, 143, 0,230,11724,"thorium_233", "Th233",1320,null,null,"protactinium_233",true);
    public static Element Th234 = new Element(90, 144, 0,230,11724,"thorium_234", "Th234",2073600, null,null,"protactinium_234",true);
    public static Element Pa231 = new Element(91, 140, 0,1,15370,"protactinium_231", "Pa231",1040688000, "actinium_227",null,null,true);
    public static Element Pa232 = new Element(91, 141, 0,1,15370,"protactinium_232", "Pa232",86400,null,"thorium_232", "uranium_232",  true);
    public static Element Pa233 = new Element(91, 142, 0,1,15370,"protactinium_233", "Pa233",2332800, null,null,"uranium_233",true);
    public static Element Pa234 = new Element(91, 143, 0,1,15370,"protactinium_234", "Pa234",25200, null,null,"uranium_234",true);
    public static Element U232 = new Element(92, 140, 0,275,19160,"uranium_232", "U232",2175984000L, "thorium_228",null,null,true);
    public static Element U233 = new Element(92, 141, 0,275,19160,"uranium_233", "U233",6307200000000L, "thorium_229",null,null,true);
    public static Element U234 = new Element(92, 142, 0,275,19160,"uranium_234", "U234",6307200000000L, "thorium_230",null,null,true);
    public static Element U235 = new Element(92, 143, 0,275,19160,"uranium_235", "U235",22075200000000000L, "thorium_231",null,null,true);
    public static Element U236 = new Element(92, 144, 0,275,19160,"uranium_236", "U236",725328000000000L, "thorium_232",null,null,true);
    public static Element U237 = new Element(92, 145, 0,275,19160,"uranium_237", "U237",604800, null,null,"neptunium_237",true);
    public static Element U238 = new Element(92, 146, 0,275,19160,"uranium_238", "U238",126144000000000000L, "thorium_234",null,"plutonium_238",true);
    public static Element U239 = new Element(92, 147, 0,275,19160,"uranium_239", "U239",1380,null,null,"neptunium_239",true);
    public static Element U240 = new Element(92, 148, 0,275,19160,"uranium_240", "U240",50400,null,null,"neptunium_240",true);
    public static Element Np236 = new Element(93, 143, 0,1,20450,"neptunium_236", "Np236",6307200000000L, "protactinium_232","uranium_236","plutonium_236",true);
    public static Element Np237 = new Element(93, 144, 0,1,20450,"neptunium_237", "Np237",63072000000000L,"protactinium_233", null,null,true);
    public static Element Np238 = new Element(93, 145, 0,1,20450,"neptunium_238", "Np238",172800, null,null,"plutonium_238",true);
    public static Element Np239 = new Element(93, 146, 0,1,20450,"neptunium_239", "Np239",172800,null,null,"plutonium_239",true);
    public static Element Np240 = new Element(93, 147, 0,1,20450,"neptunium_240", "Np240",3720,null,null,"plutonium_240",true);
    public static Element Pu236 = new Element(94, 142, 0, 1,19816,"plutonium_236", "Pu236",94608000,"uranium_232",null,null,true);
    public static Element Pu238 = new Element(94, 144, 0, 1,19816,"plutonium_238", "Pu238",2775168000L,"uranium_234",null,null,true);
    public static Element Pu239 = new Element(94, 145, 0, 1,19816,"plutonium_239", "Pu239",756864000,"uranium_235",null,null,true);
    public static Element Pu240 = new Element(94, 146, 0,1,19816,"plutonium_240", "Pu240",207002304000L,"uranium_236",null, null,true);
    public static Element Pu241 = new Element(94, 147, 0,1,19816,"plutonium_241", "Pu241",441504000,"uranium_237",null, "americium_241",true);
    public static Element Pu242 = new Element(94, 148, 0,1,19816,"plutonium_242", "Pu242",11826000000000L,"uranium_238",null, null,true);
    public static Element Pu243 = new Element(94, 149, 0,1,19816,"plutonium_243", "Pu243",18000, null,null,"americium_243",true);
    public static Element Pu244 = new Element(94, 150, 0,1,19816,"plutonium_244", "Pu244",18000, "uranium_240",null,null,true);
    public static Element Pu246 = new Element(94, 152, 0,1,19816,"plutonium_246", "Pu246",950400, null,null,"americium_246",true);
    public static Element Am241 = new Element(95, 146, 0,1,13670,"americium_241","Am241", 13623552000L,"neptunium_237",null,null,true);
    public static Element Am242 = new Element(95, 147, 0,1,13670,"americium_242", "Am242",57600, null,"plutonium_242","curium_242",true);
    public static Element Am244 = new Element(95, 149, 0,1,13670,"americium_244", "Am244",36000,null,null,"curium_244",true);
    public static Element Am245 = new Element(95, 150, 0,1,13670,"americium_245", "Am245",72000,null,null,"curium_245",true);
    public static Element Am246 = new Element(95, 151, 0,1,13670,"americium_246", "Am246",2340,null,null,"curium_246",true);
    public static Element Cm242 = new Element(96, 148, 0,1,13510,"curium_242", "Cm242",14083200,"plutonium_238",null,null,true);
    public static Element Cm244 = new Element(96, 150, 0,1,13510,"curium_244", "Cm244",567648000,"plutonium_240",null,null,true);
    public static Element Cm245 = new Element(96, 151, 0,1,13510,"curium_245", "Cm245",268056000000L,"plutonium_241",null,null,true);
    public static Element Cm246 = new Element(96, 152, 0,1,13510,"curium_246", "Cm246",149165280000L,"plutonium_242",null,null,true);
    public static Element Cm247 = new Element(96, 153, 0,1,13510,"curium_247", "Cm247",504576000000000L,"plutonium_243",null,null,true);
    public static Element Cm248 = new Element(96, 154, 0,1,13510,"curium_248", "Cm248",10974528000000L,"plutonium_244",null,null,true);
    public static Element Cm249 = new Element(96, 155, 0,1,13510,"curium_249", "Cm249",3840,null,null,"berkelium_249",true);
    public static Element Cm250 = new Element(96, 156, 0,1,13510,"curium_250", "Cm250",261748800000L,"plutonium_246",null,"berkelium_250",true);
    public static Element Bk248 = new Element(97, 151, 0,1,14780,"berkelium_248", "Bk248",283824000,"americium_248","curium_248","californium_248",true);
    public static Element Bk249 = new Element(97, 152, 0,1,14780,"berkelium_249", "Bk249",28512000, "americium_245",null,"californium_249",true);
    public static Element Bk250 = new Element(97, 153, 0,1,14780,"berkelium_250", "Bk250",10800, null,null,"californium_250",true);
    public static Element Bk251 = new Element(97, 154, 0,1,14780,"berkelium_251", "Bk251",3360, "americium_246",null,"californium_251",true);
    public static Element Cf248 = new Element(98, 148, 0,1,15100,"californium_248", "Cf248",28857600,"curium_244",null,null,true);
    public static Element Cf249 = new Element(98, 149, 0,1,15100,"californium_249", "Cf249",11069136000L,"curium_245",null,null,true);
    public static Element Cf250 = new Element(98, 150, 0,1,15100,"californium_250", "Cf250",409968000,"curium_246",null,null,true);
    public static Element Cf251 = new Element(98, 151, 0,1,15100,"californium_251", "Cf251",28382400000L,"curium_247",null,null,true);
    public static Element Cf252 = new Element(98, 152, 0,1,15100,"californium_252", "Cf252",94608000,"curium_248",null,null,true);
    public static Element Cf253 = new Element(98, 155, 0,1,15100,"californium_253", "Cf253",94608000,"curium_249",null,"einsteinium_253",true);
    public static Element Cf254 = new Element(98, 156, 0,1,15100,"californium_254", "Cf254",5270400,"curium_250",null,null,true);
    public static Element Cf255 = new Element(98, 157, 0,1,15100,"californium_255", "Cf255",5100,null,null,"einsteinium_255",true);
    public static Element Es253 = new Element(99, 154, 0,1,8840,"einsteinium_253", "Es253",1728000, "berkelium_249",null,null,true);
    public static Element Es254 = new Element(99, 155, 0,1,8840,"einsteinium_254", "Es254",23846400, "berkelium_250","californium_254","fermium_255",true);
    public static Element Es255 = new Element(99, 156, 0,1,8840,"einsteinium_255", "Es255",3456000,"berkelium_251",null,"fermium_255",true);
    public static Element Es256 = new Element(99, 157, 0,1,8840,"einsteinium_256", "Es256",1500,null,null,"fermium_256",true);
    public static Element Fm255 = new Element(100, 155, 0,1,1,"fermium_255", "Fm255",72000,"californium_251",null,null,true);
    public static Element Fm256 = new Element(100, 156, 0,1,1,"fermium_256", "Fm256",9480,"californium_252",null,null,true);
    public static Element Fm257 = new Element(100, 157, 0,1,1,"fermium_257", "Fm257",8726400,"californium_253",null,null,true);
    public static Element Fm258 = new Element(100, 158, 0,1,1,"fermium_258", "Fm258",1,"californium_254",null,null,true);
    public static Element Fm259 = new Element(100, 159, 0,1,1,"fermium_259", "Fm259",2,"californium_255",null,null,true);
    public static Element Fm260 = new Element(100, 160, 0,1,1,"fermium_260", "Fm260",1,null,null,"mendelevium_260",true);
    public static Element Md259 = new Element(101, 158, 0,1,1,"mendelevium_259", "Md259",5760,"einsteinium_255",null,null,true);
    public static Element Md260 = new Element(101, 159, 0,1,1,"mendelevium_260", "Md260",2419200,"einsteinium_256","fermium_260",null,true);
    public final int protons, neutrons, additionalMass, hardness, density;
    public final long halfLifeSeconds;
    public final String id, element, decayIntoA, decayIntoBP, decayIntoBM;
    public final boolean isIsotope;

    /**
     * @param protons           Amount of Protons. Antiprotons if negative.
     * @param neutrons          Amount of Neutrons. Antineutrons if negative.
     * @param hardness          100*Mohs-Hardness of the Element.
     * @param density           Density of the Element in g/m^3.
     * @param halfLifeSeconds   Amount of Half Life this Material has for any decay in Seconds. -1 for stable Materials.
     * @param decayIntoA        String representing the Element it decays to with alpha decay.
     * @param decayIntoBP       String representing the Element it decays to with beta+ decay or electron capture, since both have the same consequences for the nuclide.
     * @param decayIntoBM       String representing the Element it decays to with beta- decay.
     * @param id                Name of the Element
     */
    public Element(int protons, int neutrons, int additionalMass, int hardness, int density, String id, String element, long halfLifeSeconds, String decayIntoA, String decayIntoBP, String decayIntoBM, boolean isIsotope) {
        this.protons = protons;
        this.neutrons = neutrons;
        this.additionalMass = additionalMass;
        this.hardness = hardness;
        this.density = density;
        this.id = id;
        this.element = element;
        this.halfLifeSeconds = halfLifeSeconds;
        this.decayIntoA = decayIntoA;
        this.decayIntoBP = decayIntoBP;
        this.decayIntoBM = decayIntoBM;
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

    public static Element getFromNeutrons(int neutrons, boolean include_isotopes){
        Element element = H;
        for (Element e : ELEMENTS.values()) {
            if(e.getNeutrons() == neutrons && (include_isotopes || !e.isIsotope)){
                element = e;
                break;
            }
        }
        return element;
    }

    public static Element getFromProtons(int protons, boolean include_isotopes){
        Element element = H;
        for (Element e : ELEMENTS.values()) {
            if(e.getProtons() == protons && (include_isotopes || !e.isIsotope)){
                element = e;
                break;
            }
        }
        return element;
    }

    public static Element getFromProtonsAndNeutrons(int protons, int neutrons, boolean include_isotopes){
        Element element = H;
        for (Element e : ELEMENTS.values()) {
            if(e.getProtons() == protons && e.getNeutrons() == neutrons && (include_isotopes || !e.isIsotope)){
                element = e;
                break;
            }
        }
        return element;
    }
}

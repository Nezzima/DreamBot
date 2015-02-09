package nezz.dreambot.scriptmain.herblore;

public enum Pots{
	//doesn't have Sanfew serum, Guthix balance potion, and Guthix rest tea
	AttackPotion("Attack potion", 3, 25, "Guam leaf", "Eye of newt"),
	Antipoison("Antipoison", 5, 37.5, "Marrentill", "Unicorn horn dust"),
	RelicymsBalm("Relicym's balm", 8, 40, "Rogue's purse", "Snake weed"),
	StrengthPotion("Strength potion", 12, 50, "Tarromin", "Limpwurt root"),
	Serum207("Serum 207", 15, 50, "Tarromin", "Ashes"),
	GuamTar("Guam tar", 19, 30, "Guam leaf", "Swamp tar"),//need 15 swamp tar how to do that?
	StatRestorePotion("Stat restore potion", 22, 62.5, "Harralander", "Red spiders' eggs"),
	BlamishOil("Blamish oil", 25, 80, "Harralander", "Blamish snail slime"),
	EnergyPotion("Energy potion", 26, 67.5, "Harralander", "Chocolate dust"),
	DefencePotion("Defence potion", 30, 75, "Ranarr weed", "White berries"),
	MarrentillTar("Marrentil tar", 31, 42.5, "Marrentill", "Swamp tar"), //need 15 swamp tar
	AgilityPotion("Agility potion", 34, 80, "Toadflax", "Toad's legs"),
	CombatPotion("Combat potion", 36, 84, "Harralander", "Goat horn dust"),
	PrayerPotion("Prayer potion", 38, 87.5, "Ranarr weed", "Snape grass"),
	TarrominTar("Tarromin tar", 39, 55, "Tarromin", "Swamp tar"),//15 swamp tar
	HarralanderTar("HarralanderTar", 44, 72.5, "Harralander", "Swamp tar"), //15 swamp tar
	SuperAttackPotion("Super attack", 45, 100, "Irit leaf", "Eye of newt"),
	SuperAntipoison("Superantipoison", 48, 106.3, "Irit leaf", "Unicorn horn dust"),
	FishingPotion("Fishing potion", 50, 112.5, "Avantoe", "Snape grass"),
	SuperEnergyPotion("Super energy", 52, 117.5, "Avantoe", "Mort myre fungus"),
	ShrinkingPotion("Shrinking potion", 52, 6, "Tarromin", "Shrunk Ogleroot"),
	HuntingPotion("Hunting potion",53,87.5,"Avantoe", "Ground kebbit teeth"),
	SuperStrengthPotion("Super strength",55,125, "Kwuarm", "Limpwurt root"),
	MagicEssencePotion("Magic essence potion",57,130, "Starflower", "Crushed gorak claw"),
	WeaponPoison("Weapon poison",60,137.5, "Kwuarm", "Dragon scale dust"),
	SuperRestorePotion("Super restore",63,142.5, "Snapdragon", "Red spiders' eggs"),
	SuperDefencePotion("Super defence",66,150, "Cadantine", "White berries"),
	ExtraStrongAntiPoisonPotion("Extra strong antipoison potion",68,155, "Toadflax", "Yew roots"),
	AntiFirePotion("Anti-fire potion",69,157.5, "Lantadyme", "Dragon scale dust"),
	RangingPotion("Ranging potion",72,162.5, "Dwarf weed", "Wine of Zamorak"),
	ExtraStrongWeaponPoison("Extra strong weapon poison",73,165, "Coconut milk", "Cactus spine"),//and Red spiders' eggs
	MagicPotion("Magic potion",76,172.5, "Lantadyme", "Potato cactus"),
	ZamorakPotion("Zamorak potion",78,175, "Torstol", "Jangerberries"),
	SuperStrongAntipoisonPotion("Super strong antipoison potion",79,177.5, "Coconut milk", "Magic roots"), //ingredient 1 also includes Irit leaf
	SaradominBrew("Saradomin brew",81,180, "Toadflax", "Crushed bird's nest"),
	SuperStrongWeaponPoison("Super strong weapon poison", 82, 190, "Coconut milk", "Nightshade"),//also includes Poison ivy berries in ingredient 2
	ExtendedAntifire("Extended antifire", 69, 110, "Antifire potion", "Lava scale shard");
	
	public String name;
	int level;
	double experience;
	String ingredientOne;
	String ingredientTwo;
	
	public String getName(){
		return this.name;
	}
	
	public int getLevel(){
		return this.level;
	}
	
	public double getExperience(){
		return this.experience;
	}
	
	public String getIngredientOne(){
		return this.ingredientOne;
	}
	
	public String getIngredientTwo(){
		return this.ingredientTwo;
	}
	
	Pots(String name, int level, double experience, String ingredientOne, String ingredientTwo){
		this.name = name;
		this.level = level;
		this.experience = experience;
		this.ingredientOne = ingredientOne;
		this.ingredientTwo = ingredientTwo;
	}
	
	public static String returnIngredientOne(int index){
		for(Pots p: Pots.values()){
			if(p.ordinal() == index){
				return p.getIngredientOne();
			}
		}
		return null;
	}
	
	public static String returnIngredientTwo(int index){
		for(Pots p: Pots.values()){
			if(p.ordinal() == index){
				return p.getIngredientTwo();
			}
		}
		return null;
	}
	
	public static int returnLvlReq(int index){
		for(Pots p: Pots.values()){
			if(p.ordinal() == index){
				return p.getLevel();
			}
		}
		return 0;
	}
	
	public static double returnXPGained(int index){
		for(Pots p: Pots.values()){
			if(p.ordinal() == index){
				return p.getExperience();
			}
		}
		return 0;
	}
	public String getUnfName(){
		String first = getIngredientOne().split(" ")[0];
		return first + " potion (unf)";
	}
}

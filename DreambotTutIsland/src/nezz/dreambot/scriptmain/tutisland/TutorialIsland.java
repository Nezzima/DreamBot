package nezz.dreambot.scriptmain.tutisland;

import nezz.dreambot.tutisland.gui.ScriptVars;
import nezz.dreambot.tutisland.gui.tutIslandGui;
import org.dreambot.api.Client;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.login.LoginUtility;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.awt.*;
import java.util.List;

@ScriptManifest(author = "Nezz", category = Category.MISC, description = "Does tutorial island", name = "DreamBot Tutorial Island", version = 1)
public class TutorialIsland extends AbstractScript {

	private final int TAB_CONFIG = 1021;
	private final int PERC_COMP = 406;
	private final int ATT_SPEED = 843;
	private final int POLL_OPEN = 375;
	private final int TUT_PROG = 281;
	private final int APPEAR_PAR = 269;
	private final int[][] appChildren = {{106, 113}, {107, 114}, {108, 115}, {109, 116}, {110, 117}, {111, 118}, {112, 119}, {105, 121}, {123, 127}, {122, 129}, {124, 130}, {125, 131}};
	private final int ACCEPT = 100;

	private final String RUNESCAPE_GUIDE = "RuneScape Guide";
	private final String SURVIVAL_EXPERT = "Survival Expert";
	private final String COOK_GUIDE = "Master Chef";
	private final String QUEST_GUIDE = "Quest Guide";
	private final String MINING_GUIDE = "Mining Instructor";
	private final String COMBAT_GUIDE = "Combat Instructor";
	private final String FINANCIAL_GUIDE = "Financial Advisor";
	private final String PRAY_GUIDE = "Brother Brace";
	private final String MAGIC_GUIDE = "Magic Instructor";

	Tile[] cookToQuest = new Tile[]{new Tile(3072, 3090, 0), new Tile(3072, 3092, 0), new Tile(3072, 3094, 0), new Tile(3071, 3095, 0), new Tile(3071, 3097, 0), new Tile(3071, 3099, 0), new Tile(3071, 3101, 0), new Tile(3071, 3103, 0), new Tile(3072, 3104, 0), new Tile(3073, 3105, 0), new Tile(3074, 3106, 0), new Tile(3076, 3106, 0), new Tile(3077, 3107, 0), new Tile(3077, 3109, 0), new Tile(3077, 3111, 0), new Tile(3077, 3113, 0), new Tile(3077, 3115, 0), new Tile(3077, 3117, 0), new Tile(3076, 3119, 0), new Tile(3076, 3121, 0), new Tile(3076, 3123, 0), new Tile(3077, 3125, 0), new Tile(3079, 3125, 0), new Tile(3080, 3126, 0), new Tile(3082, 3126, 0), new Tile(3084, 3126, 0), new Tile(3086, 3126, 0)};
	Tile[] combatToLadder = new Tile[]{new Tile(3106, 9509, 0), new Tile(3107, 9510, 0), new Tile(3108, 9511, 0), new Tile(3109, 9512, 0), new Tile(3110, 9513, 0), new Tile(3111, 9515, 0), new Tile(3112, 9516, 0), new Tile(3112, 9518, 0), new Tile(3112, 9520, 0), new Tile(3112, 9522, 0), new Tile(3112, 9524, 0), new Tile(3111, 9525, 0)};
	Tile[] finToPray = new Tile[]{new Tile(3130, 3124, 0), new Tile(3132, 3124, 0), new Tile(3133, 3123, 0), new Tile(3133, 3121, 0), new Tile(3134, 3119, 0), new Tile(3134, 3117, 0), new Tile(3133, 3115, 0), new Tile(3132, 3114, 0), new Tile(3131, 3113, 0), new Tile(3131, 3111, 0), new Tile(3130, 3110, 0), new Tile(3130, 3108, 0)};
	Tile[] prayToMage = new Tile[]{new Tile(3122, 3102, 0), new Tile(3123, 3101, 0), new Tile(3124, 3100, 0), new Tile(3125, 3099, 0), new Tile(3126, 3098, 0), new Tile(3127, 3097, 0), new Tile(3128, 3096, 0), new Tile(3129, 3095, 0), new Tile(3130, 3094, 0), new Tile(3132, 3094, 0), new Tile(3133, 3093, 0), new Tile(3134, 3092, 0), new Tile(3135, 3091, 0), new Tile(3136, 3090, 0), new Tile(3137, 3089, 0), new Tile(3138, 3088, 0), new Tile(3140, 3087, 0), new Tile(3141, 3088, 0)};

	ScriptVars sv = new ScriptVars();
	boolean started = false;
	boolean accMade = false;
	private State state;
	private Timer t;

	private enum State {
		CREATE_ACC, OPEN_TAB, DO_TUT
	}

	private State getState() {
		if (!started) {
			return State.CREATE_ACC;
		}
		if (PlayerSettings.getConfig(TAB_CONFIG) > 0) {
			return State.OPEN_TAB;
		}
		return State.DO_TUT;
	}

	public void onStart(String... args) {
		sv.baseName = args[0];
		sv.pass = args[1];
		sv.age = args[2];
		sv.email = args.length > 3 ? args[3] : null;
		t = new Timer();
	}

	public void onStart() {
		if (!Client.isLoggedIn()) {
			tutIslandGui gui = new tutIslandGui(sv);
			gui.setVisible(true);
			while (!sv.started) {
				sleep(30);
			}
		} else {
			accMade = true;
			started = true;
		}
		t = new Timer();
	}


	private WidgetChild getClickHereToContinue() {
		if (!Dialogues.inDialogue()) {
			log("Not in dialogue");
			return null;
		}
		List<WidgetChild> children = Widgets.getWidgetChildrenContainingText("Click here to continue");
		if (children.isEmpty()) {
			children = Widgets.getWidgetChildrenContainingText("Click to continue");
		}
		if (children.isEmpty()) {
			return null;
		}
		for (WidgetChild wc : children) {
			if (wc.isVisible()) {
				if (wc.getParentID() != 137) {
					if (wc.isGrandChild()) {
						if (wc.getID() != 107 && wc.isVisible())
							return wc;
					} else if (wc.isVisible()) {
						return wc;
					}
				}
			}
		}
		children = Widgets.getWidgetChildrenContainingText("Click to continue");
		for (WidgetChild wc : children) {
			if (wc.isVisible()) {
				if (wc.getParentID() != 137) {
					if (wc.isGrandChild()) {
						if (wc.getID() != 107 && wc.isVisible())
							return wc;
					} else if (wc.isVisible()) {
						return wc;
					}
				}
			}
		}
		return null;
	}

	private boolean isRoofEnabled() {
		return false;
	}

	private int toggleRoof() {
		if (!isRoofEnabled())
			return -1;

		return -1;
	}

	private int makeAccount() {
		log("Login!");
		login();
		Sleep.sleepUntil(() -> Widgets.getWidget(APPEAR_PAR) != null, 30000);

		return Calculations.random(250, 350);
	}

	@Override
	public int onLoop() {
		state = getState();
		if (state != State.CREATE_ACC) {
			List<WidgetChild> clickToContinue = Widgets.getWidgetChildrenContainingText("Click to continue");
			if (!clickToContinue.isEmpty()) {
				WidgetChild wc = clickToContinue.get(0);
				if (wc != null && wc.isVisible()) {
					wc.interact();
					sleep(900, 1200);
				}
			}
		}
		switch (state) {
			case CREATE_ACC:
				if (!Client.isLoggedIn()) {
					int res = makeAccount();
					if (res == -1) {
						return res;
					}
				} else {
					accMade = true;
					t = new Timer();
					log("appearance");
					final Widget par = Widgets.getWidget(APPEAR_PAR);
					if (par != null && par.isVisible()) {
						for (int i = 0; i < appChildren.length; i++) {
							if (Client.seededRandom() >= 1) {
								if (Client.seededRandom() > 1) {
									for (int ii = 0; ii < 5; ii++) {
										par.getChild(appChildren[i][0]).interact();
										sleep(100, 150);
									}
								} else {
									for (int ii = 0; ii < 5; ii++) {
										par.getChild(appChildren[i][1]).interact();
										sleep(100, 150);
									}
								}
								sleep(200, 300);
							}
						}
						par.getChild(ACCEPT).interact();
						Sleep.sleepUntil(() -> {
							WidgetChild wc = par.getChild(ACCEPT);
							return wc == null || !wc.isVisible();
						}, 1200);
						started = true;
					}
				}
				break;
			case DO_TUT:
				int conf = PlayerSettings.getConfig(TUT_PROG);
				switch (conf) {
					case 7:
					case 0:
						talkTo(RUNESCAPE_GUIDE);
						break;
					case 3:
						//open settings
						break;
					case 10:
						if (!Walking.isRunEnabled()) {
							Walking.toggleRun();
						}
						GameObject door = GameObjects.closest("Door");
						if (door != null) {
							if (door.interact("Open")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 10, Calculations.random(1600, 2000));
							}
						}
						//open door
						break;
					case 70:
					case 20:
						talkTo(SURVIVAL_EXPERT);
						//talked to survival expert
						break;
					case 30:
						Dialogues.clickContinue();
						//opened invnetory
						break;
					case 40:
						GameObject tree = GameObjects.closest("Tree");
						if (tree != null) {
							if (Players.getLocal().getAnimation() != -1) {
								Sleep.sleepUntil(() -> Inventory.contains("Logs"), Calculations.random(1000, 2000));
								break;
							}
							if (tree.interact("Chop down")) {
								walkingSleep();
								Sleep.sleepUntil(() -> Inventory.contains("Logs"), Calculations.random(1000, 2000));
							}
						}
						//chopped logs
						break;
					case 50:
						if (!Inventory.contains("Logs")) {
							Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 50, 5000);
							break;
						}
						lightFire();
						//lit logs
						break;
					case 60:
						//opened skills
						break;
					case 80:
						if (Players.getLocal().getAnimation() != -1) {
							Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 80, 5000);
							break;
						}
						NPC pool = NPCs.closest("Fishing spot");
						if (pool != null) {
							if (pool.interact("Net")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 80, Calculations.random(4000, 5000));
							}
						}
						//caught shrimp
						break;
					case 90:
						cookShrimp();
						break;
					case 100:
						if (Dialogues.canContinue()) {
							Dialogues.clickContinue();
							break;
						}
						cookShrimp();
						//burned shrimp
						break;
					case 110:
						if (!Inventory.contains("Raw shrimps")) {
							if (Players.getLocal().getAnimation() != -1) {
								Sleep.sleepUntil(() -> Inventory.contains("Raw shrimps"), 5000);
							} else {
								pool = NPCs.closest("Fishing spot");
								if (pool != null) {
									pool.interact("Net");
									Sleep.sleepUntil(() -> Inventory.contains("Raw shrimps"), 5000);
								}
							}
						} else {
							cookShrimp();
						}
						//cooked second shrimp
						break;
					case 120:
						if (Players.getLocal().getTile().distance(new Tile(3091, 3092, 0)) > 5) {

							Walking.walk(new Tile(3090, 3092, 0));
							walkingSleep();
						} else {
							GameObject gate = GameObjects.closest("Gate");
							if (gate != null) {
								if (gate.interact("Open")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 120, Calculations.random(1400, 1800));
								}
							}
						}
						//went through gate
						break;
					case 130:
						if (Players.getLocal().getTile().distance(new Tile(3080, 3084, 0)) > 5) {
							Walking.walk(new Tile(3080, 3084, 0));
							walkingSleep();
						} else {
							GameObject gate = GameObjects.closest("Door");
							if (gate != null) {
								if (gate.interact("Open")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 130, 1200);
								}
							}
						}
						//went through cook door
						break;
					case 140:
						talkTo(COOK_GUIDE);
						//talked to cook
						break;
					case 150:
						if (!Inventory.isItemSelected()) {
							if (Inventory.interact("Bucket of water", "Use")) {
								Sleep.sleepUntil(Inventory::isItemSelected, Calculations.random(1200, 1400));
							}
						} else {
							if (Inventory.interact("Pot of flour", "Use")) {
								Sleep.sleepUntil(() -> Inventory.contains("Bread dough"), Calculations.random(1200, 1400));
							}
						}
						//made dough
						break;
					case 160:
						if (!Inventory.isItemSelected()) {
							if (Inventory.interact("Bread dough", "Use")) {
								Sleep.sleepUntil(Inventory::isItemSelected, Calculations.random(1200, 1400));
							}
						} else {
							GameObject range = GameObjects.closest("Range");
							if (range.interact("Use")) {
								walkingSleep();
								Sleep.sleepUntil(() -> Inventory.contains("Bread"), Calculations.random(2000, 3000));
							}
						}
						//cooked bread
						break;
					case 170:
						//opened music
						break;
					case 180:
						if (Players.getLocal().getTile().distance(new Tile(3073, 3090, 0)) > 5) {
							Walking.walk(new Tile(3073, 3090, 0));
							walkingSleep();
						} else {
							GameObject gate = GameObjects.closest("Door");
							if (gate != null) {
								if (gate.interact("Open")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 180, Calculations.random(1200, 1600));
								}
							}
						}
						//opened cook door
						break;
					case 183:
						//opened emotes
						break;
					case 187:
						Rectangle r = new Rectangle(560, 213, 20, 40);
						Mouse.move(r);
						Mouse.click();
						Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 187, 5000);
						//use emote
						break;
					case 190:
						//opened settings
						break;
					case 200:
						//261,65
						WidgetChild wc = Widgets.getChildWidget(261, 63);
						if (wc != null && wc.isVisible()) {
							wc.interact();
							Sleep.sleepUntil(Walking::isRunEnabled, 1200);
						}
						//turned on run in settings
						break;
					case 210:
						if (Players.getLocal().getTile().distance(new Tile(3086, 3126, 0)) > 5) {
							Walking.walk(cookToQuest[cookToQuest.length - 1]);//, Calculations.random(10,15));
							Sleep.sleepUntil(() -> Players.getLocal().isMoving(), 1200);
							Sleep.sleepUntil(() -> {
								Tile dest = Client.getDestination();
								return !Players.getLocal().isMoving() || dest == null || Players.getLocal().distance(dest) < 5;
							}, Calculations.random(2600, 3000));
						} else {
							GameObject gate = GameObjects.closest("Door");
							if (gate != null) {
								if (gate.interact("Open")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 210, Calculations.random(1200, 1400));
								}
							}
						}
						//opened door
						break;
					case 240:
					case 220:
						talkTo(QUEST_GUIDE);
						//talked to quest guy
						break;
					case 230:
						//open quest tab
						break;
					case 250:
						GameObject gate = GameObjects.closest("Ladder");
						if (gate != null) {
							if (gate.interact("Climb-down")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 250, Calculations.random(4000, 6000));
							}
						}
						//climb down ladder
						break;
					case 330:
					case 290:
					case 260:
						talkTo(MINING_GUIDE);
						//talk to mining instructor
						break;
					case 270:
						GameObject tin = GameObjects.closest(g -> {
							if (g == null || g.getName() == null)
								return false;
							if (!g.getName().equals("Rocks"))
								return false;
							return g.getTile().equals(new Tile(3077, 9504, 0));
						});
						if (tin != null) {
							if (tin.interact("Prospect")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 270, Calculations.random(3000, 4000));
							}
						}
						//inspect tin
						break;
					case 280:
						tin = GameObjects.closest(g -> {
							if (g == null || g.getName() == null)
								return false;
							if (!g.getName().equals("Rocks"))
								return false;
							return g.getTile().equals(new Tile(3083, 9501, 0));
						});
						if (tin != null) {
							if (tin.interact("Prospect")) {
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 280, Calculations.random(4000, 5000));
							}
						}
						//inspect copper
						break;
					case 300:
						tin = GameObjects.closest(g -> {
							if (g == null || g.getName() == null)
								return false;
							if (!g.getName().equals("Rocks"))
								return false;
							return g.getTile().equals(new Tile(3077, 9504, 0));
						});
						if (tin != null) {
							if (tin.interact("Mine")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 300, Calculations.random(2000, 3000));
							}
						}
						//mine tin
						break;
					case 310:
						tin = GameObjects.closest(g -> {
							if (g == null || g.getName() == null)
								return false;
							if (!g.getName().equals("Rocks"))
								return false;
							return g.getTile().equals(new Tile(3083, 9501, 0));
						});
						if (tin != null) {
							if (tin.interact("Mine")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 310, Calculations.random(2000, 3000));
							}
						}
						//mine copper
						break;
					case 320:
						if (Dialogues.canContinue()) {
							Dialogues.clickContinue();
							sleep(900, 1200);
						} else {
							if (!Inventory.isItemSelected()) {
								sleep(1200, 1800);
								Inventory.interact("Tin ore", "Use");
								Sleep.sleepUntil(Inventory::isItemSelected, Calculations.random(800, 1200));
							} else {
								GameObject furnace = GameObjects.closest(10082);

								if (furnace != null) {
									if (furnace.interact("Use")) {
										walkingSleep();
										Sleep.sleepUntil(() -> Inventory.contains("Bronze bar"), Calculations.random(2000, 3000));
									}
								}
							}
						}
						//smelt bronze
						break;
					case 340:
						if (!Inventory.isItemSelected()) {
							Inventory.interact("Bronze bar", "Use");
							Sleep.sleepUntil(Inventory::isItemSelected, 1200);
						} else {
							GameObject furnace = GameObjects.closest("Anvil");
							if (furnace != null) {
								if (furnace.interact("Use")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 340, Calculations.random(2000, 3000));
								}
							}
						}
						//open anvil panel
						break;
					case 350:
						Widgets.getChildWidget(312, 2).interact();
						Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 350, 3000);
						//smith knife
						break;
					case 360:
						if (Players.getLocal().getTile().distance(new Tile(3094, 9502, 0)) > 5) {
							Walking.walk(new Tile(3094, 9502, 0));
							walkingSleep();
						} else {
							gate = GameObjects.closest("Gate");
							if (gate != null) {
								if (gate.interact("Open")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 360, Calculations.random(1200, 1800));
								}
							}
						}
						//open gate
						break;
					case 410:
					case 370:
						talkTo(COMBAT_GUIDE);
						//tlak to combat instructor
						break;
					case 390:
						//open equipment
						break;
					case 400:
						Widgets.getChildWidget(387, 17).interact();
						Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 400, Calculations.random(1200, 1600));
						//open equipment stats
						break;
					case 405:
						if (Inventory.interact("Bronze dagger", "Equip")) {
							Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 410, Calculations.random(1200, 1600));
						} else if (Inventory.interact("Bronze dagger", "Wield")) {
							Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 410, Calculations.random(1200, 1600));
						}
						Widgets.getChildWidget(84, 4).interact();
						//equip dagger
						break;
					case 420:
						Item i = Equipment.getItemInSlot(EquipmentSlot.WEAPON.getSlot());
						if (i != null && i.getName().contains("dagger")) {
							Equipment.unequip(EquipmentSlot.WEAPON);
						} else {
							if (i != null) {
								Inventory.interact("Wooden shield", "Wield");
								Sleep.sleepUntil(() -> Equipment.getItemInSlot(EquipmentSlot.SHIELD.getSlot()) != null, Calculations.random(1200, 1600));
							} else {
								Inventory.interact("Bronze sword", "Wield");
								Sleep.sleepUntil(() -> Equipment.getItemInSlot(EquipmentSlot.WEAPON.getSlot()) != null, Calculations.random(1200, 1600));
							}
						}
						//unequip knife
						//equip sword/shield
						break;
					case 430:
						//open combat tab
						break;
					case 440:
						if (Players.getLocal().getTile().distance(new Tile(3111, 9518, 0)) > 5) {
							Walking.walk(new Tile(3111, 9518, 0));
							walkingSleep();
						} else {
							gate = GameObjects.closest("Gate");
							if (gate != null) {
								if (gate.interact("Open")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 360, Calculations.random(1200, 1600));
								}
							}
						}
						//open rat gate
						break;
					case 450:
						NPC rat = NPCs.closest(n -> {
							if (n == null || n.getName() == null)
								return false;
							return n.getName().equals("Giant rat") && !n.isInCombat();
						});
						if (rat != null) {
							if (rat.interact("Attack")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 450, Calculations.random(1200, 2000));
							} else {
								if (Camera.getPitch() < Calculations.random(150, 200)) {
									Camera.rotateToPitch(Calculations.random(200, 360));
								}
							}
						}
						//attack rat
						break;
					case 460:
						Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 460, 2400);
						//killed rat
						break;
					case 470:
						if (!Map.canReach(new Tile(3112, 9518, 0))) {
							gate = GameObjects.closest("Gate");
							if (gate != null) {
								if (gate.interact("Open")) {
									walkingSleep();
									Sleep.sleepUntil(() -> Players.getLocal().getTile().getX() == 3111, Calculations.random(1200, 1400));
								}
							}
						} else
							talkTo(COMBAT_GUIDE);
						//talk to combat instructor
						break;
					case 480:
						if (Equipment.isSlotEmpty(EquipmentSlot.ARROWS.getSlot())) {
							Inventory.interact("Bronze arrow", "Wield");
							Sleep.sleepUntil(() -> Equipment.isSlotFull(EquipmentSlot.ARROWS.getSlot()), Calculations.random(1200, 1600));
						} else if (Inventory.contains("Shortbow")) {
							Inventory.interact("Shortbow", "Wield");
							Sleep.sleepUntil(() -> !Inventory.contains("Shortbow"), Calculations.random(1200, 1600));
						} else {
							rat = NPCs.closest(n -> {
								if (n == null || n.getName() == null)
									return false;
								return n.getName().equals("Giant rat") && !n.isInCombat();
							});
							if (rat != null) {
								if (rat.interact("Attack")) {
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 480, Calculations.random(2400, 3600));
								}
							}
						}
						//equip bow & arrow
						//attack rat
						break;
					case 490:
						if (Equipment.isSlotEmpty(EquipmentSlot.ARROWS.getSlot())) {
							Inventory.interact("Bronze arrow", "Wield");
							Sleep.sleepUntil(() -> Equipment.isSlotFull(EquipmentSlot.ARROWS.getSlot()), 1200);
						} else if (Inventory.contains("Shortbow")) {
							Inventory.interact("Shortbow", "Wield");
							Sleep.sleepUntil(() -> !Inventory.contains("Shortbow"), 1200);
						} else {
							if (Players.getLocal().getInteractingCharacter() == null) {
								rat = NPCs.closest(n -> {
									if (n == null || n.getName() == null)
										return false;
									return n.getName().equals("Giant rat") && !n.isInCombat();
								});
								if (rat != null) {
									if (rat.interact("Attack")) {
										Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 480, Calculations.random(2400, 3600));
									}
								}
							} else {
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 490, Calculations.random(2400, 3000));
							}
						}

						//killed rat
						break;
					case 500:
						if (Players.getLocal().getTile().distance(new Tile(3112, 9525, 0)) > 5) {
							Walking.walk(combatToLadder[combatToLadder.length - 1]);//, Calculations.random(10,15));
							walkingSleep();
						} else {
							gate = GameObjects.closest("Ladder");
							if (gate != null) {
								if (gate.interact("Climb-up")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 500, Calculations.random(2400, 3600));
								}
							}
						}
						//go up ladder
						break;
					case 510:
						Tile t = new Tile(3122, 3123, 0);
						if (Players.getLocal().distance(t) > 5) {
							Walking.walk(t);
							walkingSleep();
						} else {
							if (Dialogues.getOptionIndex("Yes.") > 0) {
								Dialogues.clickOption("Yes.");
								Sleep.sleepUntil(Bank::isOpen, Calculations.random(1200, 1600));

								Bank.depositAllItems();
								Sleep.sleepUntil(Inventory::isEmpty, Calculations.random(800, 1200));

								Bank.depositAllEquipment();
								Sleep.sleepUntil(Equipment::isEmpty, Calculations.random(800, 1200));

								Bank.close();
								Sleep.sleepUntil(() -> !Bank.isOpen(), Calculations.random(800, 1200));
							} else {
								if (!Dialogues.canContinue()) {
									GameObject bankBooth = GameObjects.closest("Bank booth");
									if (bankBooth != null) {
										if (bankBooth.interact("Use")) {
											Sleep.sleepUntil(Dialogues::canContinue, Calculations.random(2400, 3000));
										}
									}
								} else {
									Dialogues.clickContinue();
									Sleep.sleepUntil(() -> !Dialogues.canContinue(), Calculations.random(1200, 1400));
								}
							}

						}
						//use bank booth
						//continue through convo
						break;
					case 520:
						if (Bank.isOpen()) {
							Bank.close();
							Sleep.sleepUntil(() -> !Bank.isOpen(), Calculations.random(1200, 1600));
						} else {
							if (PlayerSettings.getConfig(POLL_OPEN) == 0) {
								GameObject pbooth = GameObjects.closest("Poll booth");
								if (pbooth != null) {
									if (pbooth.interact("Use")) {
										walkingSleep();
										Sleep.sleepUntil(Dialogues::canContinue, 2400);
									}
								}
								if (Dialogues.canContinue()) {
									while (Dialogues.canContinue() || PlayerSettings.getConfig(POLL_OPEN) == 0) {
										Dialogues.clickContinue();
										sleep(300, 500);
									}
								}
								log("Poll config: " + PlayerSettings.getConfig(POLL_OPEN));
							}
							sleep(300, 500);
							if (PlayerSettings.getConfig(POLL_OPEN) > 0) {
								WidgetChild bar = Widgets.getChildWidget(310, 1);
								if (bar != null) {
									bar = bar.getChild(11);
								}
								if (bar != null && bar.isVisible()) {
									bar.interact();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(POLL_OPEN) == 0, Calculations.random(1200, 1500));
								}
							}
						}
						//use poll booth
						//continue through convo
						//close poll booth
						break;
					case 525:
						if (PlayerSettings.getConfig(POLL_OPEN) > 0) {
							WidgetChild bar = Widgets.getChildWidget(345, 1);
							if (bar != null) {
								bar = bar.getChild(11);
							}
							if (bar != null && bar.isVisible()) {
								bar.interact();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(POLL_OPEN) == 0, Calculations.random(1200, 1500));
							}
						} else {
							door = GameObjects.closest(g -> {
								if (g == null || g.getName() == null)
									return false;
								if (!g.getName().equals("Door"))
									return false;
								return g.getTile().equals(new Tile(3125, 3124, 0));
							});
							if (door != null) {
								if (door.interact("Open")) {
									walkingSleep();
									Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 525, Calculations.random(1600, 2400));
								}
							}
							//go through door
						}
						break;
					case 530:
						talkTo(FINANCIAL_GUIDE);
						//talk to financial guy
						break;
					case 540:
						door = GameObjects.closest(g -> {
							if (g == null || g.getName() == null)
								return false;
							if (!g.getName().equals("Door"))
								return false;
							return g.getTile().equals(new Tile(3130, 3124, 0));
						});
						if (door != null) {
							if (door.interact("Open")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 540, Calculations.random(1600, 2400));
							}
						}
						//go through door
						break;
					case 550:
						if (Players.getLocal().getTile().distance(new Tile(3126, 3106, 0)) > 5) {
							Walking.walk(finToPray[finToPray.length - 1]);//, Calculations.random(10,15));
							walkingSleep();
						} else {
							gate = GameObjects.closest(g -> {
								if (g == null || g.getName() == null) {
									return false;
								}
								if (!g.getName().equals("Large door"))
									return false;
								return g.getTile().equals(new Tile(3129, 3107, 0));
							});
							if (gate != null && !Map.canReach(NPCs.closest(PRAY_GUIDE).getTile())) {
								if (gate.interact("Open")) {
									sleep(600, 900);
								}
							}
							talkTo(PRAY_GUIDE);
						}
						//3128,3107
						//check if double doors closed
						//talk to prayer guy
						break;
					case 560:
						//open pray tab
						break;
					case 600:
					case 570:
						talkTo(PRAY_GUIDE);
						//talk to pray guy
						break;
					case 580:
						//open friends
						break;
					case 590:
						//open ignore
						break;
					case 610:
						door = GameObjects.closest("Door");
						if (door != null) {
							if (door.interact("Open")) {
								walkingSleep();
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 610, Calculations.random(1600, 2400));
							}
						}
						//open door
						break;
					case 620:
						if (Players.getLocal().getTile().distance(new Tile(3141, 3088, 0)) > 5) {
							Walking.walk(prayToMage[prayToMage.length - 1]);//;,Calculations.random(10,15));
							walkingSleep();
						} else {
							talkTo(MAGIC_GUIDE);
						}
						//talkt o mage guy
						break;
					case 630:
						//open magic tab
						break;
					case 640:
						talkTo(MAGIC_GUIDE);
						//talk to mage guy
						break;
					case 650:
						if (Players.getLocal().getTile().distance(new Tile(3139, 3091, 0)) > 2) {
							Walking.walk(new Tile(3139, 3091, 0));
							walkingSleep();
						} else {
							if (Magic.castSpellOn(Normal.WIND_STRIKE, NPCs.closest("Chicken"))) {
								Sleep.sleepUntil(() -> PlayerSettings.getConfig(TUT_PROG) != 650, Calculations.random(1600, 2400));
							}
						}
						//click spell
						//click chicken
						break;
					case 670:
						if (Dialogues.getOptions() == null) {
							talkTo(MAGIC_GUIDE);
						} else {
							Dialogues.clickOption(1);
						}
						break;
					case 1000:
						log("You're done!");
						log("Finished in: " + this.t.formatTime());
						log("Username: " + sv.finalName);
						log("Email: " + sv.finalName + "@gmail.com");
						log("Password: " + sv.pass);
						return -1;
				}
				break;
			case OPEN_TAB:
				if (Dialogues.canContinue()) {
					Dialogues.clickContinue();
				}
				final Tab t = getTab();
				if (t == null) {
					log("Tab is null?");
					break;
				}
				if (Tabs.openWithMouse(t)) {
					Sleep.sleepUntil(() -> Tabs.isOpen(t), Calculations.random(1200, 1600));
				}
				break;
		}
		return Calculations.random(200, 300);
	}

	private void login() {
		LoginUtility.login(sv.email, sv.pass);
		sleep(800, 1200);
	}

	private void cookShrimp() {
		GameObject fire = GameObjects.closest("Fire");
		if (fire == null)
			lightFire();
		else {
			if (!Inventory.isItemSelected()) {
				if (Inventory.interact("Raw shrimps", "Use")) {
					Sleep.sleepUntil(Inventory::isItemSelected, Calculations.random(800, 1200));
				}
			}
			if (Inventory.isItemSelected()) {
				if (fire.interact("Use")) {
					walkingSleep();
					Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, Calculations.random(2000, 3000));
					Sleep.sleepUntil(() -> Players.getLocal().getAnimation() == -1, Calculations.random(2000, 3000));
				}
			}
		}
	}

	private void lightFire() {
		if (!Inventory.contains("Logs")) {
			GameObject tree = GameObjects.closest("Tree");
			if (tree != null) {
				if (tree.interact("Chop down")) {
					walkingSleep();
					Sleep.sleepUntil(() -> Inventory.contains("Logs"), Calculations.random(1200, 1600));
				}
				if (Players.getLocal().getAnimation() != -1) {
					Sleep.sleepUntil(() -> Inventory.contains("Logs"), Calculations.random(4000, 5000));
				}
			}
		}
		if (Inventory.contains("Logs")) {
			if (!Inventory.isItemSelected()) {
				Inventory.interact("Tinderbox", "Use");
				Sleep.sleepUntil(Inventory::isItemSelected, Calculations.random(800, 1200));
			}
			if (Inventory.isItemSelected()) {
				Inventory.interact("Logs", "Use");
				Sleep.sleepUntil(() -> Players.getLocal().getAnimation() != -1, Calculations.random(1200, 1600));
				Sleep.sleepUntil(() -> Players.getLocal().getAnimation() == -1, Calculations.random(6000, 8000));
			}
		}
	}

	private void talkTo(String npc) {
		List<WidgetChild> clickToContinue = Widgets.getWidgetChildrenContainingText("Click to continue");
		if (!clickToContinue.isEmpty()) {
			WidgetChild wc = clickToContinue.get(0);
			if (wc != null && wc.isVisible()) {
				log("Interacting with widget");
				wc.interact();
				sleep(900, 1200);
			}
		}
		if (!Dialogues.canContinue()) {
			final NPC guide = NPCs.closest(npc);
			if (guide != null) {
				if (guide.isOnScreen()) {
					if (guide.interact("Talk-to")) {
						walkingSleep();
						Sleep.sleepUntil(Dialogues::canContinue, Calculations.random(1200, 1600));
					}
				} else {
					Walking.walk(guide);
					walkingSleep();
				}
			}
		} else {
			log("Clicking continue");
			Dialogues.clickContinue();
			sleep(600, 900);
		}
	}

	private final Tab[] tabs = new Tab[]{Tab.COMBAT, Tab.SKILLS, Tab.QUEST, Tab.INVENTORY, Tab.EQUIPMENT,
			Tab.PRAYER, Tab.MAGIC, Tab.CLAN, Tab.ACCOUNT_MANAGEMENT, Tab.FRIENDS, Tab.LOGOUT, Tab.OPTIONS, Tab.EMOTES, Tab.MUSIC};

	private Tab getTab() {
		int conf = PlayerSettings.getConfig(1021) & 15;
		conf -= 1;
		int conf2 = PlayerSettings.getConfig(281);
		if (conf2 == 580) {
			conf = 9;
		}
		if (conf2 == 590)
			conf = 8;
		if (conf >= 0 && conf < tabs.length) {
			return tabs[conf];
		}
		return null;
	}

	private void walkingSleep() {
		Sleep.sleepUntil(() -> Players.getLocal().isMoving(), Calculations.random(1200, 1600));
		Sleep.sleepUntil(() -> !Players.getLocal().isMoving(), Calculations.random(2400, 3600));
	}

	public void onPaint(Graphics g) {
		if (state != null) {
			g.drawString("State: " + state, 10, 25);
		}
		g.drawString("Current progress: " + PlayerSettings.getConfig(TUT_PROG), 10, 40);
		if (t != null)
			g.drawString("Runtime: " + t.formatTime(), 10, 55);
		if (!sv.finalName.equals(""))
			g.drawString("Final username: " + sv.finalName, 10, 70);
		if (accMade) {
			g.drawString("Login index: " + Client.getLoginIndex(), 10, 85);
		}
	}

}

package nezz.dreambot.scriptmain.tutisland;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import nezz.dreambot.accountcreate.AccountCreate;
import nezz.dreambot.filemethods.FileMethods;
import nezz.dreambot.tutisland.gui.ScriptVars;
import nezz.dreambot.tutisland.gui.tutIslandGui;

import org.dreambot.api.data.GameState;
import org.dreambot.api.input.event.impl.InteractionEvent;
import org.dreambot.api.input.mouse.destination.impl.shape.RectangleDestination;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.filter.*;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

@ScriptManifest(author = "Nezz", category = Category.MISC, description = "Does tutorial island", name = "DreamBot Tutorial Island", version = 0)
public class TutorialIsland extends AbstractScript{

	private final int TAB_CONFIG = 1021;
	private final int PERC_COMP = 406;
	private final int ATT_SPEED = 843;
	private final int POLL_OPEN = 375;
	private final int TUT_PROG = 281;
	private final int APPEAR_PAR = 269;
	private final int[][] appChildren = {{106,113},{107,114},{108,115},{109,116},{110,117},{111,118},{112,119},{105,121},{123,127},{122,129},{124,130},{125,131}};
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

	Tile[] cookToQuest = new Tile[]{new Tile(3072,3090,0),new Tile(3072,3092,0),new Tile(3072,3094,0),new Tile(3071,3095,0),new Tile(3071,3097,0),new Tile(3071,3099,0),new Tile(3071,3101,0),new Tile(3071,3103,0),new Tile(3072,3104,0),new Tile(3073,3105,0),new Tile(3074,3106,0),new Tile(3076,3106,0),new Tile(3077,3107,0),new Tile(3077,3109,0),new Tile(3077,3111,0),new Tile(3077,3113,0),new Tile(3077,3115,0),new Tile(3077,3117,0),new Tile(3076,3119,0),new Tile(3076,3121,0),new Tile(3076,3123,0),new Tile(3077,3125,0),new Tile(3079,3125,0),new Tile(3080,3126,0),new Tile(3082,3126,0),new Tile(3084,3126,0),new Tile(3086,3126,0)};
	Tile[] combatToLadder = new Tile[]{new Tile(3106,9509,0),new Tile(3107,9510,0),new Tile(3108,9511,0),new Tile(3109,9512,0),new Tile(3110,9513,0),new Tile(3111,9515,0),new Tile(3112,9516,0),new Tile(3112,9518,0),new Tile(3112,9520,0),new Tile(3112,9522,0),new Tile(3112,9524,0),new Tile(3111,9525,0)};
	Tile[] finToPray = new Tile[]{new Tile(3130,3124,0),new Tile(3132,3124,0),new Tile(3133,3123,0),new Tile(3133,3121,0),new Tile(3134,3119,0),new Tile(3134,3117,0),new Tile(3133,3115,0),new Tile(3132,3114,0),new Tile(3131,3113,0),new Tile(3131,3111,0),new Tile(3130,3110,0),new Tile(3130,3108,0)};
	Tile[] prayToMage = new Tile[]{new Tile(3122,3102,0),new Tile(3123,3101,0),new Tile(3124,3100,0),new Tile(3125,3099,0),new Tile(3126,3098,0),new Tile(3127,3097,0),new Tile(3128,3096,0),new Tile(3129,3095,0),new Tile(3130,3094,0),new Tile(3132,3094,0),new Tile(3133,3093,0),new Tile(3134,3092,0),new Tile(3135,3091,0),new Tile(3136,3090,0),new Tile(3137,3089,0),new Tile(3138,3088,0),new Tile(3140,3087,0),new Tile(3141,3088,0)};

	private boolean hover = false;
	
	ScriptVars sv = new ScriptVars();
	boolean started = false;
	boolean accMade = false;
	private State state;
	private Timer t;
	private enum State{
		CREATE_ACC, OPEN_TAB, DO_TUT
	}

	private State getState(){
		if(!started){
			return State.CREATE_ACC;
		}
		if(getPlayerSettings().getConfig(TAB_CONFIG) > 0){
			return State.OPEN_TAB;
		}
		return State.DO_TUT;
	}

	public void onStart(){
		if(!getClient().isLoggedIn()){
			tutIslandGui gui = new tutIslandGui(sv);
			gui.setVisible(true);
			while(!sv.started){
				sleep(30);
			}
			if(!accMade){
				int made = -1;
				log("Creating acc");
				String tempName = sv.baseName;
				while(tempName.contains("#")){
					String rep = ""+Calculations.random(0,10);
					log("Swapping # with " + rep);
					tempName = tempName.replaceFirst("#", rep);
					log(tempName);
				}
				log("Trying name: " + tempName);
				AccountCreate ac = new AccountCreate(getClient().getInstance());
				try {
					made = ac.makeAccount(tempName, tempName+"@gmail.com",sv.pass, sv.age);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(made == 0){
					log("Success! Username is: " + tempName);
					accMade = true;
					sv.finalName = tempName;
					FileMethods fm = new FileMethods("tutIslandDA");
					fm.appendFile(tempName + "@gmail.com" + ":" + sv.pass,"tutIslandAccs");
				}
				else{
					log("Creation failed!");
					if(!sv.baseName.contains("#")){
						log("Your username is taken, please try again with another name!");
						return;
					}
					accMade = false;
				}
			}
			else{
				log("Login!");
				login();
				sleepUntil(new Condition(){
					public boolean verify(){
						return getWidgets().getWidget(APPEAR_PAR)!=null;
					}
				},5000);
			}
		}
	}


	@Override
	public int onLoop() {
		state = getState();
		switch(state){
		case CREATE_ACC:
			if(!getClient().isLoggedIn()){
				if(!accMade){
					int made = -1;
					log("Creating acc");
					String tempName = sv.baseName;
					while(tempName.contains("#")){
						String rep = ""+Calculations.random(0,10);
						log("Swapping # with " + rep);
						tempName = tempName.replaceFirst("#", rep);
						log(tempName);
					}
					log("Trying name: " + tempName);
					AccountCreate ac = new AccountCreate(getClient().getInstance());
					try {
						made = ac.makeAccount(tempName, tempName+"@gmail.com",sv.pass, sv.age);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(made == 0){
						log("Success! Username is: " + tempName);
						accMade = true;
						sv.finalName = tempName;
						FileMethods fm = new FileMethods("tutIsland");
						fm.appendFile(tempName + "@gmail.com" + ":" + sv.pass,"tutIslandAccs");
					}
					else{
						log("Creation failed!");
						if(!sv.baseName.contains("#")){
							log("Your username is taken, please try again with another name!");
							return -1;
						}
						accMade = false;
					}
				}
				else{
					log("Login!");
					login();
					sleepUntil(new Condition(){
						public boolean verify(){
							return getWidgets().getWidget(APPEAR_PAR)!=null;
						}
					},30000);
				}
			}
			else{
				t = new Timer();
				log("appearance");
				final Widget par = getWidgets().getWidget(APPEAR_PAR);
				if(par != null && par.isVisible()){
					for(int i =0; i < appChildren.length; i++){
						if(getClient().seededRandom() >= 1){
							if(getClient().seededRandom() > 1){
								for(int ii = 0; ii < 5; ii++){
									par.getChild(appChildren[i][0]).interact();
									sleep(100,150);
								}
							}
							else{
								for(int ii = 0; ii < 5; ii++){
									par.getChild(appChildren[i][1]).interact();
									sleep(100,150);
								}
							}
							sleep(200,300);
						}
					}
					par.getChild(ACCEPT).interact();
					sleepUntil(new Condition(){
						public boolean verify(){
							WidgetChild wc = par.getChild(ACCEPT);
							return wc == null || !wc.isVisible();
						}
					},1200);
				}
				started = true;
			}
			break;
		case DO_TUT:
			int conf = getPlayerSettings().getConfig(TUT_PROG);
			switch(conf){
			case 7:
			case 0:
				talkTo(RUNESCAPE_GUIDE);
				break;
			case 3:
				//open settings
				break;
			case 10:
				if(!getWalking().isRunEnabled()){
					getWalking().toggleRun();
				}
				GameObject door = getGameObjects().closest("Door");
				if(door != null){
					if(door.interact("Open")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 10;
							}
						},Calculations.random(1600,2000));
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
				getDialogues().clickContinue();
				//opened invnetory
				break;
			case 40:
				GameObject tree = getGameObjects().closest("Tree");
				if(tree != null){
					if(getLocalPlayer().getAnimation() != -1){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().contains("Logs");
							}
						},Calculations.random(1000,2000));
						break;
					}
					if(tree.interact("Chop down")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().contains("Logs");
							}
						},Calculations.random(1000,2000));
					}
				}
				//chopped logs
				break;
			case 50:
				if(!getInventory().contains("Logs")){
					sleepUntil(new Condition(){
						public boolean verify(){
							return getPlayerSettings().getConfig(TUT_PROG) != 50;
						}
					},5000);
					break;
				}
				lightFire();
				//lit logs
				break;
			case 60:
				//opened skills
				break;
			case 80:
				if(getLocalPlayer().getAnimation() != -1){
					sleepUntil(new Condition(){
						public boolean verify(){
							return getPlayerSettings().getConfig(TUT_PROG) != 80;
						}
					},5000);
					break;
				}
				NPC pool = getNpcs().closest("Fishing spot");
				if(pool != null){
					if(pool.interact("Net")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 80;
							}
						},Calculations.random(4000,5000));
					}
				}
				//caught shrimp
				break;
			case 90:
				cookShrimp();
				break;
			case 100:
				if(getDialogues().canContinue()){
					getDialogues().clickContinue();
					break;
				}
				cookShrimp();
				//burned shrimp
				break;
			case 110:
				if(!getInventory().contains("Raw shrimps")){
					if(getLocalPlayer().getAnimation() != -1){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().contains("Raw shrimps");
							}
						},5000);
					}
					else{
						pool = getNpcs().closest("Fishing spot");
						if(pool != null){
							pool.interact("Net");
							sleepUntil(new Condition(){
								public boolean verify(){
									return getInventory().contains("Raw shrimps");
								}
							},5000);
						}
					}
				}
				else{
					cookShrimp();
				}
				//cooked second shrimp
				break;
			case 120:
				if(getLocalPlayer().getTile().distance(new Tile(3091,3092,0)) > 5){
					
					getWalking().walk(new Tile(3090,3092,0));
					walkingSleep();
				}
				else{
					GameObject gate = getGameObjects().closest("Gate");
					if(gate != null){
						if(gate.interact("Open")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) != 120;
								}
							},Calculations.random(1400,1800));
						}
					}
				}
				//went through gate
				break;
			case 130:
				if(getLocalPlayer().getTile().distance(new Tile(3080,3084,0)) > 5){
					getWalking().walk(new Tile(3080,3084,0));
					walkingSleep();
				}
				else{
					GameObject gate = getGameObjects().closest("Door");
					if(gate != null){
						if(gate.interact("Open")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) != 130;
								}
							},1200);
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
				if(!getInventory().isItemSelected()){
					if(getInventory().interact("Bucket of water", "Use")){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().isItemSelected();
							}
						}, Calculations.random(1200,1400));
					}
				}
				else{
					if(getInventory().interact("Pot of flour", "Use")){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().contains("Bread dough");
							}
						},Calculations.random(1200,1400));
					}
				}
				//made dough
				break;
			case 160:
				if(!getInventory().isItemSelected()){
					if(getInventory().interact("Bread dough", "Use")){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().isItemSelected();
							}
						},Calculations.random(1200,1400));
					}
				}
				else{
					GameObject range = getGameObjects().closest("Range");
					if(range.interact("Use")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().contains("Bread");
							}
						},Calculations.random(2000,3000));
					}
				}
				//cooked bread
				break;
			case 170:
				//opened music
				break;
			case 180:
				if(getLocalPlayer().getTile().distance(new Tile(3073,3090,0)) > 5){
					getWalking().walk(new Tile(3073,3090,0));
					walkingSleep();
				}
				else{
					GameObject gate = getGameObjects().closest("Door");
					if(gate != null){
						if(gate.interact("Open")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) != 180;
								}
							},Calculations.random(1200,1600));
						}
					}
				}
				//opened cook door
				break;
			case 183:
				//opened emotes
				break;
			case 187:
				Rectangle r = new Rectangle(560,213,20,40);
				getMouse().move(r);
				getMouse().click();
				sleepUntil(new Condition(){
					public boolean verify(){
						return getPlayerSettings().getConfig(TUT_PROG) != 187;
					}
				},5000);
				//use emote
				break;
			case 190:
				//opened settings
				break;
			case 200:
				//261,65
				WidgetChild wc = getWidgets().getChildWidget(261,52);
				if(wc != null && wc.isVisible()){
					wc.interact();
					sleepUntil(new Condition(){
						public boolean verify(){
							return getWalking().isRunEnabled();
						}
					},1200);
				}
				//turned on run in settings
				break;
			case 210:
				if(getLocalPlayer().getTile().distance(new Tile(3086,3126,0)) > 5){
					getWalking().walk(cookToQuest[cookToQuest.length - 1]);//, Calculations.random(10,15));
					sleepUntil(new Condition(){
						public boolean verify(){
							return getLocalPlayer().isMoving();
						}
					},1200);
					sleepUntil(new Condition(){
						public boolean verify(){
							Tile dest = getClient().getDestination();
							return !getLocalPlayer().isMoving() || dest == null || getLocalPlayer().distance(dest) < 5;
						}
					},Calculations.random(2600,3000));
				}
				else{
					GameObject gate = getGameObjects().closest("Door");
					if(gate != null){
						if(gate.interact("Open")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) != 210;
								}
							},Calculations.random(1200,1400));
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
				GameObject gate = getGameObjects().closest("Ladder");
				if(gate != null){
					if(gate.interact("Climb-down")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 250;
							}
						},Calculations.random(4000,6000));
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
				GameObject tin = getGameObjects().closest(new Filter<GameObject>(){
					public boolean match(GameObject g){
						if(g == null || g.getName() == null)
							return false;
						if(!g.getName().equals("Rocks"))
							return false;
						if(g.getTile().equals(new Tile(3077,9504,0)))
							return true;
						return false;
					}
				});
				if(tin != null){
					if(tin.interact("Prospect")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG)!=270;
							}
						},Calculations.random(3000,4000));
					}
				}
				//inspect tin
				break;
			case 280:
				tin = getGameObjects().closest(new Filter<GameObject>(){
					public boolean match(GameObject g){
						if(g == null || g.getName() == null)
							return false;
						if(!g.getName().equals("Rocks"))
							return false;
						if(g.getTile().equals(new Tile(3083,9501,0)))
							return true;
						return false;
					}
				});
				if(tin != null){
					if(tin.interact("Prospect")){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG)!=280;
							}
						},Calculations.random(4000,5000));
					}
				}
				//inspect copper
				break;
			case 300:
				tin = getGameObjects().closest(new Filter<GameObject>(){
					public boolean match(GameObject g){
						if(g == null || g.getName() == null)
							return false;
						if(!g.getName().equals("Rocks"))
							return false;
						if(g.getTile().equals(new Tile(3077,9504,0)))
							return true;
						return false;
					}
				});
				if(tin != null){
					if(tin.interact("Mine")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG)!=300;
							}
						},Calculations.random(2000,3000));
					}
				}
				//mine tin
				break;
			case 310:
				tin = getGameObjects().closest(new Filter<GameObject>(){
					public boolean match(GameObject g){
						if(g == null || g.getName() == null)
							return false;
						if(!g.getName().equals("Rocks"))
							return false;
						if(g.getTile().equals(new Tile(3083,9501,0)))
							return true;
						return false;
					}
				});
				if(tin != null){
					if(tin.interact("Mine")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG)!=310;
							}
						},Calculations.random(2000,3000));
					}
				}
				//mine copper
				break;
			case 320:
				if(getDialogues().canContinue()){
					getDialogues().clickContinue();
					sleep(900,1200);
				}
				else{
					if(!getInventory().isItemSelected()){
						sleep(1200,1800);
						getInventory().interact("Tin ore", "Use");
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().isItemSelected();
							}
						},Calculations.random(800,1200));
					}
					else{
						GameObject furnace = getGameObjects().closest(10082);
						
						if(furnace != null){
							if(furnace.interact("Use")){
								walkingSleep();
								sleepUntil(new Condition(){
									public boolean verify(){
										return getInventory().contains("Bronze bar");
									}
								},Calculations.random(2000,3000));
							}
						}
					}
				}
				//smelt bronze
				break;
			case 340:
				if(!getInventory().isItemSelected()){
					getInventory().interact("Bronze bar", "Use");
					sleepUntil(new Condition(){
						public boolean verify(){
							return getInventory().isItemSelected();
						}
					},1200);
				}
				else{
					GameObject furnace = getGameObjects().closest("Anvil");
					if(furnace != null){
						if(furnace.interact("Use")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) != 340;
								}
							},Calculations.random(2000,3000));
						}
					}
				}
				//open anvil panel
				break;
			case 350:
				getWidgets().getChildWidget(312,2).interact();
				sleepUntil(new Condition(){
					public boolean verify(){
						return getPlayerSettings().getConfig(TUT_PROG) != 350;
					}
				},3000);
				//smith knife
				break;
			case 360:
				if(getLocalPlayer().getTile().distance(new Tile(3094,9502,0)) > 5){
					getWalking().walk(new Tile(3094,9502,0));
					walkingSleep();
				}
				else{
					gate = getGameObjects().closest("Gate");
					if(gate != null){
						if(gate.interact("Open")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) !=360;
								}
							},Calculations.random(1200,1800));
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
				getWidgets().getChildWidget(387,17).interact();
				sleepUntil(new Condition(){
					public boolean verify(){
						return getPlayerSettings().getConfig(TUT_PROG) != 400;
					}
				},Calculations.random(1200,1600));
				//open equipment stats
				break;
			case 405:
				if(getInventory().interact("Bronze dagger", "Equip")){
					sleepUntil(new Condition(){
						public boolean verify(){
							return getPlayerSettings().getConfig(TUT_PROG) != 410;
						}
					},Calculations.random(1200,1600));
				}
				else if(getInventory().interact("Bronze dagger", "Wield")){
					sleepUntil(new Condition(){
						public boolean verify(){
							return getPlayerSettings().getConfig(TUT_PROG) != 410;
						}
					},Calculations.random(1200,1600));
				}
				getWidgets().getChildWidget(84,4).interact();
				//equip dagger
				break;
			case 420:
				Item i = getEquipment().getItemInSlot(EquipmentSlot.WEAPON.getSlot());
				if(i != null && i.getName().contains("dagger")){
					getEquipment().unequip(EquipmentSlot.WEAPON);
				}
				else{
					if(i != null){
						getInventory().interact("Wooden shield", "Wield");
						sleepUntil(new Condition(){
							public boolean verify(){
								return getEquipment().getItemInSlot(EquipmentSlot.SHIELD.getSlot()) != null;
							}
						},Calculations.random(1200,1600));
					}
					else{
						getInventory().interact("Bronze sword", "Wield");
						sleepUntil(new Condition(){
							public boolean verify(){
								return getEquipment().getItemInSlot(EquipmentSlot.WEAPON.getSlot()) != null;
							}
						},Calculations.random(1200,1600));
					}
				}
				//unequip knife
				//equip sword/shield
				break;
			case 430:
				//open combat tab
				break;
			case 440:
				if(getLocalPlayer().getTile().distance(new Tile(3111,9518,0)) > 5){
					getWalking().walk(new Tile(3111,9518,0));
					walkingSleep();
				}
				else{
					gate = getGameObjects().closest("Gate");
					if(gate != null){
						if(gate.interact("Open")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) !=360;
								}
							},Calculations.random(1200,1600));
						}
					}
				}
				//open rat gate
				break;
			case 450:
				NPC rat = getNpcs().closest(new Filter<NPC>(){
					public boolean match(NPC n){
						if(n == null || n.getName() == null)
							return false;
						return n.getName().equals("Giant rat") && !n.isInCombat();
					}
				});
				if(rat != null){
					if(rat.interact("Attack")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 450;
							}
						},Calculations.random(1200,2000));
					}
					else{
						if(getCamera().getPitch() < Calculations.random(150,200)){
							getCamera().rotateToPitch(Calculations.random(200,360));
						}
					}
				}
				//attack rat
				break;
			case 460:
				sleepUntil(new Condition(){
					public boolean verify(){
						return getPlayerSettings().getConfig(TUT_PROG) != 460;
					}
				},2400);
				//killed rat
				break;
			case 470:
				if(!getMap().canReach(new Tile(3112,9518,0))){
					gate = getGameObjects().closest("Gate");
					if(gate != null){
						if(gate.interact("Open")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getLocalPlayer().getTile().getX() == 3111;
								}
							},Calculations.random(1200,1400));
						}
					}
				}
				else
					talkTo(COMBAT_GUIDE);
				//talk to combat instructor
				break;
			case 480:
				if(getEquipment().isSlotEmpty(EquipmentSlot.ARROWS.getSlot())){
					getInventory().interact("Bronze arrow", "Wield");
					sleepUntil(new Condition(){
						public boolean verify(){
							return getEquipment().isSlotFull(EquipmentSlot.ARROWS.getSlot());
						}
					},Calculations.random(1200,1600));
				}
				else if(getInventory().contains("Shortbow")){
					getInventory().interact("Shortbow", "Wield");
					sleepUntil(new Condition(){
						public boolean verify(){
							return !getInventory().contains("Shortbow");
						}
					},Calculations.random(1200,1600));
				}
				else{
					rat = getNpcs().closest(new Filter<NPC>(){
						public boolean match(NPC n){
							if(n == null || n.getName() == null)
								return false;
							return n.getName().equals("Giant rat") && !n.isInCombat();
						}
					});
					if(rat != null){
						if(rat.interact("Attack")){
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) != 480;
								}
							},Calculations.random(2400, 3600));
						}
					}
				}
				//equip bow & arrow
				//attack rat
				break;
			case 490:
				if(getEquipment().isSlotEmpty(EquipmentSlot.ARROWS.getSlot())){
					getInventory().interact("Bronze arrow", "Wield");
					sleepUntil(new Condition(){
						public boolean verify(){
							return getEquipment().isSlotFull(EquipmentSlot.ARROWS.getSlot());
						}
					},1200);
				}
				else if(getInventory().contains("Shortbow")){
					getInventory().interact("Shortbow", "Wield");
					sleepUntil(new Condition(){
						public boolean verify(){
							return !getInventory().contains("Shortbow");
						}
					},1200);
				}
				else{
					if(getLocalPlayer().getInteractingCharacter() == null){
						rat = getNpcs().closest(new Filter<NPC>(){
							public boolean match(NPC n){
								if(n == null || n.getName() == null)
									return false;
								return n.getName().equals("Giant rat") && !n.isInCombat();
							}
						});
						if(rat != null){
							if(rat.interact("Attack")){
								sleepUntil(new Condition(){
									public boolean verify(){
										return getPlayerSettings().getConfig(TUT_PROG) != 480;
									}
								},Calculations.random(2400, 3600));
							}
						}
					}
					else{
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 490;
							}
						},Calculations.random(2400,3000));
					}
				}
				
				//killed rat
				break;
			case 500:
				if(getLocalPlayer().getTile().distance(new Tile(3112,9525,0)) > 5){
					getWalking().walk(combatToLadder[combatToLadder.length-1]);//, Calculations.random(10,15));
					walkingSleep();
				}
				else{
					gate = getGameObjects().closest("Ladder");
					if(gate != null){
						if(gate.interact("Climb-up")){
							walkingSleep();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(TUT_PROG) != 500;
								}
							},Calculations.random(2400,3600));
						}
					}
				}
				//go up ladder
				break;
			case 510:
				Tile t = new Tile(3122,3123,0);
				if(getLocalPlayer().distance(t) > 5){
					getWalking().walk(t);
					walkingSleep();
				}
				else{
					if(getDialogues().getOptionIndex("Yes.") > 0){
						getDialogues().clickOption("Yes.");
						sleepUntil(new Condition(){
							public boolean verify(){
								return getBank().isOpen();
							}
						},Calculations.random(1200,1600));
						
						getBank().depositAllItems();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getInventory().isEmpty();
							}
						},Calculations.random(800,1200));
						
						getBank().depositAllEquipment();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getEquipment().isEmpty();
							}
						},Calculations.random(800,1200));
						
						getBank().close();
						sleepUntil(new Condition(){
							public boolean verify(){
								return !getBank().isOpen();
							}
						}, Calculations.random(800,1200));
					}
					else{
						if(!getDialogues().canContinue()){
							GameObject bankBooth = getGameObjects().closest("Bank booth");
							if(bankBooth != null){
								if(bankBooth.interact("Use")){
									sleepUntil(new Condition(){
										public boolean verify(){
											return getDialogues().canContinue();
										}
									},Calculations.random(2400,3000));
								}
							}
						}
						else{
							getDialogues().clickContinue();
							sleepUntil(new Condition(){
								public boolean verify(){
									return !getDialogues().canContinue();
								}
							},Calculations.random(1200,1400));
						}
					}

				}
				//use bank booth
				//continue through convo
				break;
			case 520:
				if(getBank().isOpen()){
					getBank().close();
					sleepUntil(new Condition(){
						public boolean verify(){
							return !getBank().isOpen();
						}
					},Calculations.random(1200,1600));
				}
				else{
					if(getPlayerSettings().getConfig(POLL_OPEN) == 0){
						GameObject pbooth = getGameObjects().closest("Poll booth");
						if(pbooth != null){
							if(pbooth.interact("Use")){
								walkingSleep();
								sleepUntil(new Condition(){
									public boolean verify(){
										return getDialogues().canContinue();
									}
								},2400);
							}
						}
						while(getDialogues().canContinue()){//getPlayerSettings().getConfig(POLL_OPEN) == 0){
							getDialogues().clickContinue();
							sleep(300,500);
						}
						log("Poll config: " + getPlayerSettings().getConfig(POLL_OPEN));
					}
					sleep(300,500);
					if(getPlayerSettings().getConfig(POLL_OPEN) > 0){
						WidgetChild bar = getWidgets().getChildWidget(345,1);
						if(bar != null){
							bar = bar.getChild(11);
						}
						if(bar != null && bar.isVisible()){
							bar.interact();
							sleepUntil(new Condition(){
								public boolean verify(){
									return getPlayerSettings().getConfig(POLL_OPEN) == 0;
								}
							}, Calculations.random(1200,1500));
						}
					}
				}
				//use poll booth
				//continue through convo
				//close poll booth
				break;
			case 525:
				if(getPlayerSettings().getConfig(POLL_OPEN) > 0){
					WidgetChild bar = getWidgets().getChildWidget(345,1);
					if(bar != null){
						bar = bar.getChild(11);
					}
					if(bar != null && bar.isVisible()){
						bar.interact();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(POLL_OPEN) == 0;
							}
						}, Calculations.random(1200,1500));
					}
				}
				else{
				door = getGameObjects().closest(new Filter<GameObject>(){
					public boolean match(GameObject g){
						if(g == null || g.getName() == null)
							return false;
						if(!g.getName().equals("Door"))
							return false;
						return g.getTile().equals(new Tile(3125,3124,0));
					}
				});
				if(door != null){
					if(door.interact("Open")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 525;
							}
						},Calculations.random(1600,2400));
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
				door = getGameObjects().closest(new Filter<GameObject>(){
					public boolean match(GameObject g){
						if(g == null || g.getName() == null)
							return false;
						if(!g.getName().equals("Door"))
							return false;
						return g.getTile().equals(new Tile(3130,3124,0));
					}
				});
				if(door != null){
					if(door.interact("Open")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 540;
							}
						},Calculations.random(1600,2400));
					}
				}
				//go through door
				break;
			case 550:
				if(getLocalPlayer().getTile().distance(new Tile(3126,3106,0)) > 5){
					getWalking().walk(finToPray[finToPray.length-1]);//, Calculations.random(10,15));
					walkingSleep();
				}
				else{
					gate = getGameObjects().closest(new Filter<GameObject>(){
						public boolean match(GameObject g){
							if(g == null || g.getName() == null){
								return false;
							}
							if(!g.getName().equals("Large door"))
								return false;
							return g.getTile().equals(new Tile(3129,3107,0));
						}
					});
					if(gate != null && !getMap().canReach(getNpcs().closest(PRAY_GUIDE).getTile())){
						if(gate.interact("Open")){
							sleep(600,900);
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
				door = getGameObjects().closest("Door");
				if(door != null){
					if(door.interact("Open")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 610;
							}
						},Calculations.random(1600,2400));
					}
				}
				//open door
				break;
			case 620:
				if(getLocalPlayer().getTile().distance(new Tile(3141,3088,0)) > 5){
					getWalking().walk(prayToMage[prayToMage.length-1]);//;,Calculations.random(10,15));
					walkingSleep();
				}
				else{
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
				if(getLocalPlayer().getTile().distance(new Tile(3139,3091,0)) > 2){
					getWalking().walk(new Tile(3139,3091,0));
					walkingSleep();
				}
				else{
					if(getMagic().castSpellOn(Normal.WIND_STRIKE, getNpcs().closest("Chicken"))){
						sleepUntil(new Condition(){
							public boolean verify(){
								return getPlayerSettings().getConfig(TUT_PROG) != 650;
							}
						},Calculations.random(1600,2400));
					}
				}
				//click spell
				//click chicken
				break;
			case 670:
				if(getDialogues().getOptions() == null){
					talkTo(MAGIC_GUIDE);
				}
				else{
					getDialogues().clickOption(1);
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
			if(getDialogues().canContinue()){
				getDialogues().clickContinue();
			}
			final Tab t = getTab();
			if(t == null){
				log("Tab is null?");
				break;
			}
			if(getTabs().openWithMouse(t)){
				sleepUntil(new Condition(){
					public boolean verify(){
						return getTabs().isOpen(t);
					}
				},Calculations.random(1200,1600));
			}
			break;
		}
		return Calculations.random(200,300);
	}

	private void login(){
		int tries = 0;
		while(getClient().getGameState() == GameState.LOGIN_SCREEN && tries < 10){
			tries++;
			switch (getClient().getLoginIndex()) {
            case 0:
                RectangleDestination button = new RectangleDestination(getClient(), new Rectangle(400, 279, 125, 26));
                new InteractionEvent(button).interact();
                break;
            case 1:
                button = new RectangleDestination(getClient(), new Rectangle(227, 301, 148, 35));
                new InteractionEvent(button).interact();
                break;
            case 3:
                button = new RectangleDestination(getClient(), new Rectangle(307, 301, 148, 35));
                new InteractionEvent(button).interact();
                break;
            case 2:
                if (getClient().getUsername().length() > 0 || getClient().getPassword().length() > 0) {
                    // Click cancel to restart
                    button = new RectangleDestination(getClient(), new Rectangle(387, 301, 148, 35));
                    new InteractionEvent(button).interact();
                    break;
                } else {
                    getKeyboard().type(sv.finalName + "@gmail.com", true);
                    MethodProvider.sleep(300, 800);
                    getKeyboard().type(sv.pass, true);
                    return;
                }
        }
			sleep(800,1200);
		}
	}
	
	private void cookShrimp(){
		GameObject fire = getGameObjects().closest("Fire");
		if(fire == null)
			lightFire();
		else{
			if(!getInventory().isItemSelected()){
				if(getInventory().interact("Raw shrimps", "Use")){
					sleepUntil(new Condition(){
						public boolean verify(){
							return getInventory().isItemSelected();
						}
					},Calculations.random(800,1200));
				}
			}
			if(getInventory().isItemSelected()){
				if(fire.interact("Use")){
					walkingSleep();
					sleepUntil(new Condition(){
						public boolean verify(){
							return getLocalPlayer().getAnimation() != -1;
						}
					},Calculations.random(2000,3000));
					sleepUntil(new Condition(){
						public boolean verify(){
							return getLocalPlayer().getAnimation() == -1;
						}
					},Calculations.random(2000,3000));
				}
			}
		}
	}

	private void lightFire(){
		if(!getInventory().contains("Logs")){
			GameObject tree = getGameObjects().closest("Tree");
			if(tree != null){
				if(tree.interact("Chop down")){
					walkingSleep();
					sleepUntil(new Condition(){
						public boolean verify(){
							return getInventory().contains("Logs");
						}
					},Calculations.random(1200, 1600));
				}
				if(getLocalPlayer().getAnimation() != -1){
					sleepUntil(new Condition(){
						public boolean verify(){
							return getInventory().contains("Logs");
						}
					},Calculations.random(4000,5000));
				}
			}
		}
		if(getInventory().contains("Logs")){
			if(!getInventory().isItemSelected()){
				getInventory().interact("Tinderbox", "Use");
				sleepUntil(new Condition(){
					public boolean verify(){
						return getInventory().isItemSelected();
					}
				},Calculations.random(800,1200));
			}
			if(getInventory().isItemSelected()){
				getInventory().interact("Logs", "Use");
				sleepUntil(new Condition(){
					public boolean verify(){
						return getLocalPlayer().getAnimation() != -1;
					}
				},Calculations.random(1200,1600));
				sleepUntil(new Condition(){
					public boolean verify(){
						return getLocalPlayer().getAnimation() == -1;
					}
				},Calculations.random(6000,8000));
			}
		}
	}

	private void talkTo(String npc){
		List<WidgetChild> clickToContinue = getWidgets().getWidgetChildrenContainingText("Click to continue");
		if(!clickToContinue.isEmpty()){
			WidgetChild wc = clickToContinue.get(0);
			if(wc != null && wc.isVisible()){
				wc.interact();
				sleep(900,1200);
			}
		}
		if(!getDialogues().canContinue()){
			final NPC guide = getNpcs().closest(npc);
			if(guide != null){
				if(guide.isOnScreen()){
					if(guide.interact("Talk-to")){
						walkingSleep();
						sleepUntil(new Condition(){
							public boolean verify(){
								return getDialogues().canContinue();
							}
						},Calculations.random(1200,1600));
					}
				}
				else{
					getWalking().walk(guide);
					walkingSleep();
				}
			}
		}
		else{
			getDialogues().clickContinue();
			sleep(600,900);
		}
	}

	private Tab getTab(){
		int conf = getPlayerSettings().getConfig(TAB_CONFIG);
		switch(conf){
		case 1:
			return Tab.COMBAT;
		case 2:
			return Tab.STATS;
		case 3:
			return Tab.QUEST;
		case 4:
			return Tab.INVENTORY;
		case 5:
			return Tab.EQUIPMENT;
		case 6:
			return Tab.PRAYER;
		case 7:
			return Tab.MAGIC;
		case 8:
			return Tab.CLAN;
		case 9:
			return Tab.FRIENDS;
		case 10:
			return Tab.IGNORE;
		case 11:
			return Tab.LOGOUT;
		case 12:
			return Tab.OPTIONS;
		case 13:
			return Tab.EMOTES;
		case 14:
			return Tab.MUSIC;
		}
		return null;
	}
	
	private void walkingSleep(){
		sleepUntil(new Condition(){
			public boolean verify(){
				return getLocalPlayer().isMoving();
			}
		}, Calculations.random(1200,1600));
		sleepUntil(new Condition(){
			public boolean verify(){
				return !getLocalPlayer().isMoving();
			}
		},Calculations.random(2400,3600));
	}

	public void onPaint(Graphics g){
		if(state != null){
			g.drawString("State: " + state.toString(), 10, 25);
		}
		g.drawString("Current progress: " + getPlayerSettings().getConfig(TUT_PROG), 10, 40);
		if(t != null)
			g.drawString("Runtime: " + t.formatTime(), 10, 55);
		if(!sv.finalName.equals(""))
			g.drawString("Final username: " + sv.finalName, 10, 70);
		if(accMade){
			g.drawString("Login index: " + getClient().getLoginIndex(), 10, 85);
		}
	}

}

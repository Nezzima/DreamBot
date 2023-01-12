package nezz.dreambot.scriptmain.ironknifer;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.input.Keyboard;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class AnvilSmith {
	private final int SMITH_PARENT = 312;
	private final int IRON_KNIFE = 24;
	private final int CLOSE_BUTTON_PARENT = 1;
	private final int CLOSE_BUTTON_CHILD = 11;
	//548,119
	private final int ENTER_AMT_PARENT = 548;
	private final int ENTER_AMT_CHILD = 119;

	private long closeUpdate = 0;
	private long knifeUpdate = 0;
	private long anvilUpdate = 0;
	private long entAmtUpdate = 0;
	private WidgetChild closeButton = null;
	private WidgetChild ironKnife = null;
	private WidgetChild enterAmt = null;
	private GameObject anvil = null;

	public WidgetChild getCloseButton() {
		if (closeButton == null || System.currentTimeMillis() - closeUpdate > 5000) {
			WidgetChild temp = Widgets.getChildWidget(SMITH_PARENT, CLOSE_BUTTON_PARENT);
			if (temp != null) {
				closeButton = temp.getChild(CLOSE_BUTTON_CHILD);
				closeUpdate = System.currentTimeMillis();
			}
		}
		return closeButton;
	}

	public WidgetChild getKnifeWidget() {
		if (ironKnife == null || System.currentTimeMillis() - knifeUpdate > 5000) {
			ironKnife = Widgets.getChildWidget(SMITH_PARENT, IRON_KNIFE);
			knifeUpdate = System.currentTimeMillis();
		}
		return ironKnife;
	}

	public WidgetChild getEnterAmount() {
		if (enterAmt == null || System.currentTimeMillis() - entAmtUpdate > 5000) {
			enterAmt = Widgets.getChildWidget(ENTER_AMT_PARENT, ENTER_AMT_CHILD);
			entAmtUpdate = System.currentTimeMillis();
		}
		return enterAmt;
	}

	public boolean needToType() {
		return getEnterAmount() != null && getEnterAmount().isVisible() && getEnterAmount().getText().contains("Enter amount");
	}

	public boolean isOpen() {
		return getCloseButton() != null && getCloseButton().isVisible();
	}

	public boolean makeKnife() {
		if (needToType()) {
			int amt = Calculations.random(3, 9);
			String type = "" + amt + "" + amt;
			Keyboard.type(type, true);
			return true;
		} else if (!isOpen())
			return false;
		else if (getKnifeWidget() != null) {
			getKnifeWidget().interact("Smith X sets");
			Sleep.sleepUntil(new Condition() {
				public boolean verify() {
					return needToType();
				}
			}, 2400);
		}
		return false;
	}

	public GameObject getAnvil() {
		if (anvil == null || System.currentTimeMillis() - anvilUpdate > 5000) {
			anvil = GameObjects.closest("Anvil");
			if (anvil != null)
				anvilUpdate = System.currentTimeMillis();
		}
		return anvil;
	}
}

package nezz.dreambot.tools;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.utilities.Logger;

public class PricedItem {
	private String name;
	private int lastCount = 0;
	private int amount = 0;
	private int price = 0;
	private int id = 0;

	public PricedItem(String name, boolean getPrice) {
		this.name = name;
		if (Inventory.contains(name)) {
			lastCount = Inventory.count(name);
		}
		if (getPrice) {
			String tempName = name;
			if (name.contains("arrow"))
				tempName += "s";
			Logger.log("Getting price");
			price = PriceGrab.getInstance().getPrice(tempName, 2);
			Logger.log("Got price: " + price);
		} else
			price = 0;
	}

	public PricedItem(String name, int id, boolean getPrice) {
		this.name = name;
		this.setId(id);
		if (Inventory.contains(id))
			lastCount = Inventory.count(id);
		if (getPrice)
			price = PriceGrab.getInstance().getPrice(name, 2);
		else
			price = 0;
	}

	public void update() {
		int increase = 0;
		if (id == 0)
			increase = Inventory.count(name) - lastCount;
		else
			increase = Inventory.count(id) - lastCount;
		if (increase < 0)
			increase = 0;
		amount += increase;
		if (id == 0)
			lastCount = Inventory.count(name);
		else
			lastCount = Inventory.count(id);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAmount(int amt) {
		this.amount = amt;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public int getAmount() {
		return amount;
	}

	public int getPrice() {
		return price;
	}

	public int getValue() {
		if (amount <= 0)
			return 0;
		return amount * price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

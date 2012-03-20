package be.goossens.oracle;

public class Food implements Comparable<Food> {
	private int id;
	private String platform;
	private int languageid;
	private int visible;
	private int categoryid;
	private int userid;
	private int isfavorite;
	private String name;
	
	public Food(int id, String platform, int languageid, int visible,
			int categoryid, int userid, int isfavorite, String name) {
		super();
		this.id = id;
		this.platform = platform;
		this.languageid = languageid;
		this.visible = visible;
		this.categoryid = categoryid;
		this.userid = userid;
		this.isfavorite = isfavorite;
		this.name = name;
	}

	public Food(Food newFood) {
		super();
		this.id = newFood.getId();
		this.platform = newFood.getPlatform();
		this.languageid = newFood.getLanguageid();
		this.visible = newFood.getVisible();
		this.categoryid = newFood.getCategoryid();
		this.userid = newFood.getUserid();
		this.isfavorite = newFood.getIsfavorite();
		this.name = newFood.getName();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public int getLanguageid() {
		return languageid;
	}

	public void setLanguageid(int languageid) {
		this.languageid = languageid;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public int getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(int categoryid) {
		this.categoryid = categoryid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getIsfavorite() {
		return isfavorite;
	}

	public void setIsfavorite(int isfavorite) {
		this.isfavorite = isfavorite;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString(){
		return name;
	}
	
	public int compareFoodItemTo(String stringToCompare) {
		int returnvalue = 0;
		int index = 0;

		char[] thisItem = name.toCharArray();
		char[] itemStringToCompare = stringToCompare.toCharArray();

		while ((index < name.length()) && (index < itemStringToCompare.length)) {
			if (ExcelCharacter.compareToAsInExcel(thisItem[index],
					itemStringToCompare[index]) != 0) {
				break;
			}
			index++;
		}
		if ((index < name.length()) && (index < itemStringToCompare.length)) {
			if (ExcelCharacter.compareToAsInExcel(thisItem[index],
					itemStringToCompare[index]) < 0)
				return -1;
			if (ExcelCharacter.compareToAsInExcel(thisItem[index],
					itemStringToCompare[index]) > 0)
				return 1;
		}
		if ((index >= name.length()) || (index >= itemStringToCompare.length)) {
			if (thisItem.length < itemStringToCompare.length)
				return -1;
			if (thisItem.length > itemStringToCompare.length)
				return 1;
		}
		return returnvalue;
	}

	public int compareTo(Food itemToCompare) {
		return compareFoodItemTo(itemToCompare.name);
	}

}

package be.goossens.oracle.Rest;

/*
 * This class is used to parse data from activity to activity.
 * The data has to be parsing with the same name and the names are stored in this class
 */

public class DataParser {
	//public static final String
	public static final String fromWhereWeCome = "fromWherWeCome";
	public static final String weComeFromShowFoodList = "weComeFromShowFoodList";
	public static final String weComeFRomShowSelectedFood = "weComeFRomSelectedFood";
	public static final String weComeFromShowFoodTemplates = "weComeFromShowFoodTemplates";

	//for parsing ID's
	public static final String idFood = "foodID";
	public static final String idSelectedFood = "selectedFoodID";
	public static final String idUnit = "unitID";
	
	//for parsing the food amount from a template to the showAddFoodToSelectionPage
	public static final String foodAmount = "foodAmount";
}

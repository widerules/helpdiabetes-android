package be.goossens.oracle.Rest;

/*
 * This class is used to parse data from activity to activity.
 * The data has to be parsing with the same name and the names are stored in this class
 */

public class DataParser {
	// public static final String
	public static final String fromWhereWeCome = "fromWherWeCome";
	public static final String whatToDo = "whatToDo";

	// Food //
	public static final String weComeFromShowFoodList = "weComeFromShowFoodList";
	public static final String weComeFRomShowSelectedFood = "weComeFRomSelectedFood";
	public static final String weComeFromShowFoodTemplates = "weComeFromShowFoodTemplates";

	// for parsing ID's
	public static final String idFood = "foodID";
	public static final String idSelectedFood = "selectedFoodID";
	public static final String idUnit = "unitID";

	// for parsing the food amount from a template to the
	// showAddFoodToSelectionPage
	public static final String foodAmount = "foodAmount";

	// Exercise event //
	public static final String doUpdateExerciseEvent = "updateExerciseEvent";
	public static final String doCreateExerciseEvent = "createExerciseEvent";
	public static final String idExerciseEvent = "exerciseEventID";
	
	// Exercise type //
	public static final String doUpdateExerciseType = "updateExerciseType";
	public static final String doCreateExerciseType = "createExerciseType";
	public static final String idExerciseType = "exerciseTypeID";
	
	// used to parse the tabHost object //
	public static final String tabhost = "tabhost";
	
	//Activity IDs //
	public static final String activityIDTracking = "showTracking";
	public static final String activityIDMeal = "showMeal";
	public static final String activityIDGlucose = "showGlucose";
	public static final String activityIDMedicine = "showMedicine";
	public static final String activityIDExercise = "showExercise";
	public static final String activityIDSettings = "showSettings";
}

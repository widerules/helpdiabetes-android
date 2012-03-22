package be.goossens.oracle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ShowCreateFood extends Activity {
	private EditText editTextfoodName;
	private EditText editTextUnitStandardAmound;
	private EditText editTextUnitName;
	private EditText editTextUnitKcal;
	private EditText editTextUnitCarbs;
	private EditText editTextUnitProt;
	private EditText editTextUnitFat;

	DbAdapter dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_create_food);

		dbHelper = new DbAdapter(this);
		dbHelper.open();

		editTextfoodName = (EditText) findViewById(R.id.editTextFoodName);
		editTextUnitStandardAmound = (EditText) findViewById(R.id.editTextFoodUnitStandardAmound);
		editTextUnitName = (EditText) findViewById(R.id.editTextFoodUnitName);
		editTextUnitKcal = (EditText) findViewById(R.id.editTextFoodUnitKcal);
		editTextUnitCarbs = (EditText) findViewById(R.id.editTextFoodUnitCarbs);
		editTextUnitProt = (EditText) findViewById(R.id.editTextFoodUnitProt);
		editTextUnitFat = (EditText) findViewById(R.id.editTextFoodUnitFat);
	}

	public void onClickAdd(View view) {
		if (checkAllFieldsGotSomeValue()) {
			long foodId = dbHelper.createFood(editTextfoodName.getText()
					.toString());
			dbHelper.createFoodUnit(foodId, editTextUnitName.getText()
					.toString(), Float.parseFloat(editTextUnitStandardAmound
					.getText().toString()), Float.parseFloat(editTextUnitKcal
					.getText().toString()), Float.parseFloat(editTextUnitProt
					.getText().toString()), Float.parseFloat(editTextUnitCarbs
					.getText().toString()), Float.parseFloat(editTextUnitFat
					.getText().toString()));
			
			setResult(RESULT_OK);
			finish();
		}
	}

	private boolean checkAllFieldsGotSomeValue() {
		if (editTextfoodName.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.food_name_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitStandardAmound.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.unit_name_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitName.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.unit_name_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitKcal.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.kcal_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitProt.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.prot_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitCarbs.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.carbs_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else if (editTextUnitFat.getText().length() <= 0) {
			Toast.makeText(this,
					getResources().getString(R.string.fat_is_required),
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			// if there is a value in everything ( check if the value aint '0'
			// or '0.'
			try {
				float unitStandardAmound = Float
						.parseFloat(editTextUnitStandardAmound.getText()
								.toString());
				if (unitStandardAmound <= 0) {
					Toast.makeText(
							this,
							getResources().getString(
									R.string.unit_name_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(
						this,
						getResources()
								.getString(R.string.unit_name_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			/*
			 *Carbs, prot, kcal, fat can be 0 
			 * */
			
			/*try {
				float unitKcal = Float.parseFloat(editTextUnitKcal.getText()
						.toString());
				if (unitKcal <= 0) {
					Toast.makeText(
							this,
							getResources().getString(R.string.kcal_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.kcal_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			try {
				float unitProt = Float.parseFloat(editTextUnitProt.getText()
						.toString());
				if (unitProt <= 0) {
					Toast.makeText(
							this,
							getResources().getString(R.string.prot_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.prot_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			try {
				float unitCarb = Float.parseFloat(editTextUnitCarbs.getText()
						.toString());
				if (unitCarb <= 0) {
					Toast.makeText(
							this,
							getResources()
									.getString(R.string.carbs_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.carbs_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}

			try {
				float unitFat = Float.parseFloat(editTextUnitFat.getText()
						.toString());
				if (unitFat <= 0) {
					Toast.makeText(this,
							getResources().getString(R.string.fat_is_required),
							Toast.LENGTH_LONG).show();
					return false;
				}
			} catch (Exception e) {
				Toast.makeText(this,
						getResources().getString(R.string.fat_is_required),
						Toast.LENGTH_LONG).show();
				return false;
			}*/

			// if everything went OK we return true
			return true;
		}
	}

	public void onClickBack(View view) {
		setResult(RESULT_OK);
		finish();
	}
}

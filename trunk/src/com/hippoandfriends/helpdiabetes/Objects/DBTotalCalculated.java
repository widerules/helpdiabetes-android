package com.hippoandfriends.helpdiabetes.Objects;

import com.hippoandfriends.helpdiabetes.R.string;

public class DBTotalCalculated {
	private String calculatedValue;
	// use to know if we have carb, prot, fat or kcal
	// carb = 1, prot = 2, fat = 3, kcal = 4
	private int valueNumber;
	private String valueText;

	public DBTotalCalculated(String calculatedValue, int valueNumber,
			String valueText) {
		super();
		this.calculatedValue = calculatedValue;
		this.valueNumber = valueNumber;
		this.valueText = valueText;
	}

	public String getCalculatedValue() {
		return calculatedValue;
	}

	public void setCalculatedValue(String calculatedValue) {
		this.calculatedValue = calculatedValue;
	}

	public int getValueNumber() {
		return valueNumber;
	}

	public void setValueNumber(int valueNumber) {
		this.valueNumber = valueNumber;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

}

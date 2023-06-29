package com.example.Transmission;


import java.util.ArrayList;

public class LoginRes {
	private ArrayList<ArrayList<Email>> arrayLists = new ArrayList<>();

	public LoginRes(ArrayList<ArrayList<Email>> arrayLists) {
		this.arrayLists = arrayLists;
	}

	public ArrayList<ArrayList<Email>> getArrayLists() {
		return arrayLists;
	}


}

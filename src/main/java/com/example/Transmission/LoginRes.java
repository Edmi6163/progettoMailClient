package com.example.Transmission;


import java.io.Serializable;
import java.util.ArrayList;

public class LoginRes implements Serializable {
	private ArrayList<ArrayList<Email>> arrayLists = new ArrayList<>();

	public LoginRes(){}

	public LoginRes(ArrayList<ArrayList<Email>> arrayLists) {
		this.arrayLists = arrayLists;
	}


}

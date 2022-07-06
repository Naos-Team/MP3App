package com.zxfdwka.bestcountrymusic.ringtone.Receiver;

public class ItemNemosofts {

 	private String purchase_code, product_name, purchase_date, buyer_name, license_type, nemosofts_key, package_name;

	public ItemNemosofts(String purchase_code, String product_name, String purchase_date, String buyer_name, String license_type, String nemosofts_key, String package_name) {
		this.purchase_code = purchase_code;
		this.product_name = product_name;
		this.purchase_date = purchase_date;
		this.buyer_name = buyer_name;
		this.license_type = license_type;
		this.nemosofts_key = nemosofts_key;
		this.package_name = package_name;

	}


	public String getPurchase_code() {
		return purchase_code;
	}

	public String getProduct_name() {
		return product_name;
	}

	public String getPurchase_date() {
		return purchase_date;
	}

	public String getBuyer_name() {
		return buyer_name;
	}

	public String getLicense_type() {
		return license_type;
	}

	public String getNemosofts_key() {
		return nemosofts_key;
	}

	public String getPackage_name() {
		return package_name;
	}
}

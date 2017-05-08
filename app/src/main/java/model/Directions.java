package model;

import android.util.Log;

import java.io.Serializable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Directions implements Serializable{
	private static final long serialVersionUID = -3616074050459610955L;
	private double startlon, startlat, endlon,endlat;
	//private ArrayList[]() routes;
	private String dirs;
	public Directions(String dirs){
		this.dirs=dirs;
		deserializeGooglejson(dirs);
	}
	public Directions(double startlat, double startlon,double endlat, double endlon){
		this.endlat=endlat;
		this.endlon=endlon;
		this.startlat=startlat;
		this.startlon=startlon;
	}
	public Directions(){}
	
	public String toString() {
		return "SLat: "+this.getStartlat()+"\tSLon: "+this.getStartlon()
			+"\nELat: "+this.getEndlat()+"\tELon: "+this.getEndlon();	
	}
	
	public Directions get(){
		return this;
	}
	public double getEndlon() {
		return endlon;
	}
	public void setEndlon(double endlon) {
		this.endlon = endlon;
	}
	public double getEndlat() {
		return endlat;
	}
	public void setEndlat(double endlat) {
		this.endlat = endlat;
	}
	public String getDirs() {
		return dirs;
	}
  
	public double getStartlon() {
		return startlon;
	}
	
	public double getStartlat() {
		return startlat;
	}
	
	public void setStartlon(double startlon){
		this.startlon=startlon;
	}
	public void setStartlat(double startlat) {
		this.startlat = startlat;
	}
	
	public boolean equals(Directions otherDirs){
		return (this.getStartlat()==otherDirs.getStartlat())&&(this.getStartlon()==otherDirs.getStartlon())
				&&(this.getEndlat()==otherDirs.getEndlat())&&(this.getEndlon()==otherDirs.getEndlon());
	}
	
	private void deserializeGooglejson(String directions){
		JSONObject route,legsObj,endlocation=null,startLocation=null;
		JSONArray legs;

		try{
			route= new JSONObject(new JSONObject(directions).getJSONArray("routes").get(0).toString());
			//System.out.println(route.toString());
			legs = route.getJSONArray("legs");
			legsObj= new JSONObject(legs.get(0).toString());
			endlocation = new JSONObject(legsObj.getJSONObject("end_location").toString());
			startLocation = new JSONObject(legsObj.getJSONObject("start_location").toString());
		}catch (JSONException e){
			Log.d("Error on JSON",e.getMessage());
		}

		try{
			this.setStartlat(startlat = startLocation.getDouble("lat"));
			this.setStartlon(startlon= startLocation.getDouble("lng"));
			this.setEndlat(endlat = endlocation.getDouble("lat"));
			this.setEndlon(endlon = endlocation.getDouble("lng"));			
		}catch(Exception e){
			System.out.println(e.getMessage());
			//In case of fire: http://stackoverflow.com/questions/2591098/how-to-parse-json-in-java	
		}		 	
	}
}
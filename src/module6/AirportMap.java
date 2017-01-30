package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import module5.CommonMarker;
import module5.EarthquakeMarker;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private Marker lastSelected;
	private Marker lastClicked;
	public HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
			//System.out.println(feature.getId());
		}
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	

	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked.setSelected(false);
			lastClicked = null;
		}
		else {
			if(ClickedMarker(airportList)){
				hideMarkers(lastClicked);
			}
		}
		// TODO: Implement this method
		// Hint: You probably want a helper method or two to keep this code
		// from getting too long/disorganized
	}
	
	private boolean ClickedMarker(List<Marker> markers)
	{
		// TODO: Implement this method
		for (Marker mk : markers) {
			if (mk.isInside(map, mouseX, mouseY) && (lastClicked == null)) {
				lastClicked = (Marker) mk;
				lastClicked.setSelected(true);;
				return true;
			}
		}
		return false;
	}

	private void hideMarkers(Marker LastClicked) {
		double mindistance = 1000000;
		Marker minmarker = null;
		List<Marker> nhidden_port;
		nhidden_port = new ArrayList<Marker>();
		for(Marker marker : routeList) {
			if (!(LastClicked.getLocation().toString().equals(airports.get(Integer.parseInt((String)marker.getProperty("source"))).toString()))) {
				marker.setHidden(true);
			}
			else {
				nhidden_port.add(marker);
				if (mindistance > LastClicked.getDistanceTo(airports.get(Integer.parseInt((String)marker.getProperty("destination"))))) {
				    mindistance = LastClicked.getDistanceTo(airports.get(Integer.parseInt((String)marker.getProperty("destination"))));
				    minmarker = marker;
				}
			}
		}
		if (minmarker != null) {
			minmarker.setStrokeWeight(5);
			//System.out.println("The minimum flight distance from " + LastClicked.getProperty("city").toString() + " is " + mindistance + " to " + airports.get(Integer.parseInt((String)minmarker.getProperty("destination"))).toString());

		}
		for(Marker marker : airportList) {
			Integer count = 0;
			//System.out.println(LastClicked.getLocation().toString() + " " + airports.get(Integer.parseInt((String)marker.getProperty("source"))));
			if (!(LastClicked.getLocation().toString().equals(marker.getLocation().toString()))) {
				for (Marker m : nhidden_port){
					if (marker.getLocation().toString().equals(airports.get(Integer.parseInt((String)m.getProperty("destination"))).toString())) count++;
				}
			    if (count == 0) marker.setHidden(true);

			}
			if(minmarker != null){
			    if ((airports.get(Integer.parseInt((String)minmarker.getProperty("destination"))).toString().equals(marker.getLocation().toString()))){
				    System.out.println("The minimum flight distance from " + LastClicked.getProperty("name").toString() + " is " + mindistance + " to " + marker.getProperty("name").toString());
			    }
			}
		}
	}
	
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : airportList) {
			marker.setHidden(false);
		}
		for(Marker marker : routeList) {
			marker.setStrokeWeight(1);
			marker.setHidden(false);
		}
	}


public void mouseMoved()
{
	// clear the last selection
	if (lastSelected != null) {
		lastSelected.setSelected(false);
		lastSelected = null;
	
	}
	selectMarkerIfHover(airportList);
	//loop();
}

// If there is a marker selected 
private void selectMarkerIfHover(List<Marker> markers)
{
	// Abort if there's already a marker selected
	if (lastSelected != null) {
		return;
	}
	
	for (Marker m : markers) 
	{
		AirportMarker marker = (AirportMarker)m;
		if (marker.isInside(map,  mouseX, mouseY)) {
			lastSelected = marker;
			marker.setSelected(true);
			return;
		}
	}
}

}

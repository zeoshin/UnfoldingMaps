import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import java.util.Arrays;

public class selection_sort extends PApplet{
	
	public selection_sort() {
		
		int[] array = new int[] {5, 1, 10, 8, 2, 8, 0};
		System.out.println("original array is: " + Arrays.toString(array));
		for (int i = 0; i < (array.length - 1); i++) {
			int tmpele = array[i];
			int minidx = i;
			for (int j = i + 1; j < (array.length); j++ ) {
				if (array[j] < array[minidx]){
					minidx = j;
				}
			}
			array[i] = array[minidx];
			array[minidx] = tmpele;
		}
		System.out.println("sorted array is: " + Arrays.toString(array));
	}
}
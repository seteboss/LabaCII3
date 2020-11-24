package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntType {
		int curCity; //Текущий город
		int nextCity; //Следующий город
		int[] tabu;
		int pathIndex;
		int[] path;
		double tourLength; //Длинна пути

		public void setTabuValue (int index, int value){
				tabu[index] = value;
		}

		public void setPathValue (int index, int value){
				path[index] = value;
		}

		public void incPathIndex () { pathIndex ++;}

		public void init(int max){
				tabu = new int[max];
				path = new int[max];
		}

}

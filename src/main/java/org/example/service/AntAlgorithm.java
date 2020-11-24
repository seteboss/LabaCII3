package org.example.service;

import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.AntType;
import org.example.entity.CityType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntAlgorithm {

		double alpha; //Относительная значимость пути

		double beta; //Относительная значимость видимости

		double rho; //Продолжительность пути

		double qval;

		int maxCities;

		int maxDistance = 100;

		int maxAnts;

		int maxTour = maxCities * maxDistance;

		int maxTours = 20;

		int maxTime = maxTours * maxCities;

		double initPheromone;

		CityType[] cities;

		AntType[] ants;

		double[][] distance;

		double[][] pheromone;

		double best;

		int bestIndex;

		private Random random = new Random();



		private void initCities (){
				cities = new CityType[maxCities];
				for (int i = 0; i < maxCities; i++) {
						cities[i] = new CityType();
				}
		}
		private void initAnts(){
				ants = new AntType[maxAnts];
				for (int i = 0; i < maxAnts; i++) {
						ants[i] = new AntType();
						ants[i].init(maxCities);
				}
		}


		private void init() {
				/*Создание городов*/
				initCities();
				initAnts();
				distance = new double[maxCities][maxCities];
				pheromone = new double[maxCities][maxCities];
				for (int from = 0; from < maxCities; from++) {
						cities[from].setX(random.nextInt(maxDistance));
						cities[from].setY(random.nextInt(maxDistance));
						for (int to = 0; to < maxCities; to++) {
								distance[from][to] = 0;
								pheromone[from][to] = initPheromone;
						}
				}
				/* Вычисляем расстояние между городами */
				for (int from = 0; from < maxCities; from++) {
						for (int to = 0; to < maxCities; to++) {
								if ((to != from) && (distance[from][to] == 0)) {
										int xd = Math.abs(cities[from].getX() - cities[to].getX());
										int yd = Math.abs(cities[from].getY() - cities[to].getY());
										distance[from][to] = Math.sqrt((double) ((xd * xd) + (yd * yd)));
										distance[to][from] = distance[from][to];
								}
						}
				}
				/* Инициализация муравьев */
				int to = 0;
				for (int ant = 0; ant < maxAnts; ant++) {
						/* Распределяем муравьев по городам равномерно */
						if (to == maxCities) {
								to = 0;
						}
						ants[ant].setCurCity(to++);
						for (int from = 0; from < maxCities; from++) {
								ants[ant].setTabuValue(from, 0);
								ants[ant].setPathValue(from, -1);
						}
						ants[ant].setPathIndex(1);
						ants[ant].setPathValue(0, ants[ant].getCurCity());
						ants[ant].setNextCity(-1);
						ants[ant].setTourLength(0);
						/* Помещаем исходный город, в котором находится муравей, в список табу */
						ants[ant].setTabuValue(ants[ant].getCurCity(), 1);
				}
		}

		/* Функция предназначена для повторной инициализации всех муравьев */
		private void restartAnts() {
				int to = 0;
				for (int ant = 0; ant < maxAnts; ant++) {
						if (ants[ant].getTourLength() < best) {
								best = ants[ant].getTourLength();
								bestIndex = ant;
						}
						ants[ant].setNextCity(-1);
						ants[ant].setTourLength(0);
						for (int i = 0; i < maxCities; i++) {
								ants[ant].setTabuValue(i, 0);
								ants[ant].setPathValue(i, -1);
						}
						if (to == maxCities) {
								to = 0;
						}
						ants[ant].setCurCity(to++);
						ants[ant].setPathIndex(1);
						ants[ant].setPathValue(0, ants[ant].getCurCity());
						ants[ant].setTabuValue(ants[ant].getCurCity(), 1);
				}
		}

		private double antProduct(int from, int to) {
				return (
						(
								Math.pow(pheromone[from][to], alpha) *
										Math.pow((1.0 / distance[from][to]), beta)));
		}

		private int selectNextCity(int ant) {
				int from, to;
				double denom = 0;
				/* Выбрать следующий город */
				from = ants[ant].getCurCity();
				for (to = 0; to < maxCities; to++) {
						if (ants[ant].getTabu()[to] == 0) {
								denom += antProduct(from, to);
						}
				}
				if (denom == 0) {
						throw new RuntimeException("");
				}

				do {
						double p;
						to++;
						if (to >= maxCities) {
								to = 0;
						}
						if (ants[ant].getTabu()[to] == 0) {
								p = antProduct(from, to) / denom;
								if (Math.random() < p) {
										break;
								}
						}
				} while (true);
				return to;
		}

		/* Функция рассчитывает движение муравьев по графу */
		private int simulateAnts() {
				int moving = 0;
				for (int k = 0; k < maxAnts; k++) {
						/* Убедиться, что у муравья есть куда идти */
						if (ants[k].getPathIndex() < maxCities) {
								ants[k].setNextCity(selectNextCity(k));
								ants[k].getTabu()[ants[k].getNextCity()] = 1;
								ants[k].getPath()[ants[k].getPathIndex()] = ants[k].getNextCity();
								ants[k].incPathIndex();
								ants[k].setTourLength(ants[k].getTourLength()
										+ distance[ants[k].getCurCity()][ants[k].getNextCity()]);
								if (ants[k].getPathIndex() == maxCities) {
										ants[k].setTourLength(ants[k].getTourLength() +
												distance[ants[k].getPath()[maxCities - 1]][ants[k].getPath()[0]]);
								}
								ants[k].setCurCity(ants[k].getNextCity());
								moving++;
						}
				}
				return moving;
		}

		/* Функция обновляет пути */
		private void updateTrails() {
				int from, to;
				for (from = 0; from < maxCities; from++) {
						for (to = 0; to < maxCities; to++) {
								if (from != to) {
										pheromone[from][to] *= 1 - rho;
										if (pheromone[from][to] < 0) {
												pheromone[from][to] = initPheromone;
										}
								}
						}
				}
				/* Нанесение нового фермента */
				for (int ant = 0; ant < maxAnts; ant++) {
						for (int i = 0; i < maxCities; i++) {
								if (i < maxCities - 1) {
										from = ants[ant].getPath()[i];
										to = ants[ant].getPath()[i + 1];
								} else {
										from = ants[ant].getPath()[i];
										to = ants[ant].getPath()[0];
								}
								pheromone[from][to] += (qval / ants[ant].getTourLength());
								pheromone[to][from] = pheromone[from][to];
						}
				}
				for (from = 0; from < maxCities; from++) {
						for (to = 0; to < maxCities; to++) {
								pheromone[from][to] *= rho;
						}
				}
		}

		public void perform() {
				int curTime = 0;
				init();
				while (curTime++ < maxTime) {
						if (simulateAnts() == 0) {
								updateTrails();
								if (curTime != maxTime) {
										restartAnts();
								}
						}
				}
		}

		public AntType getBest(){
				return ants[bestIndex];
		}

}



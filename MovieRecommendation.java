import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

class WeightSort implements Comparator<SimilarityRating> {
	public int compare(SimilarityRating sr1, SimilarityRating sr2) {
		if (sr1.weight == sr2.weight) {
			return 0;
		} else if (sr2.weight > sr1.weight) {
			return 1;
		} else {
			return -1;
		}
	}
}

public class MovieRecommendation {
	static int trainingMatrix[][] = new int[200][1000];
	static String trainingDataPath = null;
	static String testDataPath = null;
	static String filterType = null;
	static String algorithm = null;
	static PrintWriter output = null; 
	static BufferedReader br = null ;
	static String line = null;
	static int testUser = 0;
	static int testMovies[] = new int[20];
	static int testRatings[] = new int[20];
	static int num = 0;
	static PriorityQueue<SimilarityRating> weights;
	static WeightSort ws = new WeightSort();
	
	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
			System.out.println("Usage: <training path> <test path> <filterType> <algorithm>");
			return;
		}
		
		trainingDataPath = args[0];
		testDataPath = args[1];
		filterType = args[2];
		algorithm = args[3];
		System.out.println("TrainingData: " + trainingDataPath);
		System.out.println("testDataPath: " + testDataPath);
		
		constructTrainingMatrix();
		output = new PrintWriter("output.txt");
		br = new BufferedReader(new FileReader(testDataPath));
		
		if (filterType.equals("user") && algorithm.equals("cosine")) {
			System.out.println("user-cosine");
			userCosine();
		} else if (filterType.equals("user") && algorithm.equals("pearson")) {
			System.out.println("user-pearson");
			userPearson();
		} else if (filterType.equals("user") && algorithm.equals("inverse")) {
			System.out.println("user-inverse");
			userInverse();
		} else if (filterType.equals("user") && algorithm.equals("case")) {
			System.out.println("user-case");
			userCase();
		} else if (filterType.equals("item") && algorithm.equals("adjusted")) {
			System.out.println("user-adjusted");
			userAdjusted();
		} else if (filterType.equals("custom") && algorithm.equals("custom")) {
			System.out.println("user-custom");
			userCustom();
		} else {
			System.out.println("Usage: <training path> <test path> <filterType> <algorithm>");
			return;
		}
		
		output.close();
	}
	
	public static void constructTrainingMatrix() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(trainingDataPath));
		
		for (int i = 0; i < 200; i++) {
			String line = br.readLine();
			StringTokenizer st = new StringTokenizer(line);
			for (int j = 0; j < 1000; j++) {
				trainingMatrix[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
	}
	
	public static void userInverse() throws IOException { 
		
	}
	
	public static void userCase() throws IOException { 
		
	}
	
	public static void userAdjusted() throws IOException { 
		
	}
	
	public static void userCustom() throws IOException { 
		
	}
	
	public static void userPearson() throws IOException { 
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			int userId = Integer.parseInt(st.nextToken());
			int movieId = Integer.parseInt(st.nextToken()) - 1;
			int rating = Integer.parseInt(st.nextToken());
			
			if (testUser == 0 || testUser != userId) {
				testUser = userId;
				num = 0;	
			}
			if (rating != 0) {
				testMovies[num] = movieId;
				testRatings[num] = rating;
				num++;
			} else {	
				weights = new PriorityQueue<SimilarityRating>(200, ws);
				double testUserOverallAvg = 0;
				
				for (int i = 0; i < num; i++) {
					testUserOverallAvg += testRatings[i];
				}
				testUserOverallAvg = testUserOverallAvg/num;
				
				for (int i = 0; i < 200; i++) {
					int userNum = 0;
					double trainingUserAvg = 0;
					double testUserAvg = 0;
					
					for (int j = 0; j < num; j++) {
						if (trainingMatrix[i][testMovies[j]] != 0) {
							userNum++;
							trainingUserAvg += trainingMatrix[i][testMovies[j]];
							testUserAvg += testRatings[j];
						}
					}
					trainingUserAvg = trainingUserAvg/userNum;
					testUserAvg = testUserAvg/userNum;
					
					double numerator = 0;
					double testUserRating = 0;
					double trainingUserRating = 0;
					for (int j = 0; j < num; j++) {
						double userRating = trainingMatrix[i][testMovies[j]];
						if (userRating == 0) {
							continue;
						}
						double userdiff = userRating - trainingUserAvg;
						double testdiff = testRatings[j] - testUserAvg;
						
						testUserRating += (testdiff * testdiff);
						trainingUserRating += (userdiff * userdiff);
						numerator += userdiff * testdiff;
					}
					
					double denominator = Math.sqrt(testUserRating) * Math.sqrt(trainingUserRating);
					double weight = numerator/denominator;
					
					if (denominator != 0.0 && trainingMatrix[i][movieId] != 0 && weight != 0) {
						SimilarityRating sr = new SimilarityRating();
						sr.weight = weight;
						sr.rating = (trainingMatrix[i][movieId] - trainingUserAvg);
						// System.out.println("Weight: " + sr.weight + ", Rating: " + sr.rating);
						weights.offer(sr);
					}
				}
				
				double denominator = 0;
				double numerator = 0;
				int i = 0;
				
				for (i = 0; i < 100; i++) {
					if (weights.isEmpty()) {
						break;
					}
					
					SimilarityRating sr = weights.poll();
					denominator += Math.abs(sr.weight);
					numerator += (sr.rating * sr.weight);
				}
				
				double doubleRating = Math.round(numerator/denominator) + testUserOverallAvg;
				int finalRating = (int) doubleRating;
				if (finalRating == 0) {
					finalRating = 1;
				}
			
				output.println(testUser + " " + (movieId + 1) + " " + finalRating);
			}
		}
	}
	
	public static void userCosine() throws IOException {
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			int userId = Integer.parseInt(st.nextToken());
			int movieId = Integer.parseInt(st.nextToken()) - 1;
			int rating = Integer.parseInt(st.nextToken());
			
			if (testUser == 0 || testUser != userId) {
				testUser = userId;
				num = 0;	
			}
			if (rating != 0) {
				testMovies[num] = movieId;
				testRatings[num] = rating;
				num++;
			} else {
				weights = new PriorityQueue<SimilarityRating>(200, ws);

				for (int i = 0; i < 200; i++) {
					int numerator = 0;
					double testUserRating = 0;
					double trainingUserRating = 0;
					for (int j = 0; j < num; j++) {
						int userRating = trainingMatrix[i][testMovies[j]];
						if (userRating == 0) {
							continue;
						}
						testUserRating += (testRatings[j] * testRatings[j]);
						trainingUserRating += (userRating * userRating);
						numerator += testRatings[j] * trainingMatrix[i][testMovies[j]];
					}
					
					double denominator = Math.sqrt(testUserRating) * Math.sqrt(trainingUserRating);
					double weight = numerator/denominator;
					
					if (denominator != 0.0 && trainingMatrix[i][movieId] != 0) {
						SimilarityRating sr = new SimilarityRating();
						sr.weight = weight;
						sr.rating = trainingMatrix[i][movieId];
						weights.offer(sr);
					}
				}
				
				double denominator = 0;
				double numerator = 0;
				int k = 100;
				int i = 0;
				
				for (i = 0; i < k; i++) {
					if (weights.isEmpty()) {
						break;
					}
					SimilarityRating sr = weights.poll();
					denominator += sr.weight;
					numerator += (sr.rating * sr.weight);
				}
				
				double doubleRating = Math.round(numerator/denominator);
				int finalRating = (int) doubleRating;
				if (finalRating == 0) {
					finalRating = 1;
				}
			
				output.println(testUser + " " + (movieId + 1) + " " + finalRating);
			}
		}
	}
}


package clusterTreeSimulation;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class Main_Antarctica_DirectSampling2D 
{
	public static void main(String arg[]) throws IOException
	{
		System.out.println("Antarctica simulation: direct sampling method 2D mission begin !");
		System.out.println(new Date());
		final int RealizationAmount = 5;
		final int Height_R = 1202;
		final int Width_R = 1202;
		final int UnknownBound = -5000;
		final int SolutionValue_Max = 3346;
		final int SolutionValue_Min = -1000;
		final double MaxElementDifference = 4346;
				
		// DS parameters
		final int DS_MaxRadius = 10;
		final int DS_Neighborhood = 100;
		final double DS_Threshold = 0.02;
		final double DS_Fraction = 0.1;
		final int DS_Neighborhood_min = 0;
				
		// analyze the distance
		double distance_print_primary_Mean = 0.00;
		final int distance_print_Amount = 10000;
		int simulationPointCounter = 0;
		int searchCounter = 0;
				
		Realization2D_Antarctica realization_Original = new Realization2D_Antarctica(Height_R,Width_R);
		realization_Original.ReadRealizationFile(new String("z_Antarctica_python_realization.txt"));
		//realization.PrintOut_Color(new String("z_original"));
		
		int unknownAmount = realization_Original.GetUnknownAmount(UnknownBound);
		System.out.println(new String("the number of unknown points is "+ unknownAmount));
		int DS_SearchScope = realization_Original.GetRangeAmount(SolutionValue_Min, SolutionValue_Max);
		DS_SearchScope = (int)((double)(DS_SearchScope) * DS_Fraction);
		System.out.println(new String("the search scope is "+ DS_SearchScope));
		
		Realization2D_Antarctica realization = new Realization2D_Antarctica(Height_R,Width_R);
		
		double[][] distanceImage = new double[Height_R][Width_R];
		
		int realizationIndex=0,unknownIndex=0,unknownX=0,unknownY=0,unknownValue=0;
		int randomX=0, randomY=0, randomIndex=0, randomValue=0;
		double distance_min=0.0,distance=0.0;
		Random random = new Random();
		PatternDS pattern_DS = new PatternDS();
		
		// record the running time
		Date end = new Date();
		int CostTime = 0;
		
		// record the searching time
		long searchingTime_start = 0, searchingTime_end=0, searchingTime=0;
		
		// 2D simulations
		System.out.println("2D simulations begin !");
		System.out.println(new Date());
		Date start = new Date();
		for(realizationIndex=0;realizationIndex!=RealizationAmount;++realizationIndex)
		{
			System.out.println(new String("Realization"+realizationIndex));
			realization.SetRealizationMatrix(realization_Original.GetRealizationMatrix());
			unknownIndex = 0;
			searchCounter = 0;
			distance_print_primary_Mean = 0.0;
			simulationPointCounter = 0;
			
			while(unknownIndex < unknownAmount)
			{
				unknownY = Math.abs(random.nextInt()%Height_R);
				unknownX = Math.abs(random.nextInt()%Width_R);
				if(realization.GetValue(unknownY, unknownX) < UnknownBound)
				{
					pattern_DS = new PatternDS(realization.GetPattern_DS(unknownY, unknownX, DS_Neighborhood, DS_MaxRadius, UnknownBound));
					if(pattern_DS.GetKnownPointAmount() <= DS_Neighborhood_min)
					{
						continue;
					}
					else
					{
						//System.out.println(unknownIndex);
						
						randomIndex=0;
						distance_min = 1.0;
						distanceImage = new double[Height_R][Width_R];
						
						// record the searching time
						searchingTime_start = System.nanoTime();
						
						while(randomIndex < DS_SearchScope)
						{
							randomY = Math.abs(random.nextInt()%Height_R);
							randomX = Math.abs(random.nextInt()%Width_R);
							randomValue = realization.GetValue(randomY, randomX);
							if(randomValue>SolutionValue_Min && randomValue<SolutionValue_Max && distanceImage[randomY][randomX]==0.0)
							{
								distance = realization_Original.GetPatternDistance_Euclidean(randomY, randomX, pattern_DS, UnknownBound,MaxElementDifference);
								// is the training pattern satisfied?
								if(distance <= DS_Threshold)
								{
									unknownValue = realization_Original.GetValue(randomY, randomX);
									distance_min = distance;
									break;
								}
								else if (distance < distance_min)
								{
									unknownValue = realization_Original.GetValue(randomY, randomX);
									distance_min = distance;
								}
								distanceImage[randomY][randomX] = distance;
								++randomIndex; 
							}
						}
						
						// record the searching time
						searchingTime_end = System.nanoTime();
						searchingTime = searchingTime + searchingTime_end - searchingTime_start;
						
						distance_print_primary_Mean += distance_min;
						++searchCounter;
						
						if((unknownIndex - simulationPointCounter) > distance_print_Amount)
						{
							System.out.println(distance_print_primary_Mean/(double)searchCounter);
							distance_print_primary_Mean = 0.0;
							searchCounter = 0;
							simulationPointCounter = unknownIndex;
						}
						// estimate a point
						realization.SetValue(unknownY, unknownX, unknownValue);
						++unknownIndex;
					}
				}
			}
			//realization.PrintOut_Color(new String("z_original"));
			realization.PrintOut_txt(new String("z_Antarctica_DS"+realizationIndex+".txt"));
			end = new Date();
			CostTime = (int) (end.getTime()-start.getTime())/1000;
			System.out.println(new String("The program costs "+ CostTime + " seconds so far"));
			
			System.out.println(new String("The searching program costs "+ searchingTime + " nanoseconds so far"));
			System.out.println(new String("The searching program costs "+ searchingTime * 1e-9  + " seconds so far"));
		}
		
		System.out.println(new Date());
		end = new Date();
		CostTime = (int) (end.getTime()-start.getTime())/1000;
		System.out.println(new String("The simulation procedure costs "+ CostTime + " seconds"));
		
		System.out.println(new String("The searching program costs "+ searchingTime + " nanoseconds so far"));
		System.out.println(new String("The searching program costs "+ searchingTime * 1e-9  + " seconds so far"));
		
		System.out.println(new String("The whole procedure have been accomplished !"));
		return;
	}
}

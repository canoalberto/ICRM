package net.sf.jclec.problem.classification.classic.listener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.problem.classification.ClassificationAlgorithm;
import net.sf.jclec.problem.classification.IClassifier;
import net.sf.jclec.problem.classification.classic.ClassicClassificationMetadata;
import net.sf.jclec.problem.classification.classic.ClassicClassificationReporter;
import net.sf.jclec.problem.classification.classic.IClassicClassifier;
import net.sf.jclec.problem.classification.rule.Rule;
import net.sf.jclec.problem.classification.rule.RuleBase;
import net.sf.jclec.problem.util.dataset.AbstractDataset;

/**
 * Classification reporter for rule-based classic classification algorithms
 * 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 * @author Jose M. Luna 
 * @author Alberto Cano 
 * @author Juan Luis Olmo
 */

public class RuleBaseReporter extends ClassicClassificationReporter
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */
	
	private static final long serialVersionUID = -8548482239030974796L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor
	 */
	
	public RuleBaseReporter() 
	{
		super();
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Make the classifier report over the train and test datasets
	 * 
	 * @param algorithm Algorithm
	 */
    protected void doClassificationReport(ClassificationAlgorithm algorithm)
	{
		// Test report name
		String testReportFilename = "TestClassificationReport.txt";
		// Train report name
		String trainReportFilename = "TrainClassificationReport.txt";
		// Test report file
		File testReportFile = new File(reportDirectory, testReportFilename);
		// Train report file
		File trainReportFile = new File(reportDirectory, trainReportFilename);
		// Test file writer
		FileWriter testFile = null;
		// Train file writer
		FileWriter trainFile = null;
		// Number of conditions
		int conditions = 0;
		// Classifier
		IClassifier classifier = algorithm.getClassifier();
		
		int[][] confusionMatrixTrain = ((IClassicClassifier) classifier).getConfusionMatrix(algorithm.getTrainSet());
		int[][] confusionMatrixTest = ((IClassicClassifier) classifier).getConfusionMatrix(algorithm.getTestSet());
		
		int[] numberInstancesTrain = new int[confusionMatrixTrain.length];
		int[] numberInstancesTest = new int[confusionMatrixTest.length];
		int correctedClassifiedTrain = 0, correctedClassifiedTest = 0;
		
		for(int i = 0; i < confusionMatrixTrain.length; i++)
		{
			correctedClassifiedTrain += confusionMatrixTrain[i][i];
			correctedClassifiedTest += confusionMatrixTest[i][i];
			
			for(int j = 0; j < confusionMatrixTrain.length; j++)
			{
				numberInstancesTrain[i] += confusionMatrixTrain[i][j];
				numberInstancesTest[i] += confusionMatrixTest[i][j];
			}
		}
		
		double kappaRateTrain = Kappa(confusionMatrixTrain);
		double kappaRateTest = Kappa(confusionMatrixTest);
		
		double aucTrain = AUC(confusionMatrixTrain);
		double aucTest = AUC(confusionMatrixTest);
		
		double mediaGeoTrain = GeoMean(confusionMatrixTrain);
		double mediaGeoTest = GeoMean(confusionMatrixTest);
		
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df4 = new DecimalFormat("0.0000");
		
		try {
			testReportFile.createNewFile();
			trainReportFile.createNewFile();
			testFile = new FileWriter (testReportFile);
			trainFile = new FileWriter (trainReportFile);
			
			// Dataset metadata
			ClassicClassificationMetadata metadata = (ClassicClassificationMetadata) algorithm.getTrainSet().getMetadata();
			
			// Get the classifier
			List<Rule> classificationRules = ((RuleBase) classifier).getClassificationRules();
			
			// Obtain the number of conditions
			conditions = ((RuleBase) classifier).getConditions();
			
			// Obtain the number of classes
			int numClasses = metadata.numberOfClasses();
			
			// Train data
			trainFile.write("File name: " + ((AbstractDataset) algorithm.getTrainSet()).getFileName());
			trainFile.write("\nRun Time (s): " + (((double)(endTime-initTime)) / 1000.0));
			trainFile.write("\nEvaluation Time (s): " + (algorithm.getEvaluator().getEvaluationTime() / 1000.0));
			trainFile.write("\nNumber of different attributes: " + metadata.numberOfAttributes());
			trainFile.write("\nNumber of rules: " + classificationRules.size());
			trainFile.write("\nNumber of conditions: "+ conditions);
			trainFile.write("\nAverage number of conditions per rule: " + df4.format((double)conditions/((double)classificationRules.size())));
			trainFile.write("\nAccuracy: " + df4.format((correctedClassifiedTrain /  (double) algorithm.getTrainSet().getExamples().size())));
			
			// Write the geometric mean
			trainFile.write("\nGeometric mean: " + df4.format(mediaGeoTrain));
			trainFile.write("\nCohen's Kappa rate: " + df4.format(kappaRateTrain));
			trainFile.write("\nAUC: " + df4.format(aucTrain));
			
			trainFile.write("\n\n#Percentage of correct predictions per class");			
			
			// Test data
			testFile.write("File name: " + ((AbstractDataset) algorithm.getTestSet()).getFileName());
			testFile.write("\nRun Time (s): " + (((double)(endTime-initTime)) / 1000.0));
			testFile.write("\nEvaluation Time (s): " + (algorithm.getEvaluator().getEvaluationTime() / 1000.0));
			testFile.write("\nNumber of different attributes: " + metadata.numberOfAttributes());		
			testFile.write("\nNumber of rules: " + classificationRules.size());
			testFile.write("\nNumber of conditions: "+ conditions);
			testFile.write("\nAverage number of conditions per rule: " + df4.format((double)conditions/((double)classificationRules.size())));
			testFile.write("\nAccuracy: " + df4.format((correctedClassifiedTest /  (double) algorithm.getTestSet().getExamples().size())));
			
			// Write the geometric mean
			testFile.write("\nGeometric mean: " + df4.format(mediaGeoTest));
			testFile.write("\nCohen's Kappa rate: " +  df4.format(kappaRateTest));
			testFile.write("\nAUC: " +  df4.format(aucTest));
			
			testFile.write("\n\n#Percentage of correct predictions per class");

			// Check if the report directory name is in a file
			String aux = "";
			if(getReportDirName().split("/").length>1)
				aux = getReportDirName().split("/")[0]+"/";
			else
				aux = "./";

			// Global report for train
			String nameFileTrain = aux +getGlobalReportName() + "-train.txt";
			File fileTrain = new File(nameFileTrain);
			BufferedWriter bwTrain;
			
			// Global report for test
			String nameFileTest = aux +getGlobalReportName() + "-test.txt";
			File fileTest = new File(nameFileTest);
			BufferedWriter bwTest; 
			
			// If the global report for train exist
			if(fileTrain.exists())
			{
				bwTrain = new BufferedWriter (new FileWriter(nameFileTrain,true));
				bwTrain.write(System.getProperty("line.separator"));
			}
			else
			{
				bwTrain = new BufferedWriter (new FileWriter(nameFileTrain));
				bwTrain.write("Dataset, Accuracy, Cohen's Kappa rate, AUC, geometric mean, number of rules, number of conditions, average number of conditions, number of evaluations, evaluation time, execution time\n");
			}
			
			// If the global report for test exist
			if(fileTest.exists())
			{
				bwTest = new BufferedWriter (new FileWriter(nameFileTest,true));
				bwTest.write(System.getProperty("line.separator"));
			}
			else
			{
				bwTest = new BufferedWriter (new FileWriter(nameFileTest));
				
				bwTest.write("Dataset, Accuracy, Cohen's Kappa rate, AUC, geometric mean, number of rules, number of conditions, average number of conditions, number of evaluations, evaluation time, execution time\n");
			}
			
			//Write the train dataset name
			bwTrain.write(((AbstractDataset) algorithm.getTrainSet()).getFileName() + ",");
			//Write the test dataset name
			bwTest.write(((AbstractDataset) algorithm.getTestSet()).getFileName() + ",");
			//Write the percentage of correct predictions
			bwTrain.write(((correctedClassifiedTrain /  (double) algorithm.getTrainSet().getExamples().size())) + ",");
			
			bwTrain.write(kappaRateTrain + ",");
			bwTrain.write(aucTrain + ",");
			
			for(int i=0; i<numClasses; i++)
			{
				String result = new String();
				
				result = "\n Class " + metadata.getClassAttribute().show(i) + ":";
				if(numberInstancesTrain[i] == 0)
				{
					result += " 100.00%";
				}
				else
				{
					result += " " + df.format((confusionMatrixTrain[i][i] / (double) numberInstancesTrain[i]) * 100) + "%";
				}
		
				trainFile.write(result);
			}
			
			trainFile.write("\n#End percentage of correct predictions per class");

			bwTrain.write(mediaGeoTrain + ",");
			bwTrain.write(classificationRules.size() + ",");
			bwTrain.write(conditions + ",");
			bwTrain.write((double)conditions/((double)classificationRules.size())+",");
			bwTrain.write(algorithm.getEvaluator().getNumberOfEvaluations()+",");
			bwTrain.write((algorithm.getEvaluator().getEvaluationTime() / 1000.0) + ",");
			bwTrain.write((((double)(endTime-initTime)) / 1000.0) + "");
		
			trainFile.write("\n\n#Classifier\n");
			
			trainFile.write(classifier.toString(metadata));
			
			// Write the Percentage of correct predictions
			bwTest.write(((correctedClassifiedTest /  (double) algorithm.getTestSet().getExamples().size())) + ",");
			bwTest.write(kappaRateTest + ",");
			bwTest.write(aucTest + ",");
					
			for(int i=0; i<numClasses; i++)
			{
				String result = new String();
				
				result = "\n Class " +  metadata.getClassAttribute().show(i) +":";
				if(numberInstancesTest[i] == 0)
				{
					result += " 100.00%";
				}
				else
				{
					result += " " + df.format((confusionMatrixTest[i][i] / (double) numberInstancesTest[i]) *100) + "%";
				}
				testFile.write(result);
			}
			
			testFile.write("\n#End percentage of correct predictions per class");

			bwTest.write(mediaGeoTest + ",");		
			bwTest.write(classificationRules.size() + ",");
			bwTest.write(conditions + ",");
			bwTest.write((double)conditions/((double)classificationRules.size()) + ",");
			bwTest.write(algorithm.getEvaluator().getNumberOfEvaluations()+",");
			bwTest.write((algorithm.getEvaluator().getEvaluationTime() / 1000.0) + ",");
			bwTest.write((((double)(endTime-initTime)) / 1000.0) + "");
		
			testFile.write("\n\n#Classifier\n");
			
			// Show classifier			
			testFile.write(classifier.toString(metadata));
			
			// Show confusion matrix
			testFile.write("\n#Test Classification Confusion Matrix\n");
			trainFile.write("\n#Train Classification Confusion Matrix\n");
			
			String[][] trainCF = new String[numClasses+1][numClasses+1];
			String[][] testCF = new String[numClasses+1][numClasses+1];
			
			for(int i = 0; i < numClasses+1; i++)
				for(int j = 0; j < numClasses+1; j++)
				{
					trainCF[i][j] = "";
					testCF[i][j] = "";
				}
			
			trainCF[0][0] = "Actual vs Predicted";
			testCF[0][0] = "Actual vs Predicted";
			
			for(int i = 0; i < numClasses; i++)
			{
				testCF[i+1][0] = testCF[0][i+1] = trainCF[i+1][0] = trainCF[0][i+1] = metadata.getAttribute(metadata.numberOfAttributes()).show(i);
				
				for(int j = 0; j < numClasses; j++)
				{
					trainCF[i+1][j+1] +=  confusionMatrixTrain[i][j];
					testCF[i+1][j+1] += confusionMatrixTest[i][j];
				}
			}
			
			testFile.write(printConfusionMatrix(testCF));
			trainFile.write(printConfusionMatrix(trainCF));
			
			// Close the files
			bwTest.close();
			bwTrain.close();
			testFile.close();
			trainFile.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
    
    /////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
    
    @Override
	public void algorithmTerminated(AlgorithmEvent event) {
	}
}
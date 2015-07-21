public class Neuron 
{
	double neuronValue;
	
	public Neuron()
	{
		neuronValue = 0;
	}
	
	public double sigmoid(double in)
	{		
		neuronValue = 1 / (1 + Math.exp(0 - in));
		
		return neuronValue;
	}
	
	public double getNeuronValue()
	{
		return neuronValue;
	}
	
	public void printNeuronValue()
	{
		System.out.println(neuronValue);
	}
}

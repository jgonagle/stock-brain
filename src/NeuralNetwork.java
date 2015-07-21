import java.util.ArrayList;

public class NeuralNetwork 
{
	ArrayList<Layer> neuralNetwork;
	Matrix networkInputs;
	Matrix finalOutputs;
	Matrix desiredOutputs;
	double learningRate;
	
	public NeuralNetwork(int[] neuralNetFrame, double lR)
	{
		neuralNetwork = new ArrayList<Layer>();
		learningRate = lR;
		
		for (int i = 1; i < neuralNetFrame.length; i++)
		{
			Layer layer = new Layer(neuralNetFrame[i], neuralNetFrame[i - 1]);
			
			neuralNetwork.add(layer);
		}
		
	}
	
	public void forwardPropagate(Matrix in)
	{
		networkInputs = in;
		Matrix inputs = in;
		for (int i = 0; i < neuralNetwork.size(); i++)
		{
			neuralNetwork.get(i).calcNeuronOutput(inputs);
			Matrix outputs = neuralNetwork.get(i).getNeuronsOutput();
			inputs = outputs;

			//neuralNetwork.get(i).printWeightMatrix();
			
			if (i == neuralNetwork.size() - 1)
			{
				finalOutputs = outputs;
			}
		}
	}
	
	public void backwardPropagate(Matrix desiredOutputs)
	{
		Layer layerFinal = neuralNetwork.get(neuralNetwork.size() - 1);
		layerFinal.getFinalDeltas(desiredOutputs);
		
		Layer nextLayer = layerFinal;
		
		for (int i = neuralNetwork.size() - 2; i >= 0; i--)
		{
			Layer layerHidden = neuralNetwork.get(i);
			layerHidden.getHiddenDeltas(nextLayer.getWeightMatrix(), nextLayer.getDeltaMatrix());
			
			nextLayer = layerHidden;
		}
		
		Layer layerBegin = neuralNetwork.get(0);
		layerBegin.setNewWeights(networkInputs, learningRate);
		
		//layerBegin.getWeightMatrix().printMatrix();
		
		Matrix prevLayerOutputs = layerBegin.getNeuronsOutput();
		
		for (int i = 1; i < neuralNetwork.size(); i++)
		{
			Layer layerCurrent = neuralNetwork.get(i);
			layerCurrent.setNewWeights(prevLayerOutputs, learningRate);
			
			prevLayerOutputs = layerCurrent.getNeuronsOutput();
			
			//layerCurrent.getWeightMatrix().printMatrix();
		}
	}
	
	public Matrix getFinalOutputs()
	{
		return finalOutputs;
	}
}

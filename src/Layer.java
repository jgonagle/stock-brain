import java.util.ArrayList;

public class Layer 
{
	ArrayList<Neuron> neuronLayer;
	Matrix weightMatrix;
	Matrix neuronOutput;
	Matrix deltas;
	Matrix biasWeightMatrix;
	Matrix biasDeltas;
	
	final double bias = 1;
		
	public Layer (int curNumNeurons, int prevNumNeurons)
	{
		neuronLayer = new ArrayList<Neuron>();
		
		for (int i = 0; i < curNumNeurons; i++)
		{
			Neuron neuron = new Neuron();
			neuronLayer.add(neuron);
		}
		
		weightMatrix = new Matrix(curNumNeurons, prevNumNeurons);
		weightMatrix.randomize();
		
		biasWeightMatrix = new Matrix(curNumNeurons, 1);
		biasWeightMatrix.randomize();
		
		neuronOutput = new Matrix(curNumNeurons, 1);
		deltas = new Matrix(curNumNeurons, prevNumNeurons);
		biasDeltas = new Matrix(curNumNeurons, 1);
	}
	
	public void calcNeuronOutput(Matrix inputs)
	{
		Matrix layerOutput = weightMatrix.multiply(inputs);
		
		Matrix biasAddition = new Matrix(this.getCurLayerSize(), 1);
		biasAddition.setValue(bias);
		biasAddition = biasAddition.pairwiseMultiply(biasWeightMatrix);
		layerOutput = layerOutput.add(biasAddition);
		
		for (int i = 0; i < neuronLayer.size(); i++)
		{
			neuronOutput.setEntry(i, 0, neuronLayer.get(i).sigmoid(layerOutput.getEntry(i, 0)));
			
			//neuronLayer.get(i).printNeuronValue();
		}
	}
	
	public Matrix getFinalDeltas(Matrix dOutputs)
	{
		Matrix output = neuronOutput.fillColumns(this.getPrevLayerSize());
		
		Matrix ones = new Matrix(this.getCurLayerSize(), this.getPrevLayerSize());
		ones.setValue(1);
		Matrix oneMinusOutput = ones.subtract(output);
		
		Matrix desiredOutputs = dOutputs.fillColumns(this.getPrevLayerSize());
		Matrix desiredMinusOutput = desiredOutputs.subtract(output);
		
		Matrix finalDeltas = output.pairwiseMultiply(oneMinusOutput).pairwiseMultiply(desiredMinusOutput);
		
		for (int i = 0; i < finalDeltas.getRows(); i++)
		{
			biasDeltas.setEntry(i, 0, finalDeltas.getEntry(i, 0));
		}
		
		deltas = finalDeltas;
		
		return finalDeltas;		
	}
	
	public Matrix getHiddenDeltas(Matrix nextWeights, Matrix nextDeltas)
	{
		Matrix output = neuronOutput.fillColumns(this.getPrevLayerSize());
		
		Matrix ones = new Matrix(this.getCurLayerSize(), this.getPrevLayerSize());
		ones.setValue(1);
		Matrix oneMinusOutput = ones.subtract(output);
		
		Matrix weightDeltaSum = nextWeights.transpose().multiply(nextDeltas);
		
		Matrix deltaPrime = output.pairwiseMultiply(oneMinusOutput);
		
		Matrix hiddenDeltas = new Matrix(this.getCurLayerSize(), this.getPrevLayerSize());
		
		for (int i = 0; i < this.getCurLayerSize(); i++)
		{
			for (int j = 0 ; j < this.getPrevLayerSize(); j++)
			{
				hiddenDeltas.setEntry(i, j, (deltaPrime.getEntry(i, j) * weightDeltaSum.getEntry(i, i)));
			}
		}
		
		for (int i = 0; i < hiddenDeltas.getRows(); i++)
		{
			biasDeltas.setEntry(i, 0, hiddenDeltas.getEntry(i, 0));
		}
		
		deltas = hiddenDeltas;
		
		return hiddenDeltas;	
	}
	
	public void setNewWeights(Matrix prevOutputs, double learningRate)
	{
		Matrix alphas = new Matrix(this.getCurLayerSize(), this.getPrevLayerSize());
		alphas.setValue(learningRate);
		Matrix alphasDelta = alphas.pairwiseMultiply(deltas);
		
		Matrix prevLayerOutputs = prevOutputs.transpose().fillRows(this.getCurLayerSize());
		Matrix weightChange = prevLayerOutputs.pairwiseMultiply(alphasDelta);
		
		weightMatrix = weightMatrix.add(weightChange);
		
		Matrix biasAlphas = new Matrix(this.getCurLayerSize(), 1);
		biasAlphas.setValue(learningRate);
		Matrix biasAlphasDelta = biasAlphas.pairwiseMultiply(biasDeltas);
		
		Matrix biasInputs = new Matrix(this.getCurLayerSize(), 1);
		biasInputs.setValue(bias);
		
		Matrix biasWeightChange = biasInputs.pairwiseMultiply(biasAlphasDelta);
		
		biasWeightMatrix = biasWeightMatrix.add(biasWeightChange);
	}

	public int getCurLayerSize()
	{
		return weightMatrix.getRows();
	}
	
	public int getPrevLayerSize()
	{
		return weightMatrix.getColumns();
	}
	
	public Matrix getNeuronsOutput()
	{
		return neuronOutput;
	}
	
	public Matrix getWeightMatrix()
	{
		return weightMatrix;
	}
	
	public Matrix getDeltaMatrix()
	{
		return deltas;
	}
	
	public void printWeightMatrix()
	{
		weightMatrix.printMatrix();
	}
	
	public int getLayerSize()
	{
		return neuronLayer.size();
	}
	
	public Neuron getNeuron(int i)
	{
		return neuronLayer.get(i);
	}
}
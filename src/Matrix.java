import java.util.Random;

public class Matrix 
{
	double[][] matrix;
	
	public Matrix(int rows, int columns)
	{
		matrix = new double[rows][columns];
	}
	
	public void randomize()
	{
		Random generator = new Random();
		
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				this.setEntry(i, j, generator.nextDouble());
			}
		}
	}
	
	public void setValue(double value)
	{
		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[i].length; j++)
			{
				this.setEntry(i, j, value);
			}
		}
	}
	
	public Matrix fillRows(int x)
	{
		Matrix sameRows = new Matrix(x, this.getColumns());
		
		for (int i = 0; i < sameRows.getRows(); i++)
		{
			for (int j = 0; j < sameRows.getColumns(); j++)
			{
				sameRows.setEntry(i, j, this.getEntry(0, j));
			}
		}
		
		return sameRows;
	}
	
	public Matrix fillColumns(int y)
	{
		Matrix sameCols = new Matrix(this.getRows(), y);
		
		for (int i = 0; i < sameCols.getRows(); i++)
		{
			for (int j = 0; j < sameCols.getColumns(); j++)
			{
				sameCols.setEntry(i, j, this.getEntry(i, 0));
			}
		}
		
		return sameCols;
	}
	
	public Matrix transpose()
	{
		Matrix transposedMatrix = new Matrix(this.getColumns(), this.getRows());
		
		for (int i = 0; i < transposedMatrix.getRows(); i++)
		{
			for (int j = 0 ; j < transposedMatrix.getColumns(); j++)
			{
				transposedMatrix.setEntry(i, j, this.getEntry(j, i));
			}
		}
		
		return transposedMatrix;
	}
	
	public Matrix multiply(Matrix b)
	{		
		Matrix c = new Matrix(this.getRows(), b.getColumns());
		
		for (int i = 0; i < c.getRows(); i++)
		{
			for (int j = 0; j < c.getColumns(); j++)
			{
				double sum = 0;
				
				for (int k = 0; k < this.getColumns(); k++)
				{
					sum += this.getEntry(i, k) * b.getEntry(k, j);
				}
				
				c.setEntry(i, j, sum);
			}
		}
		
		return c;
	}
	
	public Matrix pairwiseMultiply(Matrix b)
	{		
		Matrix c = new Matrix(this.getRows(), this.getColumns());
		
		for (int i = 0; i < c.getRows(); i++)
		{
			for (int j = 0; j < c.getColumns(); j++)
			{
				c.setEntry(i, j, (this.getEntry(i, j) * b.getEntry(i, j)));
			}
		}
		
		return c;
	}
	
	public Matrix add(Matrix b)
	{
		Matrix c = new Matrix(this.getRows(), this.getColumns());
		
		for (int i = 0; i < c.getRows(); i++)
		{
			for (int j = 0; j < c.getColumns(); j++)
			{
				c.setEntry(i, j, (this.getEntry(i, j) + b.getEntry(i, j)));
			}
		}
		
		return c;
	}
	
	public Matrix subtract(Matrix b)
	{
		Matrix c = new Matrix(this.getRows(), this.getColumns());
		
		for (int i = 0; i < c.getRows(); i++)
		{
			for (int j = 0; j < c.getColumns(); j++)
			{
				c.setEntry(i, j, (this.getEntry(i, j) - b.getEntry(i, j)));
			}
		}
		
		return c;
	}
	
	public double getEntry(int i, int j)
	{
		return matrix[i][j];
	}
	
	public void setEntry(int i, int j, double val)
	{
		matrix[i][j] = val;
	}
	
	public int getRows()
	{
		return matrix.length;
	}
	
	public int getColumns()
	{
		return matrix[0].length;
	}
	
	public void printMatrix()
	{
		for (int i = 0; i < this.getRows(); i++)
		{
			for (int j = 0; j < this.getColumns(); j++)
			{
				System.out.print(this.getEntry(i, j) + " ");
			}
			
			System.out.println("");
		}
		
		System.out.println("");
	}
}

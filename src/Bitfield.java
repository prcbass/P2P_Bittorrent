
public class Bitfield {

	private boolean[] bitfield;
	private int piecesTotal;
	private int piecesDownloaded;
	private boolean completed;
	
	Bitfield(int piecesTotal)
	{
		completed = false;
		this.piecesTotal = piecesTotal;
		bitfield = new boolean[piecesTotal];
	}
	
	public boolean[] getBitField()
	{
		return this.bitfield;
	}
	
	public void setBit(int index)
	{
		if(bitfield[index] == false)
	   {
			bitfield[index] = true;
			piecesDownloaded++;
			if(piecesDownloaded >= piecesTotal)
			{
				completed = true;
			}
	   }
	}
	
	public void setAllBits()
	{
		for (int i = 0; i < piecesTotal; i++)
		{
			bitfield[i] = true;
		}
		completed = true;
	}
	
	public boolean isCompleted()
	{
		return completed;
	}
    
}

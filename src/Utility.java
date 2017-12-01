import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.*;

public class Utility 
{
	public static byte[] inputStreamToByteArray(InputStream in) throws IOException 
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int read = 0;
        while ((read = in.read(buff, 0, buff.length)) != -1) 
        {
            byteStream.write(buff, 0, read);
        }
        byteStream.flush();
        return byteStream.toByteArray();
	}
	
	public static int byteArrayToInt(byte[] bytes) 
	{
		return ByteBuffer.wrap(bytes).getInt();
	}

	// combines two arrays
    // ex: [1, 2, 3] and [4, 5, 6] become [1, 2, 3, 4, 5, 6]
	public static byte[] combine(byte[] left, byte[] right)
    {
        int leftLen = left.length;
        int rightLen = right.length;
        byte[] res = new byte[leftLen+rightLen];
        System.arraycopy(left, 0, res, 0, leftLen);
        System.arraycopy(right, 0, res, leftLen, rightLen);
        return res;
    }
	
	public static int calculateBitfieldSizeInBytes(int pieceSizeInBytes, int fileSizeInBytes) {
		int numPieces;
		
		if(fileSizeInBytes % pieceSizeInBytes != 0) {
			numPieces = (fileSizeInBytes / pieceSizeInBytes) + 1;
		} else {
			numPieces = fileSizeInBytes / pieceSizeInBytes;
		}
		
		return numPieces % 8 != 0 ? numPieces/8 + 1 : numPieces/8;
	}
	
	//gives you byte array with all 0 bytes
	public static byte[] getEmptyByteArray(int byteFieldSizeInBytes) {
		byte[] empty = new byte[byteFieldSizeInBytes];
		Arrays.fill(empty, (byte) 0);
		return empty;
	}
	
	public static byte[] getFullByteArray(int byteFieldSizeInBytes) {
		byte[] full = new byte[byteFieldSizeInBytes];
		Arrays.fill(full, (byte) 1);
		return full;
	}

	// returns true if the BitSet 'them' has a 1 in an index that 'us' does not
	public static boolean shouldBeInterested(Bitfield us, Bitfield them)
	{
		if (us.length() == 0 || them.length() == 0)
        {
            return them.length() > us.length();
        }

		for (int i = 0; i < us.length(); i++)
		{
			// if any index in the bitfield is different and 'them' has a 1, we are interested
			if (us.get(i) != them.get(i) && them.get(i))
				return true;
		}

		return false;
	}

	// returns an arraylist of all the indeces were 'us' has a 0 and 'them' has a 1
	public static ArrayList<Integer> getRequestPieces(Bitfield us, Bitfield them)
    {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < us.length(); i++)
        {
            if (!us.get(i) && them.get(i))
                res.add(i);
        }

        return res;
    }


    public synchronized static void sendMessage(DataOutputStream output, int type, byte[] payload) throws IOException
    {
        output.writeInt(payload.length + 1);
        output.writeByte(type);
        output.write(payload);
        output.flush();
    }

    public synchronized static void sendMessage(DataOutputStream output, int type) throws IOException
    {
        output.writeInt(1);
        output.writeByte(type);
        output.flush();
    }

    public synchronized static void sendMessage(DataOutputStream output, int type, int payload) throws IOException
	{
		output.writeInt(1 + 4);
		output.writeByte(type);
		output.writeInt(payload);
		output.flush();
	}

	public synchronized static void sendMessage(DataOutputStream output, int type, int index, byte[] payload) throws IOException
    {
        output.writeInt(1 + 4 + payload.length);
        output.writeByte(type);
        output.writeInt(index);
        output.write(payload);
        output.flush();
    }

    public synchronized static byte[] getBytesForPiece(int requestedPieceIndex, int myPeerId) throws IOException
    {
        int pieceSize = Config.getPieceSizeInBytes();
        int pieceCount = calculateBitfieldSizeInBytes(pieceSize, Config.getFileSizeInBytes());

        byte[] piece = new byte[pieceSize];

        int byteLength = pieceSize;

        // open file in read only mode
        String fileName = "./peer_" + myPeerId + "/" + Config.getFileName();
        RandomAccessFile file = new RandomAccessFile(fileName, "r");

        // the last piece in the file is likely to be smaller than the pieceSize
        if (requestedPieceIndex == pieceCount - 1)
        {
            long fileSize = file.length();
            if (fileSize % pieceSize != 0)
                byteLength = (int)(fileSize % (long)pieceSize);
        }

        file.seek(pieceSize * requestedPieceIndex);
        file.readFully(piece, 0, byteLength);
        file.close();

        return piece;
    }

    public synchronized static void writePieceToFile(int pieceIndex, byte[] piece, int myPeerId) throws IOException
    {
        int pieceSize = Config.getPieceSizeInBytes();
        int pieceCount = calculateBitfieldSizeInBytes(pieceSize, Config.getFileSizeInBytes());

        int byteLength = pieceSize;

        // open file in read only mode
        String fileName = "./peer_" + myPeerId + "/" + Config.getFileName();
        RandomAccessFile file = new RandomAccessFile(fileName, "rw");

        // the last piece in the file is likely to be smaller than the pieceSize
        if (pieceIndex == pieceCount - 1)
        {
            long fileSize = file.length();
            if (fileSize % pieceSize != 0)
                byteLength = (int)(fileSize % (long)pieceSize);
        }

        file.seek(pieceSize * pieceIndex);
        file.write(piece, 0, byteLength);
        file.close();

        return;
    }

}

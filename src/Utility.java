import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

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
}

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
        System.out.println(left.toString() + " " + right.toString());
        byte[] res = new byte[left.length + right.length];
        for (int i = 0; i < left.length; i++)
            res[i] = left[i];
        for (int i = left.length; i < right.length; i++)
            res[i] = right[i - left.length];

        System.out.println(res.toString());
        return res;
    }
}

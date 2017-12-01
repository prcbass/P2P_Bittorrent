import java.util.BitSet;
import java.util.Arrays;

public class Bitfield
{
    boolean[] data;

    public Bitfield(int size)
    {
        this.data = new boolean[size];
    }

    public Bitfield(boolean[] data)
    {
        this.data = data;
    }

    public void set(int index, boolean value)
    {
        this.data[index] = value;
    }

    public void set(int fromIndex, int toIndex, boolean value)
    {
        for (int i = fromIndex; i < toIndex; i++)
            this.data[i] = value;
    }

    public int length()
    {
        return data.length;
    }

    public int cardinality()
    {
        int res = 0;
        for (int i = 0; i < this.data.length; i++)
        {
            if (this.data[i])
                res++;
        }

        return res;
    }

    public boolean get(int index)
    {
        return this.data[index];
    }

    public byte[] toByteArray() {
        BitSet bits = new BitSet(this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i]) {
                bits.set(i);
            }
        }

        byte[] bytes = bits.toByteArray();
        if (bytes.length * 8 >= this.data.length) {
            return bytes;
        } else {
            return Arrays.copyOf(bytes, this.data.length / 8 + (this.data.length % 8 == 0 ? 0 : 1));
        }
    }
}
